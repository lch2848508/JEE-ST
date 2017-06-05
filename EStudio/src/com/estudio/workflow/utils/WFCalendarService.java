package com.estudio.workflow.utils;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;
import com.estudio.workflow.base.WFTimeLimit;
import com.estudio.workflow.base.WFTimeUnit;

public class WFCalendarService {

    private class WorkTimeSeq {

        int startTicks;
        int endTicks;
    }

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    Map<Integer, List<String>> year2Days = new HashMap<Integer, List<String>>();
    Map<Integer, List<WorkTimeSeq>> year2TimeSeq = new HashMap<Integer, List<WorkTimeSeq>>();
    Map<Integer, List<Integer>> year2SeqTicks = new HashMap<Integer, List<Integer>>();
    Map<Integer, Integer> year2WorkMinute = new HashMap<Integer, Integer>();

    /**
     * 计算工作日期
     * 
     * @param startDate
     * @param TimeLimit
     * @return
     */
    public Date getDurationDate(final Date startDate, final WFTimeLimit timeLimit) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        final int year = calendar.get(Calendar.YEAR);
        Date result = startDate;
        final WFTimeUnit timeUnit = timeLimit.getUnit();
        if ((timeUnit == WFTimeUnit.DAY) || (timeUnit == WFTimeUnit.HOUR) || (timeUnit == WFTimeUnit.MINUTE) || !year2Days.containsKey(year)) {
            calendar.add(timeUnit == WFTimeUnit.DAY ? Calendar.DATE : timeUnit == WFTimeUnit.HOUR ? Calendar.HOUR : Calendar.MINUTE, timeLimit.getTime());
            result = calendar.getTime();
        } else // startDate = getValidStartDate(calendar, year2Days.get(year));
        // System.out.println(Convert.Date2Str(startDate));
        if (timeUnit == WFTimeUnit.WORKDAY)
            result = getBusinessDateAfterMinute(calendar, year, timeLimit.getTime() * year2WorkMinute.get(year)); // 工作日
        else if (timeUnit == WFTimeUnit.WORKHOUR)
            result = getBusinessDateAfterMinute(calendar, year, timeLimit.getTime() * 60); // 工作小时
        else if (timeUnit == WFTimeUnit.WORKMINUTE)
            result = getBusinessDateAfterMinute(calendar, year, timeLimit.getTime()); // 工作分
        return result;
    }

    /**
     * 获取工作时间 单位分钟
     * 
     * @param startDate
     * @param minute
     * @param holidayDays
     * @param i
     * @return
     */
    private Date getBusinessDateAfterMinute(final Calendar cal, final int year, final int min) {
        Calendar calendar = cal;
        int minute = min;
        final int workingMinute = year2WorkMinute.get(year);
        final List<Integer> seqTicks = year2SeqTicks.get(calendar.get(Calendar.YEAR));

        // 偏离天数
        final int offsetDay = minute / workingMinute;
        for (int i = 0; i < offsetDay; i++) {
            calendar.add(Calendar.DATE, 1);
            calendar = skipHoliday(calendar, year2Days.get(calendar.get(Calendar.YEAR)));
        }

        // 剩余分钟
        minute = minute % workingMinute;

        while (minute != 0) {
            int currentTicks = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
            for (int i = 0; (i < (seqTicks.size() - 1)) && (minute != 0); i++) {
                if ((currentTicks >= seqTicks.get(i)) && (currentTicks <= seqTicks.get(i + 1))) { // 时间满足需要
                    final int offset = Math.min(minute, seqTicks.get(i + 1) - currentTicks);
                    calendar.add(Calendar.MINUTE, offset);
                    currentTicks += offset;
                    if ((i % 2) == 0)
                        minute -= offset;
                }
                if (currentTicks >= seqTicks.get(seqTicks.size() - 1)) { // 到了下班时间
                    calendar.add(Calendar.DATE, 1);
                    calendar = skipHoliday(calendar, year2Days.get(calendar.get(Calendar.YEAR)));
                    calendar.set(Calendar.HOUR_OF_DAY, seqTicks.get(0) / 60);
                    calendar.set(Calendar.MINUTE, seqTicks.get(0) % 60);
                    break;
                }
                
                
            }
        }
        return calendar.getTime();
    }

    /**
     * 跳过节假日
     * 
     * @param calendar
     * @param holidayDays
     */
    private Calendar skipHoliday(final Calendar calendar, final List<String> days) {
        final SimpleDateFormat sf = new SimpleDateFormat("YYYY-M-d");
        String dateStr = sf.format(calendar.getTime());
        while (days.indexOf(dateStr) != -1) {
            calendar.add(Calendar.DATE, 1);
            dateStr = sf.format(calendar.getTime());
        }
        return calendar;
    }

    /**
     * 加载节假日设置
     * 
     * @throws Exception
     */
    public void loadHolidaySetting() throws Exception {
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 5; i++)
            synchronized (year2Days) {
                final JSONObject json = getSetting(year + i);
                // System.out.println(json);
                if (json.getBoolean("r") && json.containsKey("days"))
                    setHolidaySetting(year + i, json.getJSONArray("days"), json.getString("times"));
            }
    }

    /**
     * 设置工作日
     * 
     * @param year
     * @param ms
     * @param times
     */
    public void setHolidaySetting(final int year, final JSONArray monthDays, final String times) {
        synchronized (year2Days) {

            year2Days.remove(year);
            year2TimeSeq.remove(year);
            year2SeqTicks.remove(year);
            year2WorkMinute.remove(year);

            final List<String> daysList = new ArrayList<String>();
            final List<WorkTimeSeq> workTimeSeqs = new ArrayList<WFCalendarService.WorkTimeSeq>();
            final List<Integer> seqTicks = new ArrayList<Integer>();
            year2Days.put(year, daysList);
            year2TimeSeq.put(year, workTimeSeqs);
            year2SeqTicks.put(year, seqTicks);
            int workTicks = 0;

            for (int i = 0; i < monthDays.size(); i++) {
                final int month = i + 1;
                final JSONArray days = monthDays.getJSONArray(i);
                for (int j = 0; j < days.size(); j++) {
                    final int day = days.getInt(j);
                    daysList.add(year + "-" + month + "-" + day);
                }
            }
            final String[] timeSeqs = times.split(";");
            for (final String seq : timeSeqs) {
                final WorkTimeSeq workTimeSeq = new WorkTimeSeq();

                String s = StringUtils.substringBefore(seq, "-");
                int h = Convert.str2Int(StringUtils.substringBefore(s, ":"));
                int m = Convert.str2Int(StringUtils.substringAfter(s, ":"));
                workTimeSeq.startTicks = (h * 60) + m;
                s = StringUtils.substringAfter(seq, "-");
                h = Convert.str2Int(StringUtils.substringBefore(s, ":"));
                m = Convert.str2Int(StringUtils.substringAfter(s, ":"));
                workTimeSeq.endTicks = (h * 60) + m;
                // workTimeSeq.endHour = h;
                // workTimeSeq.endMinute = m;

                seqTicks.add(workTimeSeq.startTicks);
                seqTicks.add(workTimeSeq.endTicks);
                workTicks += (workTimeSeq.endTicks - workTimeSeq.startTicks);

                workTimeSeqs.add(workTimeSeq);
            }
            year2WorkMinute.put(year, workTicks);
        }
    }

    /**
     * 获取节假日设置
     * 
     * @param year
     * @return
     * @throws Exception
     */
    public JSONObject getSetting(final int year) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            final Map<Integer, JSONArray> m2d = new HashMap<Integer, JSONArray>();
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, "select M,D from SYS_WORKFLOW_R_HOLIDAYS where Y=:Y order by M,D");
            cmd.setParam(1, year);
            cmd.executeQuery();
            while (cmd.next()) {
                final int m = cmd.getInt(1);
                final int d = cmd.getInt(2);
                if (m2d.containsKey(m))
                    m2d.get(m).add(d);
                else {
                    final JSONArray array = new JSONArray();
                    array.add(d);
                    m2d.put(m, array);
                    JSONUtils.append(json, "days", array);
                }
            }
            String timeStr = DBHELPER.executeScalarString("select seq from sys_workflow_r_worktime_seq where y=" + year, con);
            if (StringUtils.isEmpty(timeStr))
                timeStr = "00:00-24:00";
            json.put("times", timeStr);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;

    }

    /**
     * 保存节假日设置
     * 
     * @param paramInt
     * @param paramStr
     * @return
     * @throws Exception
     */
    public JSONObject saveSetting(final int year, final String daysStr, final String times) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (currentYear > year)
            json.put("msg", "不能保存" + year + "年的数据!");
        else {
            Connection con = null;
            IDBCommand cmd = null;
            try {
                con = DBHELPER.getConnection();
                final Map<String, Object> params = new HashMap<String, Object>();
                params.put("Y", year);
                DBHELPER.execute("delete from SYS_WORKFLOW_R_HOLIDAYS where Y=:Y", params, con);
                DBHELPER.execute("delete from sys_workflow_r_worktime_seq where Y=:Y", params, con);
                cmd = DBHELPER.getCommand(con, "insert into SYS_WORKFLOW_R_HOLIDAYS (Y,M,D,YMD) values (:Y,:M,:D,:YMD)");
                cmd.setParam("Y", year);
                final JSONArray ms = JSONUtils.parserJSONArray(daysStr);
                final Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < ms.size(); i++) {
                    cmd.setParam("M", i + 1);
                    final JSONArray days = ms.getJSONArray(i);
                    for (int j = 0; j < days.size(); j++) {
                        calendar.clear();
                        calendar.set(year, i, days.getInt(j));
                        cmd.setParam("D", days.getInt(j));
                        cmd.setParam("YMD", calendar.getTime());
                        cmd.execute();
                    }
                }
                params.put("SEQ", times);
                WFCalendarService.getInstance().setHolidaySetting(year, ms, times);
                DBHELPER.execute("insert into sys_workflow_r_worktime_seq(y,seq) values (:Y,:SEQ)", params, con);
                setHolidaySetting(year, ms, times);
                json.put("r", true);
            } finally {
                DBHELPER.closeCommand(cmd);
                DBHELPER.closeConnection(con);
            }
        }
        return json;
    }

    private WFCalendarService() {
    }

    private static WFCalendarService instance = new WFCalendarService();

    public static WFCalendarService getInstance() {
        return instance;
    }

}

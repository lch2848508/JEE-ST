package com.estudio.web.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.CallableStmtParamDefineAndValue;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.ICallableStmtAction;
import com.estudio.intf.db.IDBHelper;

public class QuestionServlet extends BaseServlet {
    private static final long serialVersionUID = -2708175805650877787L;
    protected static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals("getQuestion4EveryDay", operation))
            response.getWriter().println(getQuestion4EveryDay());
        else if (StringUtils.equals("postEveryDayQuestion", operation))
            response.getWriter().println(flagQuestion4EveryDay(getParamInt("score")));
        else if (StringUtils.equals(operation, "monthItemResult")) {
            long uid = getParamLong("uid");
            int score = getParamInt("score");
            String yourAnswer = request.getParameter("yourAnswer");
            response.getWriter().println(monthResultItem(uid, score, yourAnswer));
        } else if (StringUtils.equals(operation, "monthResult")) {
            response.getWriter().println(monthResult(getParamLong("did"), getParamInt("isyear") == 1));
        } else if (StringUtils.equals(operation, "questionformonyh")) {
            boolean isYear = getParamInt("isyear") == 1;
            response.getWriter().println(getQuestion4Month(isYear));
        }
    }

    /**
     * 生成每月试题
     * 
     * @param stgs
     * @return
     * @throws Exception
     */
    private JSONObject getQuestion4Month(Boolean isYear) throws Exception {
        // 存放questionID
        List<Long> questionitemList = new ArrayList<Long>();
        // 存放QuestionItem
        ArrayList<JSONObject> questionInfos = new ArrayList<JSONObject>();

        JSONObject json = new JSONObject();
        JSONObject jsonChoices = new JSONObject();

        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pItemStmt = null;
        CallableStatement cst = null;

        String sqlCheckQuestionMonth = "{call proc_general_month_exam(?,?)}";
        String sqlQuestionMonth = isYear ? "select a.you_answer,\n" + //
                "       a.id u_id,\n" + //
                "       nvl(a.answer, -1) answer,\n" + //
                "       b.id id,\n" + //
                "       b.question question,\n" + //
                "       b.score score,\n" + //
                "       c.caption caption,\n" + //
                "       d.id did\n" + //
                "  from cd_question_4_month_exam_item a,\n" + //
                "       cd_question_item              b,\n" + //
                "       cd_question_category          c,\n" + //
                "       cd_question_4_month_exam      d\n" + //
                " where a.question_id = b.id\n" + //
                "   and c.id = b.p_id\n" + //
                "   and a.p_id = d.id\n" + //
                "   and d.year_and_month = to_number(to_char(sysdate,'YYYY')||'13')\n" + //
                "   and d.user_id = ?" : //
                "select a.you_answer,\n" + //
                        "       a.id u_id,\n" + //
                        "       nvl(a.answer, -1) answer,\n" + //
                        "       b.id id,\n" + //
                        "       b.question question,\n" + //
                        "       b.score score,\n" + //
                        "       c.caption caption,\n" + //
                        "       d.id did\n" + //
                        "  from cd_question_4_month_exam_item a,\n" + //
                        "       cd_question_item              b,\n" + //
                        "       cd_question_category          c,\n" + //
                        "       cd_question_4_month_exam      d\n" + //
                        " where a.question_id = b.id\n" + //
                        "   and c.id = b.p_id\n" + //
                        "   and a.p_id = d.id\n" + //
                        "   and d.year_and_month = to_number(to_char(sysdate,'YYYYMM'))\n" + //
                        "   and d.user_id = ?";//

        try {
            con = DBHELPER.getConnection();
            cst = con.prepareCall(sqlCheckQuestionMonth);
            long userId = GlobalContext.getLoginInfo().getId();
            cst.setLong(1, userId);
            cst.setInt(2, isYear ? 1 : 0);
            cst.execute();
            pstmt = con.prepareStatement(sqlQuestionMonth);
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JSONObject rowJson = new JSONObject();
                rowJson.put("answer", rs.getInt("answer"));
                rowJson.put("id", rs.getInt("id"));
                questionitemList.add(rs.getLong("id"));
                rowJson.put("question", rs.getString("question"));
                rowJson.put("score", rs.getInt("score"));
                rowJson.put("caption", rs.getString("caption"));
                rowJson.put("did", rs.getString("did"));
                rowJson.put("uid", rs.getString("u_id"));
                rowJson.put("yourAnswer", rs.getString("you_answer"));
                questionInfos.add(rowJson);
            }

            String sqlQuestionChoiceMonth = "select id,xxbh,xxms,sfzq from cd_question_selectitem a where P_ID=? order by PX ";
            pItemStmt = con.prepareStatement(sqlQuestionChoiceMonth);
            for (int i = 0; i < questionitemList.size(); i++) {
                pItemStmt.setLong(1, questionitemList.get(i));
                rs = pItemStmt.executeQuery();
                ArrayList<JSONObject> questionChoiceInfos = new ArrayList<JSONObject>();
                while (rs.next()) {
                    JSONObject rowJson = new JSONObject();
                    rowJson.put("id", rs.getInt("id"));
                    rowJson.put("xxbh", rs.getString("xxbh"));
                    rowJson.put("xxms", rs.getString("xxms"));
                    rowJson.put("sfzq", rs.getString("sfzq"));
                    questionChoiceInfos.add(rowJson);
                }
                jsonChoices.put(questionitemList.get(i) + "", questionChoiceInfos);
            }
            String sql = isYear ? "select nvl(score,-1) score from cd_question_4_month_exam t where t.year_and_month = to_number(to_char(sysdate,'YYYY')||'13') and user_id=" + GlobalContext.getLoginInfo().getId() : "select nvl(score,-1) score from cd_question_4_month_exam t where t.year_and_month = to_number(to_char(sysdate,'YYYYMM')) and user_id=" + GlobalContext.getLoginInfo().getId();
            json.put("score", DBHELPER.executeScalarInt(sql, con));
        } finally {
            DBHELPER.closeStatement(cst);
            DBHELPER.closeStatement(pstmt);
            DBHELPER.closeStatement(pItemStmt);
            DBHELPER.closeConnection(con);
        }

        json.put("questionInfos", questionInfos);
        json.put("jsonChoices", jsonChoices);

        return json;
    }

    /**
     * 给题做标记
     * 
     * @param stbh
     * @param stfz
     * @param sfzq
     * @return
     * @throws Exception
     */
    private JSONObject monthResultItem(long uid, int score, String yourAnswer) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        String sqlupdate = "update cd_question_4_month_exam_item set answer = ?, score = ?, you_answer = ? where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBHELPER.getConnection();
            pstmt = conn.prepareStatement(sqlupdate);
            pstmt.setInt(1, score == 100 ? 1 : 0);
            pstmt.setInt(2, score);
            pstmt.setString(3, yourAnswer);
            pstmt.setLong(4, uid);
            pstmt.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(pstmt);
            DBHELPER.closeConnection(conn);
        }
        return json;
    }

    private String monthResult(long did, Boolean isYear) throws Exception {
        Connection conn = null;
        CallableStatement cst = null;
        String monthResult_Insert = "{call proc_month_result_insert(?,?,?)}";
        String zfs = null;
        try {
            conn = DBHELPER.getConnection();
            cst = conn.prepareCall(monthResult_Insert);
            cst.setLong(1, did);
            cst.setInt(2, getParamInt("isyear"));
            cst.registerOutParameter(3, oracle.jdbc.OracleTypes.VARCHAR);
            cst.execute();
            zfs = cst.getString(3);
        } finally {
            DBHELPER.closeStatement(cst);
            DBHELPER.closeConnection(conn);
        }
        return zfs;
    }

    /**
     * 提交答案
     * 
     * @param paramInt
     * @return
     * @throws Exception
     */
    private JSONObject flagQuestion4EveryDay(int score) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("update cd_question_4_everyday set score = ? where user_id = ? and regdate=to_number(to_char(sysdate,'YYYYMMDD'))");
            stmt.setInt(1, score);
            stmt.setLong(2, GlobalContext.getLoginInfo().getId());
            stmt.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    private JSONObject getQuestion4EveryDay() throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final long[] questionIds = new long[1];
            CallableStmtParamDefineAndValue[] params = new CallableStmtParamDefineAndValue[] { new CallableStmtParamDefineAndValue(GlobalContext.getLoginInfo().getId(), DBParamDataType.Long, false), new CallableStmtParamDefineAndValue(-1, DBParamDataType.Long, true) };
            DBHELPER.executeProcedure(con, "proc_general_day_exam", params, new ICallableStmtAction() {
                @Override
                public void processStatement(CallableStatement stmt) throws SQLException {
                    questionIds[0] = stmt.getLong(2);
                }
            });
            if (questionIds[0] != -1) {
                getQuestion(con, questionIds[0], json);
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 生成问题JSON
     * 
     * @param con
     * @param questionId
     * @param json
     * @throws Exception
     */
    private void getQuestion(Connection con, long questionId, JSONObject json) throws Exception {
        ArrayList<JSONObject> questionInfo = new ArrayList<JSONObject>();
        ArrayList<JSONObject> questionChoiceInfo = new ArrayList<JSONObject>();
        Statement pstmt = null;
        ResultSet rs = null;

        String sqlGetQesttion = "select a.id id,a.question question ,a.score score,b.caption caption from cd_question_item a,cd_question_category b where a.p_id = b.id and a.id=" + questionId;
        String sqlQuestionchoice = "select id,xxbh,xxms,sfzq from cd_question_selectitem a where P_ID=" + questionId + " order by PX";

        try {
            pstmt = con.createStatement();
            rs = pstmt.executeQuery(sqlGetQesttion);
            while (rs.next()) {
                JSONObject rowJson = new JSONObject();
                rowJson.put("id", rs.getInt("id"));
                rowJson.put("question", rs.getString("question"));
                rowJson.put("score", rs.getInt("score"));
                rowJson.put("caption", rs.getString("caption"));
                questionInfo.add(rowJson);
            }
            rs = pstmt.executeQuery(sqlQuestionchoice);
            while (rs.next()) {
                JSONObject rowJson = new JSONObject();
                rowJson.put("id", rs.getInt("id"));
                rowJson.put("xxbh", rs.getString("xxbh"));
                rowJson.put("xxms", rs.getString("xxms"));
                rowJson.put("sfzq", rs.getString("sfzq"));
                questionChoiceInfo.add(rowJson);
            }
        } finally {
            DBHELPER.closeStatement(pstmt);
        }
        json.put("questionInfo", questionInfo);
        json.put("questionChoiceInfo", questionChoiceInfo);
    }

}

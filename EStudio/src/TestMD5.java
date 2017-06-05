import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minidev.json.JSONObject;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.SecurityUtils;


public class TestMD5 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("A", "A");
        json.put("B", "B");
        System.out.println(processTagCellValue("≤‚ ‘{D.A}{D.B}{C.X}",json,"D"));
    }
    
    /**
     * 
     * @param cellStr
     * @param record
     * @param tagCategory
     * @return
     */
    private static String processTagCellValue(String cellStr, JSONObject record, String tagCategory) {
        Pattern p = Pattern.compile("\\{[^)]*?\\}");
        Matcher m = p.matcher(cellStr);
        while (m.find()) {
            String tmp = m.group();
            String template = StringUtils.substringBetween(tmp, "{", "}").trim().toUpperCase();
            if (template.startsWith(tagCategory)) {
                String fieldName = StringUtils.substringAfter(template, ".");
                cellStr = StringUtils.replace(cellStr, tmp, record.getString(fieldName));
            }
        }
        return cellStr;
    }
}

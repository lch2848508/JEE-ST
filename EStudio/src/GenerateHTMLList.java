import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class GenerateHTMLList {

    public static void main(final String[] args) throws IOException {
        String str = "E:\\J2EE-Workspaces\\E-Studio\\EStudio\\WebRoot\\ext";
        File file = new File(str);
        File[] dirs = file.listFiles();
        if (dirs != null) {
            for (File f : dirs)
                if (f.isDirectory())
                    generateHtml(f);
        }
    }

    private static void generateHtml(File f) throws IOException {
        String fileName = f.getName() + ".html";
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + //
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + //
                "<head>\n" + //
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + //
                "<title></title>\n" + //
                "<link href='css.css' rel='stylesheet' type='text/css' />" + //
                "</head>\n" + //
                "\n" + //
                "<body>\n<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        File[] files = f.listFiles();
        if (files != null) {
            for (File htmlFile : files) {
                if (htmlFile.isDirectory())
                    continue;
                String content = FileUtils.readFileToString(htmlFile, "utf-8");
                String caption = StringUtils.substringBetween(content, "<H1>", "</H1>");
                String summary = StringUtils.substringBetween(content, "class=summary>", "</DIV>");
                String source = StringUtils.substringBetween(content, "发布时间：", "&nbsp;&nbsp;");
                sb.append("\n" + //
                        "  <tr class='first'>\n" + //
                        "    <td>").append("<a href='" + f.getName() + "/" + htmlFile.getName() + "' target='news'>" + caption + "</a>").append("</td>\n" + //
                        "    <td align='right'>" + source + "</td>\n" + //
                        "  </tr>\n" + //
                        "  <tr class='second'>\n" + //
                        "    <td colspan=\"2\" >" + summary + "</td>\n" + //
                        "  </tr>\n" + //
                        "");
                content = StringUtils.replace(content, StringUtils.substringBetween(content, "<!-- GWD SHARE BEGIN 底部横条-->", "<!-- GWD SHARE END -->"), "");
                FileUtils.writeStringToFile(htmlFile, content, "utf-8");
            }
            sb.append("</table>\n</body></html>");

        }
        FileUtils.writeStringToFile(new File("E:\\J2EE-Workspaces\\E-Studio\\EStudio\\WebRoot\\ext\\" + fileName), sb.toString(), "utf-8");
    }
}

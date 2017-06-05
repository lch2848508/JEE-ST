package com.estudio.impl.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.estudio.define.webclient.form.FormDefine;
import com.estudio.utils.Convert;

public class WinForm2HTMLParser {

    /**
     * ����ռ�ߴ���
     * 
     * @author Administrator
     * 
     */
    private class ControlSize {
        int left;
        int top;
        int width;
        int height;
    }

    private final Map<String, String> control2GridControlType = new HashMap<String, String>();
    {
        control2GridControlType.put("ReadOnly", "ro");
        control2GridControlType.put("TextBox", "ed");
        control2GridControlType.put("ComboBox", "coro");
        control2GridControlType.put("Date", "dhxCalendar");
        control2GridControlType.put("CheckBox", "ch");
        control2GridControlType.put("Memo", "txt");
    }

    /**
     * ����Windows����� ����DOM����HTML����
     * 
     * @param formDefine
     * @param dom
     * @param jsSb
     * @param offsetX
     *            X����ƫ������
     * @param offsetY
     *            Y����ƫ������
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    public String parser(final FormDefine formDefine, final Document dom, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        final StringBuffer htmlSb = new StringBuffer();
        final StringBuffer cssSb = new StringBuffer();
        final Element rootElement = dom.getRootElement();
        final ControlSize formSize = parserForm(htmlSb, rootElement, cssSb, formDefine.getFormId(), jsSb, con);
        formDefine.setFormWidth(formSize.width);
        formDefine.setFormHeight(formSize.height);
        formDefine.setHTML(htmlSb.toString());
        formDefine.setCssStyle(cssSb.toString());
        return htmlSb.toString();
    }

    /**
     * ������
     * 
     * @param sb
     * @param cssSb
     * @param jsSb
     * @param rootElement
     * @param offsetX
     * @param offsetY
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private ControlSize parserForm(final StringBuffer sb, final Element formElement, final StringBuffer cssSb, final long formID, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        cssSb.append("#form_div_").append(formID).append(" *{");
        parserFont(cssSb, formElement);
        cssSb.append("}\n");
        final ControlSize cs = getControlSize(formElement);
        sb.append("<div style=\"");
        parserPosition(sb, formElement, true);
        parserFont(sb, formElement);
        sb.append("\"").append("id=\"form_div_").append(formID).append("\" class=\"div_form_contain\">\n");
        final String color = formElement.attributeValue("Color");
        final List<?> le = formElement.elements();
        for (int i = 0; i < le.size(); i++) {
            final Element ce = (Element) le.get(i);
            parseControl(sb, ce, color, cssSb, jsSb, con);
        }
        sb.append("</div>");

        // if (!StringUtils.isEmpty(formElement.attributeValue("Toolbars")))
        // jsSb.append("FORM_TOOLBARS.push(").append(formElement.attributeValue("Toolbars").replace(".bmp",
        // ".png")).append(");\n");
        // �����¼�
        final List<?> list_attrib = formElement.attributes();
        for (int i = 0; i < list_attrib.size(); i++) {
            final Attribute attrib = (Attribute) list_attrib.get(i);
            if (attrib.getName().startsWith("On") && !StringUtils.isEmpty(attrib.getValue()))
                jsSb.append("registerEventFunction(\"form\",\"").append(formID).append("\",\"").append(attrib.getName()).append("\",\"").append(attrib.getValue()).append("\");\n");
        }
        return cs;
    }

    /**
     * �����ؼ�
     * 
     * @param sb
     * @param parentColor
     * @param cssSb
     * @param jsSb
     * @param ce
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private void parseControl(final StringBuffer sb, final Element controlElement, final String parentColor, final StringBuffer cssSb, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        final String controlType = controlElement.getName();
        if (StringUtils.equals(controlType, "Label"))
            parserLabel(sb, controlElement, parentColor);
        else if (StringUtils.equals(controlType, "DatePick"))
            parserDatePick(sb, controlElement, parentColor);
        else if (StringUtils.equals(controlType, "TextBox"))
            parserTextBox(sb, controlElement, parentColor);
        else if (StringUtils.equals(controlType, "ComboBox"))
            parserComboBox(sb, controlElement, parentColor);
        else if (StringUtils.equals(controlType, "ValidControl"))
            parserValidControl(sb, controlElement);
        else if (StringUtils.equals(controlType, "Memo"))
            parserMemo(sb, controlElement);
        else if (StringUtils.equals(controlType, "CheckBox"))
            parserCheckBox(sb, controlElement);
        else if (StringUtils.equals(controlType, "PageControl"))
            parserPageControl(sb, controlElement, parentColor, cssSb, jsSb, con);
        else if (StringUtils.equals(controlType, "GroupBox"))
            parserGroupBox(sb, controlElement, parentColor, cssSb, jsSb, con);
        else if (StringUtils.equals(controlType, "DBGrid"))
            parserDBGrid(sb, controlElement, parentColor, cssSb, jsSb, con);
        else if (StringUtils.equals(controlType, "FileManager"))
            parserFileManager(sb, controlElement, parentColor, cssSb, jsSb, con);
    }

    /**
     * ���ɸ��������б�
     * 
     * @param sb
     * @param controlElement
     * @param parentColor
     * @param cssSb
     * @param jsSb
     * @param con
     */
    private void parserFileManager(final StringBuffer sb, final Element controlElement, final String parentColor, final StringBuffer cssSb, final StringBuilder jsSb, final Connection con) {
        sb.append("<div class=\"div_attachment_manager\" style=\"");
        parserPosition(sb, controlElement, false);
        sb.append("\" id=\"").append(controlElement.attributeValue("Name")).append("\" name=\"").append(controlElement.attributeValue("Name")).append("\">");
        sb.append("</div>");
        jsSb.append("\nATTACHMENTMANAGER.create(\"").append(controlElement.attributeValue("Name")).append("\",\"").append(controlElement.attributeValue("PTableName")).append("\",\"").append(controlElement.attributeValue("PKeyField")).append("\",\"").append(controlElement.attributeValue("FileExts")).append("\",\"").append(controlElement.attributeValue("MaxFileSize")).append("\",")
                .append(Convert.str2Boolean(controlElement.attributeValue("SaveToDB"))).append(",").append(Convert.str2Boolean(controlElement.attributeValue("BatchUpload"))).append(",\"").append(controlElement.attributeValue("ParentDataSource")).append("\",").append(Convert.str2Boolean(controlElement.attributeValue("ReadOnly"))).append(");\n");
    }

    /**
     * ����CheckBox
     * 
     * @param sb
     * @param element
     */
    private void parserCheckBox(final StringBuffer sb, final Element element) {

        String anchors = element.attributeValue("Anchor"); // Left,Top,Right,Bottom
        if (StringUtils.isEmpty(anchors))
            anchors = "Left,Top";
        final boolean anchorLeft = anchors.contains("Left");
        final boolean anchorTop = anchors.contains("Top");
        final boolean anchorRight = anchors.contains("Right");
        final boolean anchorBottom = anchors.contains("Bottom");
        sb.append("<div class=\"table_corner\" style=\"");
        sb.append("position:absolute;");
        if (anchorLeft)
            sb.append("left:").append(element.attributeValue("Left")).append("px;");
        if (anchorTop)
            sb.append("top:").append(Convert.str2Int(element.attributeValue("Top")) - 2).append("px;");
        if (anchorRight)
            sb.append("right:").append(element.attributeValue("Right")).append("px;");
        if (anchorBottom)
            sb.append("bottom:").append(Convert.str2Int(element.attributeValue("Bottom")) + 2).append("px;");
        if (!anchorLeft || !anchorRight)
            sb.append("width:").append(element.attributeValue("Width")).append("px;");
        if (!anchorTop || !anchorBottom)
            sb.append("Height:").append(element.attributeValue("Height")).append("px;");
        sb.append("\"><input type=\"checkbox\" ");
        parserControlNameFontVisibleEnabledAliasReadOnly(sb, element);
        parserDSBind(sb, element);
        sb.append("\"/></div>");
    }

    /**
     * ���������ı��༭��
     * 
     * @param sb
     * @param controlElement
     */
    private void parserMemo(final StringBuffer sb, final Element controlElement) {
        sb.append("<div class=\"table_corner\" style=\"").append(parserPosition(controlElement, 0, 0)).append("border:0px solid #91A7B4;\">");
        sb.append("<textarea");
        // �����ؼ�����
        parserControlNameFontVisibleEnabledAliasReadOnly(sb, controlElement);
        // �����ؼ����ݰ�
        parserDSBind(sb, controlElement);
        // �����¼�
        parserEvents(sb, controlElement);

        if (Convert.str2Boolean(controlElement.attributeValue("RichEdit")))
            sb.append(" richedit=\"richedit\"");

        sb.append("></textarea>");
        sb.append("</div>");
    }

    /**
     * ��������Grid�б�
     * 
     * @param sb
     * @param controlElement
     * @param parentColor
     * @param cssSb
     * @param jsSb
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private void parserDBGrid(final StringBuffer sb, final Element controlElement, final String parentColor, final StringBuffer cssSb, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        sb.append("<div dbgrid=\"dbgrid\" id=\"").append(controlElement.attributeValue("Name")).append("\" name=\"").append(controlElement.attributeValue("Name")).append("\"></div>");
        sb.append("<div id=\"grid_navigator_").append(controlElement.attributeValue("Name")).append("\" name=\"grid_navigator_").append(controlElement.attributeValue("Name")).append("\"></div>");
        createDBGridJS(cssSb, jsSb, controlElement, con);
    }

    /**
     * ����DBGrid�����javascript����
     * 
     * @param cssSb
     * 
     * @param jsSb
     * @param controlElement
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private void createDBGridJS(final StringBuffer cssSb, final StringBuilder jsSb, final Element element, final Connection con) throws DocumentException, SQLException {

        // Grid Header
        // if (!StringUtils.isEmpty(element.attributeValue("FontName"))) {
        // cssSb.append("#").append(element.attributeValue("Name")).append(" .gridheader {");
        // parserFont(cssSb, element);
        // cssSb.append("}\n");
        // }
        //
        // JSONObject gridJson = new JSONObject();
        // gridJson.put("DataSource", element.attributeValue("DataSource"));
        //
        // boolean isReadOnly =
        // Convert.str2Boolean(element.attributeValue("ReadOnly"));
        // gridJson.put("Width",
        // Convert.str2Int(element.attributeValue("Width")));
        // gridJson.put("Height",
        // Convert.str2Int(element.attributeValue("Height")));
        // gridJson.put("Left",
        // Convert.str2Int(element.attributeValue("Left")));
        // gridJson.put("Top", Convert.str2Int(element.attributeValue("Top")));
        // gridJson.put("Right",
        // Convert.str2Int(element.attributeValue("Right")));
        // gridJson.put("Bottom",
        // Convert.str2Int(element.attributeValue("Bottom")));
        // gridJson.put("Anchor", element.attributeValue("Anchor"));
        // gridJson.put("ReadOnly", isReadOnly);
        // gridJson.put("AddButton",
        // Convert.str2Boolean(element.attributeValue("ShowAddButton")));
        // gridJson.put("DeleteButton",
        // Convert.str2Boolean(element.attributeValue("ShowDeleteButton")));
        // String BindForm = element.attributeValue("BindForm");
        // if (!StringUtils.isEmpty(BindForm)) {
        // ArrayList<BindForm> bindFormList = new ArrayList<BindForm>();
        // Document dom = new SAXReader().read(new
        // ByteArrayInputStream(Convert.str2Bytes(BindForm)));
        // PortalUtils.registerBindForms2List(bindFormList,
        // dom.getRootElement());
        // gridJson.put("BindForm", PortalUtils.bindForms2JSON(bindFormList));
        // String[] formIDs = new String[bindFormList.size()];
        // for (int i = 0; i < formIDs.length; i++)
        // formIDs[i] = bindFormList.get(i).getFormId();
        // long[] gridFormSize =
        // FormDefineService.getInstance().getFormMaxSize(formIDs, con);
        // gridJson.put("BindFormSize", gridFormSize);
        // }
        //
        // List<?> cl = element.elements();
        // boolean hasSpliter = false;
        // long splitAt = 0;
        // for (int i = 0; i < cl.size(); i++) {
        // Element columnElement = (Element) cl.get(i);
        // gridJson.append("Captions", "<div style=\"text-align:" +
        // columnElement.attributeValue("TitleAlignment").toLowerCase() + "\">"
        // + columnElement.attributeValue("Caption") + "</div>");
        // gridJson.append("Widths", columnElement.attributeValue("Width"));
        // gridJson.append("ColumnAlignments",
        // columnElement.attributeValue("Alignment").toLowerCase());
        // gridJson.append("Fields",
        // StringUtils.substringBetween(columnElement.attributeValue("FieldName"),
        // "[", "]"));
        // gridJson.append("ControlTypes", isReadOnly ? "ro" :
        // getGridControlType(columnElement.attributeValue("ControlType")));
        // if (Convert.str2Boolean(columnElement.attributeValue("IsSpliter"))) {
        // hasSpliter = true;
        // splitAt = i + 1;
        // }
        //
        // // ����У�鼰ComboBox����
        // Element validElement = columnElement.element("Valid");
        // JSONObject validJson = new JSONObject();
        // validJson.put("Require",
        // Convert.str2Boolean(validElement.attributeValue("Require")));
        // validJson.put("MaxValue", validElement.attributeValue("MaxValue"));
        // validJson.put("MinValue", validElement.attributeValue("MinValue"));
        // validJson.put("RegEx", validElement.attributeValue("RegEx"));
        // validJson.put("ErrorMsg", validElement.attributeValue("ErrorMsg"));
        // validJson.put("MaxLength",
        // Convert.try2Int(validElement.attributeValue("ErrorMsg"), 0));
        // validJson.put("MinLength",
        // Convert.try2Int(validElement.attributeValue("ErrorMsg"), 0));
        // gridJson.append("Valids", validJson);
        //
        // // ComboBox����
        // if (StringUtils.equals(columnElement.attributeValue("ControlType"),
        // "ComboBox")) {
        // JSONObject comboboxJson = new JSONObject();
        // Element comboBoxElement = columnElement.element("ComboBox");
        // if (Convert.str2Boolean(comboBoxElement.attributeValue("FormDB"))) {
        // comboboxJson.put("FormDB", true);
        // comboboxJson.put("DataSource",
        // comboBoxElement.attributeValue("DataSource"));
        // comboboxJson.put("ValueField",
        // StringUtils.substringBetween(comboBoxElement.attributeValue("ValueField"),
        // "[", "]"));
        // comboboxJson.put("DisplayField",
        // StringUtils.substringBetween(comboBoxElement.attributeValue("DisplayField"),
        // "[", "]"));
        // } else {
        // comboboxJson.put("FormDB", false);
        // Element itemsElement = comboBoxElement.element("Items");
        // if (itemsElement != null) {
        // List<?> itemList = itemsElement.elements();
        // for (int j = 0; j < itemList.size(); j++) {
        // Element itemElement = (Element) itemList.get(j);
        // JSONObject itemJson = new JSONObject();
        // itemJson.put("Display", itemElement.attributeValue("Display"));
        // itemJson.put("Value", itemElement.attributeValue("Value"));
        // comboboxJson.append("Items", itemJson);
        // }
        // } else {
        // comboboxJson.put("Items", new JSONArray());
        // }
        // }
        // gridJson.append("ComboBoxs", comboboxJson);
        // } else {
        // gridJson.append("ComboBoxs", null);
        // }
        // }
        // gridJson.put("HasSpliter", hasSpliter);
        // gridJson.put("SplitAt", splitAt);
        // jsSb.append("GLOBAL_GRID_DEFINE[\"").append(element.attributeValue("Name")).append("\"] = ").append(gridJson.toString()).append(";");

        final List<?> list_attributes = element.attributes();
        for (int i = 0; i < list_attributes.size(); i++) {
            final Attribute attrib = (Attribute) list_attributes.get(i);
            if (attrib.getName().startsWith("On") && !StringUtils.isEmpty(attrib.getValue()))
                jsSb.append("registerEventFunction(\"grid\",\"").append(element.attributeValue("Name")).append("\",\"").append(attrib.getName()).append("\",\"").append(attrib.getValue()).append("\");\n");
        }
    }

    /**
     * ��������GroupBox
     * 
     * @param sb
     * @param controlElement
     * @param parentColor
     * @param cssSb
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private void parserGroupBox(final StringBuffer sb, final Element controlElement, final String parentColor, final StringBuffer cssSb, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        if (!StringUtils.isEmpty(controlElement.attributeValue("FontName"))) {
            cssSb.append("#").append(controlElement.attributeValue("Name")).append(" *{");
            parserFont(cssSb, controlElement);
            cssSb.append("}\n");
        }
        sb.append("<fieldset style=\"");
        parserPosition(sb, controlElement, false);
        sb.append("\" class=\".").append(controlElement.attributeValue("Name")).append("\" id=\"").append(controlElement.attributeValue("Name")).append("\">");
        sb.append("<legend>").append(StringEscapeUtils.escapeHtml3(controlElement.attributeValue("Caption"))).append("</legend>");
        final List<?> le = controlElement.elements();
        for (int i = 0; i < le.size(); i++) {
            final Element ce = (Element) le.get(i);
            parseControl(sb, ce, parentColor, cssSb, jsSb, con);
        }
        sb.append("</fieldset>");
    }

    /**
     * ����TabSheet
     * 
     * @param sb
     * @param controlElement
     * @param cssSb
     * @param parentColor
     * @param pageControlID
     * @param parentColor2
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private void parserTabsheetControl(final StringBuffer sb, final Element controlElement, final String parentColor, final StringBuffer cssSb, final String ID, final String pageControlID, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        if (!StringUtils.isEmpty(controlElement.attributeValue("FontName"))) {
            cssSb.append("#").append(ID).append(" *{");
            parserFont(cssSb, controlElement);
            cssSb.append("}\n");
        }

        sb.append("<div pid=\"").append(pageControlID).append("\" tabsheet=\"tabsheet\" id=\"").append(ID).append("\" name=\"").append(controlElement.attributeValue("Caption")).append("\" width=\"100px\">");
        final List<?> le = controlElement.elements();
        for (int i = 0; i < le.size(); i++) {
            final Element ce = (Element) le.get(i);
            parseControl(sb, ce, parentColor, cssSb, jsSb, con);
        }
        sb.append("</div>");

    }

    /**
     * ����PageControl
     * 
     * @param sb
     * @param controlElement
     * @param cssSb
     * @param parentColor
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private void parserPageControl(final StringBuffer sb, final Element controlElement, final String parentColor, final StringBuffer cssSb, final StringBuilder jsSb, final Connection con) throws DocumentException, SQLException {
        if (!StringUtils.isEmpty(controlElement.attributeValue("FontName"))) {
            cssSb.append("#").append(controlElement.attributeValue("Name")).append(" *{");
            parserFont(cssSb, controlElement);
            cssSb.append("}\n");
        }

        sb.append("<div pagecontrol=\"pagecontrol\" id=\"").append(controlElement.attributeValue("Name")).append("\" class=\"dhtmlxTabBar\" imgpath=\"../images/tabsheet/\" style=\"");
        parserPosition(sb, controlElement, false);
        sb.append("\"");
        sb.append(">");
        final List<?> l = controlElement.elements();
        for (int i = 0; i < l.size(); i++)
            parserTabsheetControl(sb, (Element) l.get(i), parentColor, cssSb, controlElement.attributeValue("Name") + "_" + Integer.toString(i), controlElement.attributeValue("Name"), jsSb, con);
        sb.append("</div>");

    }

    /**
     * ��������У��ؼ�
     * 
     * @param sb
     * @param element
     */
    private void parserValidControl(final StringBuffer sb, final Element element) {
        sb.append("<div class=\"valid_control\" style=\"");
        String anchors = element.attributeValue("Anchor"); // Left,Top,Right,Bottom
        if (StringUtils.isEmpty(anchors))
            anchors = "Left,Top";
        final boolean anchorLeft = anchors.contains("Left");
        final boolean anchorTop = anchors.contains("Top");
        final boolean anchorRight = anchors.contains("Right");
        final boolean anchorBottom = anchors.contains("Bottom");

        sb.append("position:absolute;");
        if (anchorLeft)
            sb.append("left:").append(element.attributeValue("Left")).append("px;");
        if (anchorTop)
            sb.append("top:").append(element.attributeValue("Top")).append("px;");
        if (anchorRight)
            sb.append("right:").append(element.attributeValue("Right")).append("px;");
        if (anchorBottom)
            sb.append("bottom:").append(element.attributeValue("Bottom")).append("px;");
        if (!anchorLeft || !anchorRight)
            sb.append("width:").append(16).append("px;");
        if (!anchorTop || !anchorBottom)
            sb.append("Height:").append(16).append("px;");
        sb.append("\"");

        if (Convert.str2Boolean(element.attributeValue("Require")))
            sb.append(" require=\"require\"");

        if (!StringUtils.isEmpty(element.attributeValue("DataFormat")))
            sb.append(" dataformat=\"").append(StringEscapeUtils.escapeHtml3(element.attributeValue("DataFormat"))).append("\"");

        if (!StringUtils.isEmpty(element.attributeValue("ValidatorMsg")))
            sb.append(" msg=\"").append(StringEscapeUtils.escapeHtml3(element.attributeValue("ValidatorMsg"))).append("\"");

        if (!StringUtils.isEmpty(element.attributeValue("Control")))
            sb.append(" control=\"").append(element.attributeValue("Control")).append("\"");

        if (Convert.str2Int(element.attributeValue("MinLength")) != 0)
            sb.append(" minlength=\"").append(element.attributeValue("MinLength")).append("\"");

        if (Convert.str2Int(element.attributeValue("MaxLength")) != 0)
            sb.append(" maxlength=\"").append(element.attributeValue("MaxLength")).append("\"");

        if (!StringUtils.isEmpty(element.attributeValue("MaxValue")))
            sb.append(" maxvalue=\"").append(StringEscapeUtils.escapeHtml3(element.attributeValue("MaxValue"))).append("\"");

        if (!StringUtils.isEmpty(element.attributeValue("MinValue")))
            sb.append(" minvalue=\"").append(StringEscapeUtils.escapeHtml3(element.attributeValue("MinValue"))).append("\"");

        final String function = element.attributeValue("Function");
        if (!StringUtils.isEmpty(function))
            sb.append(" fun=\"" + function + "\"");

        // Ajax Function û���
        sb.append("></div>");

    }

    /**
     * ����ComboBox
     * 
     * @param sb
     * @param controlElement
     */
    private void parserComboBox(final StringBuffer sb, final Element controlElement, final String parentColor) {
        final StringBuffer csb = new StringBuffer();

        csb.append("<select ");

        // �����ؼ�����
        parserControlNameFontVisibleEnabledAliasReadOnly(csb, controlElement);

        // �����ؼ����ݰ�
        parserDSBind(csb, controlElement);

        // ��������У��
        parserValid(csb, controlElement);

        // �����¼�
        parserEvents(csb, controlElement);

        // ����ComboBox Items
        if (Convert.str2Boolean(controlElement.attributeValue("ItemsFromDB"))) {
            csb.append(" itemfromdb='itemfromdb'");
            final String itemDataSource = controlElement.attributeValue("ItemDataSource");
            final String displayField = controlElement.attributeValue("ItemDisplayField");
            final String valueField = controlElement.attributeValue("ItemValueField");
            if (!StringUtils.isEmpty(itemDataSource)) {
                csb.append(" itemdatasource='").append(itemDataSource).append("'");
                csb.append(" itemdiaplayfield='").append(StringUtils.substringBetween(displayField, "[", "]")).append("'");
                csb.append(" itemvaluefield='").append(StringUtils.substringBetween(valueField, "[", "]")).append("'");
            }
            csb.append(">");
        } else {
            csb.append(">");
            final List<?> itemL = controlElement.elements("Item");
            for (int i = 0; i < itemL.size(); i++) {
                final Element itemElement = (Element) itemL.get(i);
                final String display = itemElement.attributeValue("Display");
                final String value = itemElement.attributeValue("Value");
                csb.append("<option value=\"").append(display).append("\">").append(value).append("</option>");
            }
        }

        csb.append("</select>");

        csb.append("<div style=\"width:100%;height:100%;background:white\"");
        csb.append(" id=\"_div_").append(controlElement.attributeValue("Name")).append("\" name=\"_div_").append(controlElement.attributeValue("Name")).append("\"");
        csb.append("></div>");

        // ����ɫ
        String color = controlElement.attributeValue("Color");
        if (StringUtils.equals(parentColor, color))
            color = "";

        final String addition = "<div id='__" + controlElement.attributeValue("Name") + "__' class='combox_btn'></div>";

        roundControl(true, sb, parserPosition(controlElement, 0, 2), csb.toString(), addition, "", "middle");
    }

    /**
     * ����TextBox
     * 
     * @param sb
     * @param controlElement
     */
    private void parserTextBox(final StringBuffer sb, final Element controlElement, final String parentColor) {
        final boolean hasButton = Convert.str2Boolean(controlElement.attributeValue("HasButton"));
        final StringBuffer csb = new StringBuffer();
        if (Convert.try2Int(controlElement.attributeValue("PasswordChar"), 0) == 0)
            csb.append("<input type='text' ");
        else csb.append("<input type='password' ");

        // �����ؼ�����
        parserControlNameFontVisibleEnabledAliasReadOnly(csb, controlElement);

        // �����ؼ����ݰ�
        parserDSBind(csb, controlElement);

        // ��������У��
        parserValid(csb, controlElement);

        // �����¼�
        parserEvents(csb, controlElement);

        csb.append("/>");

        // ����ɫ
        String bgColor = controlElement.attributeValue("Color");
        if (StringUtils.equals(parentColor, bgColor))
            bgColor = "";
        if (!hasButton)
            roundControl(false, sb, parserPosition(controlElement, 0, 2), csb.toString(), null, bgColor, "middle");
        else {
            String event = controlElement.attributeValue("OnButtonClick");
            if (!StringUtils.isEmpty(event))
                event = " onclick=\"" + event + "($('#" + controlElement.attributeValue("Name") + "'))\"";
            final String addition = "<div id='__" + controlElement.attributeValue("Name") + "__' class='input_btn'" + event + " ></div>";
            roundControl(true, sb, parserPosition(controlElement, 0, 2), csb.toString(), addition, bgColor, "middle");
        }
    }

    /**
     * ����Բ�ǿռ�
     * 
     * @param sb
     * @param stylePositon
     * @param content
     * @param addition
     * @param controlBgColor
     * @param contentVAligh
     */
    private void roundControl(final boolean includeButton, final StringBuffer sb, final String stylePositon, final String content, final String addition, final String controlBgColor, final String contentVAligh) {
        sb.append("<div style=\"").append(stylePositon);
        if (!StringUtils.isEmpty(controlBgColor))
            sb.append(";background-color:").append(controlBgColor);
        sb.append("\" class=\"table_corner\">");
        if (!includeButton) {
            sb.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\" style=\"width:100%;height:100%\">");
            sb.append("<tr>");
            sb.append("<td>").append(content).append("</td>");
            sb.append("</tr>");
            sb.append("</table>\n");
        } else {
            sb.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\" style=\"width:100%;height:100%\">");
            sb.append("<col/><col style=\"width:20px\"/>");
            sb.append("<tr>");
            sb.append("<td>").append(content).append("</td>").append("<td align=\"center\">").append(addition).append("</td>");
            sb.append("</tr>");
            sb.append("</table>\n");
        }
        sb.append("</div>");
    }

    /**
     * ����DatePick
     * 
     * @param sb
     * @param controlElement
     */
    private void parserDatePick(final StringBuffer sb, final Element controlElement, final String parentColor) {
        final StringBuffer csb = new StringBuffer();
        csb.append("<input type='text' ");

        // �����ؼ�����
        parserControlNameFontVisibleEnabledAliasReadOnly(csb, controlElement);

        // �����ؼ����ݰ�
        parserDSBind(csb, controlElement);

        // ��������У��
        parserValid(csb, controlElement);

        // �����¼�
        parserEvents(csb, controlElement);

        // if (!Convert.str2Boolean(controlElement.attributeValue("ReadOnly")))
        // csb.append(" readonly=\"readonly\"");

        csb.append("/>");

        // ����ɫ
        String color = controlElement.attributeValue("Color");
        if (StringUtils.equals(parentColor, color))
            color = "";

        String addition = "";
        if (Convert.str2Boolean(controlElement.attributeValue("IncludeTime")))
            addition = "<div id='__" + controlElement.attributeValue("Name") + "__' onclick=\"showCalendar('" + controlElement.attributeValue("Name") + "',true,this)\"  class='cal_btn'></div>";
        else addition = "<div id='__" + controlElement.attributeValue("Name") + "__' onclick=\"showCalendar('" + controlElement.attributeValue("Name") + "',false,this)\"  class='cal_btn'></div>";

        roundControl(true, sb, parserPosition(controlElement, 0, 2), csb.toString(), addition, color, "middle");

    }

    /**
     * ���� Label
     * 
     * @param sb
     * @param controlElement
     * @param parentColor
     */
    private void parserLabel(final StringBuffer sb, final Element controlElement, final String parentColor) {
        sb.append("<label style=\"");
        parserPosition(sb, controlElement, false);
        parserFont(sb, controlElement);
        sb.append("line-height:").append(controlElement.attributeValue("Height")).append("px;");
        sb.append("\">");
        sb.append(StringEscapeUtils.escapeHtml3(controlElement.attributeValue("Caption")));
        sb.append("</label>\n");
    }

    /**
     * �õ��ؼ��ߴ�
     * 
     * @param element
     * @return
     */
    private ControlSize getControlSize(final Element element) {
        final ControlSize size = new ControlSize();
        size.left = Integer.parseInt(element.attributeValue("Left"));
        size.top = Integer.parseInt(element.attributeValue("Top"));
        size.width = Integer.parseInt(element.attributeValue("Width"));
        size.height = Integer.parseInt(element.attributeValue("Height"));
        return size;
    }

    /**
     * �����ؼ����� ���� �ɼ��� Enabled Title ReadOnly
     * 
     * @param element
     * @param sb
     */
    private void parserControlNameFontVisibleEnabledAliasReadOnly(final StringBuffer sb, final Element element) {
        // ����
        sb.append(" id='").append(element.attributeValue("Name")).append("' name='").append(element.attributeValue("Name")).append("'");
        // ����
        sb.append(" style=\"");
        parserFont(sb, element);
        // �ɼ���
        if (!Convert.str2Boolean(element.attributeValue("Visible")))
            sb.append("display:none;");
        sb.append("\"");

        // ����
        final String alias = element.attributeValue("Alias");
        if (!StringUtils.isEmpty(alias))
            sb.append(" title=\"").append(StringEscapeUtils.escapeXml(alias)).append("\"");

        // ReadOnly
        if (Convert.str2Boolean(element.attributeValue("ReadOnly")))
            sb.append(" readonly=\"readonly\"");
    }

    /**
     * ����������Ϣ
     * 
     * @param sb
     * @param formElement
     */
    private void parserFont(final StringBuffer sb, final Element element) {
        final String fontName = element.attributeValue("FontName");
        if (!StringUtils.isEmpty(fontName)) {
            sb.append("font-family:'").append(fontName).append("';");
            sb.append("font-size:").append(element.attributeValue("FontSize")).append("pt;");
            sb.append("color:").append(element.attributeValue("FontColor")).append(";");
            if (Convert.str2Boolean(element.attributeValue("FontBold")))
                sb.append("font-weight:bold;");
            if (Convert.str2Boolean(element.attributeValue("FontItalic")))
                sb.append("text-decoration:underline;");
            if (Convert.str2Boolean(element.attributeValue("FontUnderline")))
                sb.append("font-style:italic;");
        }
    }

    /**
     * �����ռ�λ��
     * 
     * @param sb
     * @param element
     * @param isForm
     */
    private void parserPosition(final StringBuffer sb, final Element element, final boolean isForm) {
        String anchors = element.attributeValue("Anchor"); // Left,Top,Right,Bottom
        if (StringUtils.isEmpty(anchors))
            anchors = "Left,Top";

        final boolean anchorLeft = anchors.contains("Left");
        final boolean anchorTop = anchors.contains("Top");
        final boolean anchorRight = anchors.contains("Right");
        final boolean anchorBottom = anchors.contains("Bottom");

        sb.append("position:absolute;");
        if (isForm) {
            if (anchorLeft)
                sb.append("left:").append("0px;");
            if (anchorTop)
                sb.append("top:").append("0px;");
            if (anchorRight)
                sb.append("right:").append("0px;");
            if (anchorBottom)
                sb.append("bottom:").append("0px;");
            if (!anchorLeft || !anchorRight)
                sb.append("width:").append(element.attributeValue("Width")).append("px;");
            if (!anchorTop || !anchorBottom)
                sb.append("Height:").append(element.attributeValue("Height")).append("px;");
        } else {
            if (anchorLeft)
                sb.append("left:").append(element.attributeValue("Left")).append("px;");
            if (anchorTop)
                sb.append("top:").append(element.attributeValue("Top")).append("px;");
            if (anchorRight)
                sb.append("right:").append(element.attributeValue("Right")).append("px;");
            if (anchorBottom)
                sb.append("bottom:").append(element.attributeValue("Bottom")).append("px;");
            if (!anchorLeft || !anchorRight)
                sb.append("width:").append(element.attributeValue("Width")).append("px;");
            if (!anchorTop || !anchorBottom)
                sb.append("Height:").append(element.attributeValue("Height")).append("px;");
        }
    }

    /**
     * �����ؼ�λ��
     * 
     * @param element
     * @param offsetX
     * @param offsetY
     * @return
     */
    private String parserPosition(final Element element, final long offsetX, final long offsetY) {
        final StringBuilder sb = new StringBuilder();
        String anchors = element.attributeValue("Anchor"); // Left,Top,Right,Bottom
        if (StringUtils.isEmpty(anchors))
            anchors = "Left,Top";

        final boolean anchorLeft = anchors.contains("Left");
        final boolean anchorTop = anchors.contains("Top");
        final boolean anchorRight = anchors.contains("Right");
        final boolean anchorBottom = anchors.contains("Bottom");

        sb.append("position:absolute;");
        if (anchorLeft)
            sb.append("left:").append(Integer.parseInt(element.attributeValue("Left")) - offsetX).append("px;");
        if (anchorTop)
            sb.append("top:").append(Integer.parseInt(element.attributeValue("Top")) - offsetY).append("px;");
        if (anchorRight)
            sb.append("right:").append(Integer.parseInt(element.attributeValue("Right")) + offsetX).append("px;");
        if (anchorBottom)
            sb.append("bottom:").append(Integer.parseInt(element.attributeValue("Bottom")) + offsetY).append("px;");
        if (!anchorLeft || !anchorRight)
            sb.append("width:").append(element.attributeValue("Width")).append("px;");
        if (!anchorTop || !anchorBottom)
            sb.append("Height:").append(element.attributeValue("Height")).append("px;");
        return sb.toString();
    }

    /**
     * �������ݰ���
     * 
     * @param csb
     * @param controlElement
     */
    private void parserDSBind(final StringBuffer sb, final Element element) {

        final String dataSource = element.attributeValue("DataSource");
        if (!StringUtils.isEmpty(dataSource)) {
            sb.append(" datasource='").append(dataSource).append("'");
            final String fieldName = element.attributeValue("FieldName");
            if (!StringUtils.isEmpty(fieldName))
                sb.append(" fieldname='").append(StringUtils.substringBetween(fieldName, "[", "]")).append("'");
        }

        final String initDataSource = element.attributeValue("InitDataSource");
        if (!StringUtils.isEmpty(initDataSource)) {
            sb.append(" initdatasource='").append(initDataSource).append("'");
            final String initFieldName = element.attributeValue("InitFieldName");
            if (!StringUtils.isEmpty(initFieldName))
                sb.append(" initfieldname='").append(StringUtils.substringBetween(initFieldName, "[", "]")).append("'");
        }

    }

    /**
     * ��������У�鲿�ִ���
     * 
     * @param csb
     * @param controlElement
     */
    private void parserValid(final StringBuffer sb, final Element element) {
        if (Convert.str2Boolean(element.attributeValue("Require")))
            sb.append(" require='require'");
        final String regexType = element.attributeValue("RegexType");
        final String regex = element.attributeValue("Regex");
        final String errorMsg = element.attributeValue("ErrorMsg");
        if (!StringUtils.isEmpty(regexType))
            sb.append(" regextype='").append(regexType).append("'");
        if (!StringUtils.isEmpty(regex))
            sb.append(" regex='").append(regex).append("'");
        if (!StringUtils.isEmpty(errorMsg))
            sb.append(" errormsg='").append(errorMsg).append("'");
    }

    // �����ؼ��¼�
    private void parserEvents(final StringBuffer csb, final Element controlElement) {
        for (int i = 0; i < controlElement.attributeCount(); i++) {
            final String attribName = controlElement.attribute(i).getName();
            if (StringUtils.startsWith(attribName, "On")) {
                final String attribValue = controlElement.attribute(i).getValue();
                if (!StringUtils.isEmpty(attribValue))
                    csb.append(" ").append(attribName.toLowerCase()).append("=\"").append(attribValue).append("(").append("this)\"");
            }
        }
    }

    private static final WinForm2HTMLParser INSTANCE = new WinForm2HTMLParser();

    public static WinForm2HTMLParser getInstance() {
        return INSTANCE;
    }
}

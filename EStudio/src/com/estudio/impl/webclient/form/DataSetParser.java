package com.estudio.impl.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.define.webclient.form.DesignDataSource;
import com.estudio.define.webclient.form.DesignDataSourceCommand;
import com.estudio.define.webclient.form.FormDefine;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.utils.Convert;

public final class DataSetParser {

    /**
     * 根据DataSource定义生成DataSource集
     * 
     * @param dsDOM
     * @throws SQLException
     *             , DBException
     */
    public void parser(final FormDefine formDefine, final Document dsDOM, final Connection con) throws Exception {
        final List<?> datasetEL = dsDOM.getRootElement().elements("DataSet");
        for (int i = 0; i < datasetEL.size(); i++) {
            final Element datasetElement = (Element) datasetEL.get(i);
            final String datasetName = datasetElement.attributeValue("Name");
            final String datasetLabel = datasetElement.attributeValue("Alias");
            final String cacheKey = datasetElement.attributeValue("CacheKey");
            final String formCaption = formDefine.getFormName();
            final boolean clientCache = Convert.obj2Boolean(datasetElement.attributeValue("ClientCache"));
            final DesignDataSource dataset = new DesignDataSource(datasetName, datasetLabel, i == 0, Convert.str2Boolean(datasetElement.attributeValue("ASyncLoad")), formDefine.getFormId(), formCaption, cacheKey, clientCache);
            formDefine.registerDataSet(dataset);
            parserDataSet(dataset, datasetElement, con);
        }
    }

    /**
     * 根据DataSet XML节点定义解析内容
     * 
     * @param dataset
     * @param datasetElement
     * @param con
     * @throws SQLException
     *             , DBException
     */
    private void parserDataSet(final DesignDataSource dataset, final Element datasetElement, final Connection con) throws Exception {
        final boolean datasetIsReadOnly = Convert.str2Boolean(datasetElement.attributeValue("ReadOnly"));
        dataset.setReadOnly(datasetIsReadOnly);
        dataset.setAppendNullRecord(Convert.str2Boolean(datasetElement.attributeValue("AppendNullRecord")));
        dataset.setFormDataSet(Convert.str2Boolean(datasetElement.attributeValue("IsFormDataSource"), true));
        dataset.setForceExecute(Convert.str2Boolean(datasetElement.attributeValue("IsForceExecute"), false));
        parserFields(dataset, datasetElement);
        parsetCommand(dataset.getSelect(), datasetElement.element("SelectCommand"), con, true);
        if(null!=dataset.getPrimaryField()){
        	dataset.getSelect().setKeyFieldName(dataset.getPrimaryField().getName());
        }else{
        	dataset.getSelect().setKeyFieldName(null);
        }
//        dataset.getSelect().setKeyFieldName(dataset.getPrimaryField().getName());
        if (dataset.getFieldCount() != 0)
            dataset.getSelect().initPageAndCountCmd();

        if (!datasetIsReadOnly) {
            parsetCommand(dataset.getInsert(), datasetElement.element("InsertCommand"), con, false);
            parsetCommand(dataset.getUpdate(), datasetElement.element("UpdateCommand"), con, false);
            parsetCommand(dataset.getDelete(), datasetElement.element("DeleteCommand"), con, false);
        }
        dataset.setCacheLevel(Convert.try2Int(datasetElement.attributeValue("CacheLevel"), 0));
    }

    /**
     * 解析Command
     * 
     * @param select
     * @param element
     * @throws SQLException
     *             , DBException
     */
    private void parsetCommand(final DesignDataSourceCommand command, final Element element, final Connection con, final boolean isSelected) throws Exception {
        command.setSql(element.elementText("SQL"));
        final List<?> pl = element.element("Params").elements();
        for (int i = 0; i < pl.size(); i++) {
            final Element pe = (Element) pl.get(i);
            final String pName = pe.attributeValue("Name");
            final String pLabel = pe.attributeValue("Caption");
            final int pDataType = Convert.try2Int(pe.attributeValue("DataType"), 0);
            final String pInitDataSource = pe.attributeValue("InitDataSource");
            final String pInitField = StringUtils.substringBetween(pe.attributeValue("Field"), "[", "]");
            final String pControlType = pe.attributeValue("ControlType");
            final String pControlAddition = pe.attributeValue("ControlAddition");
            final String pControlDefaultValue = pe.attributeValue("DefaultValue");
            final String pFirstInitDataSoure = pe.attributeValue("FirstInitDataSource");
            final String pFirstInitField = StringUtils.substringBetween(pe.attributeValue("FirstField"), "[", "]");
            int controlWidth = Convert.try2Int(pe.attributeValue("ControlWidth"), 80);
            int controlPos = Convert.try2Int(pe.attributeValue("ControlPos"), 0);
            final SQLParam4Form param = new SQLParam4Form(pName, pLabel, DBParamDataType.fromInt(pDataType), pInitDataSource, pInitField, pControlType, pControlAddition, pControlDefaultValue, pFirstInitDataSoure, pFirstInitField, StringUtils.equals("True", pe.attributeValue("WholeWord")), controlWidth, controlPos);
            command.addParam(param);
        }
        command.initCommand(con);
    }

    /**
     * 解析字段列表
     * 
     * @param dataset
     * @param datasetElement
     */
    private void parserFields(final DesignDataSource dataset, final Element datasetElement) {
        final Element fieldsElement = datasetElement.element("Fields");
        final List<?> fieldsL = fieldsElement.elements("Field");
        for (int i = 0; i < fieldsL.size(); i++) {
            final Element fieldElement = (Element) fieldsL.get(i);
            dataset.registerField(fieldElement.attributeValue("Name"), fieldElement.attributeValue("Label"), Convert.str2Boolean(fieldElement.attributeValue("IsPrimary")), Convert.str2Boolean(fieldElement.attributeValue("IsGroup")), Convert.str2Boolean(fieldElement.attributeValue("IsSplit")), fieldElement.attributeValue("ColumnWidth"));
        }
    }

    private DataSetParser() {

    }

    private static final DataSetParser INSTANCE = new DataSetParser();

    public static DataSetParser getInstance() {
        return INSTANCE;
    }
}

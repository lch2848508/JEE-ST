package com.estudio.context;

import com.estudio.intf.db.IDBConfig;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.db.IDBDiagramService;
import com.estudio.intf.design.db.IDBEntryService;
import com.estudio.intf.design.objects.IObjectComboBoxTemplateService;
import com.estudio.intf.design.objects.IObjectFormService;
import com.estudio.intf.design.objects.IObjectQueryService;
import com.estudio.intf.design.objects.IObjectReportService;
import com.estudio.intf.design.objects.IObjectTreeService;
import com.estudio.intf.design.objects.IObjectWorkFlowService;
import com.estudio.intf.design.portal.IPortalGroupService;
import com.estudio.intf.design.portal.IPortalItemService;
import com.estudio.intf.design.portal.IPortalRightService;
import com.estudio.intf.design.user.IDepartmentService;
import com.estudio.intf.design.user.IRoleService;
import com.estudio.intf.design.user.IUserInfoService;
import com.estudio.intf.design.user.IUserManagerService;
import com.estudio.intf.design.utils.ICodeAssistService;
import com.estudio.intf.design.utils.ISQLParserService;
import com.estudio.intf.design.utils.IVersionService;
import com.estudio.intf.web.sercure.IClientLoginService;
import com.estudio.intf.webclient.form.IFormDefineService;
import com.estudio.intf.webclient.form.IPortal4ClientGridDefineService;
import com.estudio.intf.webclient.form.IPortal4ClientService;
import com.estudio.intf.webclient.report.IReportDefineService;
import com.estudio.intf.webclient.utils.IAttachmentService;
import com.estudio.intf.webclient.utils.IPictureService;
import com.estudio.workflow.storage.IWFStorage;

public final class RuntimeContext {

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 数据库部分
    private static IDBHelper dbHelper = null;

    // 用户登录
    private static IClientLoginService clientLoginService = null;

    // 辅助类 版本控制
    private static IVersionService versionService = null;
    private static ICodeAssistService codeAssistService = null;
    private static ISQLParserService sqlParserService = null;

    // 用户管理部分
    private static IDepartmentService departmentService = null;
    private static IRoleService roleService = null;
    private static IUserInfoService userInfoService = null;
    private static IUserManagerService userManagerService = null;

    // 数据库设计部分
    private static IDBEntryService dbEntryService = null;
    private static IDBDiagramService dbDiagramService = null;

    // 栏目设计部分
    private static IPortalGroupService portalGroupService = null;
    private static IPortalRightService portalRightService = null;
    private static IPortalItemService portalItemService = null;

    // 表单 报表 工作流设计器
    private static IObjectComboBoxTemplateService objectComboboxTemplateService = null;
    private static IObjectFormService objectFormService = null;
    private static IObjectReportService objectReportService = null;
    private static IObjectTreeService objectTreeService = null;
    private static IReportDefineService reportDefineService = null;
    private static IFormDefineService formDefineService = null;
    private static IObjectWorkFlowService objectWorkFlowService = null;
    private static IObjectQueryService objectQueryService = null;

    // WebClient 服务
    private static IPortal4ClientService portal4ClientService = null; // 栏目服务
    private static IPortal4ClientGridDefineService portal4ClientGridDefineService = null;

    // 杂项服务
    private static IAttachmentService attachmentService = null;
    private static IPictureService pictureService = null;
    private static IDBConfig dbConfig = null;

    // 工作流服务
    private static IWFStorage wfStorage = null;

    // 临时目录
    private static String appTempDir = "";

    // /////////////////////////////////////////////////////////////////////////////////////////

    public static IWFStorage getWfStorage() {
        return wfStorage;
    }

    public static void setWfStorage(final IWFStorage wfStorage) {
        RuntimeContext.wfStorage = wfStorage;
    }

    public static IPictureService getPictureService() {
        return pictureService;
    }

    public static void setPictureService(final IPictureService pictureService) {
        RuntimeContext.pictureService = pictureService;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static IFormDefineService getFormDefineService() {
        return formDefineService;
    }

    public static void setFormDefineService(final IFormDefineService formDefineService) {
        RuntimeContext.formDefineService = formDefineService;
    }

    public static IReportDefineService getReportDefineService() {
        return reportDefineService;
    }

    public static void setReportDefineService(final IReportDefineService reportTemplateService) {
        RuntimeContext.reportDefineService = reportTemplateService;
    }

    public static IPortalItemService getPortalItemService() {
        return portalItemService;
    }

    public static void setPortalItemService(final IPortalItemService portalItemService) {
        RuntimeContext.portalItemService = portalItemService;
    }

    public static IPortalRightService getPortalRightService() {
        return portalRightService;
    }

    public static void setPortalRightService(final IPortalRightService portalRightService) {
        RuntimeContext.portalRightService = portalRightService;
    }

    // 获取栏目设计服务
    public static IPortalGroupService getPortalGroupService() {
        return portalGroupService;
    }

    public static void setPortalGroupService(final IPortalGroupService portalGroupService) {
        RuntimeContext.portalGroupService = portalGroupService;
    }

    /**
     * 获取用户登录服务
     * 
     * @return
     */
    public static IClientLoginService getClientLoginService() {
        return clientLoginService;
    }

    /**
     * 设置用户登录服务
     * 
     * @param clientLogin
     */
    public static void setClientLoginService(final IClientLoginService clientLogin) {
        RuntimeContext.clientLoginService = clientLogin;
    }

    /**
     * 获取版本服务
     * 
     * @return
     */
    public static IVersionService getVersionService() {
        return versionService;
    }

    /**
     * 设置版本服务
     * 
     * @param versionService
     */
    public static void setVersionService(final IVersionService versionService) {
        RuntimeContext.versionService = versionService;
    }

    /**
     * 获取部门服务
     * 
     * @return
     */
    public static IDepartmentService getDepartmentService() {
        return departmentService;
    }

    /**
     * 设置部门服务
     * 
     * @param departmentService
     */
    public static void setDepartmentService(final IDepartmentService departmentService) {
        RuntimeContext.departmentService = departmentService;
    }

    /**
     * 获取角色服务
     * 
     * @return
     */
    public static IRoleService getRoleService() {
        return roleService;
    }

    /**
     * 设置角色服务
     * 
     * @param roleService
     */
    public static void setRoleService(final IRoleService roleService) {
        RuntimeContext.roleService = roleService;
    }

    /**
     * 获取用户信息服务
     * 
     * @return
     */
    public static IUserInfoService getUserInfoService() {
        return userInfoService;
    }

    /**
     * 设置用户信息服务
     * 
     * @param userInfoService
     */
    public static void setUserInfoService(final IUserInfoService userInfoService) {
        RuntimeContext.userInfoService = userInfoService;
    }

    /**
     * 得到用户管理服务
     * 
     * @return
     */
    public static IUserManagerService getUserManagerService() {
        return userManagerService;
    }

    /**
     * 设置用户管理服务
     * 
     * @param userManagerService
     */
    public static void setUserManagerService(final IUserManagerService userManagerService) {
        RuntimeContext.userManagerService = userManagerService;
    }

    /**
     * 获取表结构设计服务
     * 
     * @return
     */
    public static IDBEntryService getDbEntryService() {
        return dbEntryService;
    }

    /**
     * 设置表结构设计服务
     * 
     * @param dbEntryService
     */
    public static void setDbEntryService(final IDBEntryService dbEntryService) {
        RuntimeContext.dbEntryService = dbEntryService;
    }

    /**
     * 获取数据库模型设计服务
     * 
     * @return
     */
    public static IDBDiagramService getDbDiagramService() {
        return dbDiagramService;
    }

    /**
     * 设置数据库模型设计服务
     * 
     * @param dbDiagramService
     */
    public static void setDbDiagramService(final IDBDiagramService dbDiagramService) {
        RuntimeContext.dbDiagramService = dbDiagramService;
    }

    /**
     * 获取数据库操作类
     * 
     * @return
     */
    public static IDBHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * 设置数据库操作类
     * 
     * @param dbHelper
     */
    public static void setDbHelper(final IDBHelper dbHelper) {
        RuntimeContext.dbHelper = dbHelper;
    }

    public static ICodeAssistService getCodeAssistService() {
        return codeAssistService;
    }

    public static void setCodeAssistService(final ICodeAssistService codeAssistService) {
        RuntimeContext.codeAssistService = codeAssistService;
    }

    public static ISQLParserService getSqlParserService() {
        return sqlParserService;
    }

    public static void setSqlParserService(final ISQLParserService sqlParserService) {
        RuntimeContext.sqlParserService = sqlParserService;
    }

    public static IObjectComboBoxTemplateService getObjectComboboxTemplateService() {
        return objectComboboxTemplateService;
    }

    public static void setObjectComboboxTemplateService(final IObjectComboBoxTemplateService objectComboboxTemplateService) {
        RuntimeContext.objectComboboxTemplateService = objectComboboxTemplateService;
    }

    public static IObjectFormService getObjectFormService() {
        return objectFormService;
    }

    public static void setObjectFormService(final IObjectFormService objectFormService) {
        RuntimeContext.objectFormService = objectFormService;
    }

    public static IObjectReportService getObjectReportService() {
        return objectReportService;
    }

    public static void setObjectReportService(final IObjectReportService objectReportService) {
        RuntimeContext.objectReportService = objectReportService;
    }

    public static IObjectTreeService getObjectTreeService() {
        return objectTreeService;
    }

    public static void setObjectTreeService(final IObjectTreeService objectTreeService) {
        RuntimeContext.objectTreeService = objectTreeService;
    }

    public static IPortal4ClientService getPortal4ClientService() {
        return portal4ClientService;
    }

    public static void setPortal4ClientService(final IPortal4ClientService portal4ClientService) {
        RuntimeContext.portal4ClientService = portal4ClientService;
    }

    public static IPortal4ClientGridDefineService getPortal4ClientGridDefineService() {
        return portal4ClientGridDefineService;
    }

    public static void setPortal4ClientGridDefineService(final IPortal4ClientGridDefineService portal4ClientGridDefineService) {
        RuntimeContext.portal4ClientGridDefineService = portal4ClientGridDefineService;
    }

    public static IAttachmentService getAttachmentService() {
        return attachmentService;
    }

    public static void setAttachmentService(final IAttachmentService attachmentService) {
        RuntimeContext.attachmentService = attachmentService;
    }

    public static IObjectWorkFlowService getObjectWorkFlowService() {
        return objectWorkFlowService;
    }

    public static void setObjectWorkFlowService(final IObjectWorkFlowService objectWorkFlowService) {
        RuntimeContext.objectWorkFlowService = objectWorkFlowService;
    }

    public static IObjectQueryService getObjectQueryService() {
        return objectQueryService;
    }

    public static void setObjectQueryService(final IObjectQueryService objectQueryService) {
        RuntimeContext.objectQueryService = objectQueryService;
    }

    public static IDBConfig getDBConfig() {
        return dbConfig;
    }

    public static String getAppTempDir() {
        return appTempDir;
    }

    public static void setAppTempDir(String v) {
        appTempDir = v;
    }

    public static void setDBConfig(IDBConfig dbConfig) {
        RuntimeContext.dbConfig = dbConfig;
    }

    /**
     * 构造函数
     */
    private RuntimeContext() {
        super();
    }

}

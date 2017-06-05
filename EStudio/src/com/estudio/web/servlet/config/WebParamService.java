package com.estudio.web.servlet.config;

/**
 * 定义同服务相关的一些类
 * 
 * @author Administrator
 * 
 */
public final class WebParamService extends AbstractParamService {

    public static final String APP_NAME = "AppName"; // 应用程序名
    public static final String LOGO_CAPTION = "LogoCaption"; // Logo区域标题文字
    public static final String COPYRIGHT = "Copyright"; // 版权信息
    public static final String NAVIGATOR_CONTROL = "NavigatorControl"; // 导航控件类型
    public static final String NAVIGATOR_WIDTH = "NavigatorWidth"; // 导航面板宽度
    public static final String SHARE_DOCUMENT_ROOT = "ShareDocumentRoot";// 文档资源
    public static final String ALLOW_FILE_EXTS = "AllowListFileExts"; // 允许使用的文件扩展名
    public static final String DOWNLOAD_PATH = "DownloadPath"; // 下载路径
    public static final String LOGO_URL = "LogoUrl";
    public static final String LOGO_BG_URL = "LogoBgUrl";
    public static final String LOGO_Left = "LogoLeft";
    public static final String LOGO_Top = "LogoTop";

    private WebParamService() {
        super();
    }

    private static final WebParamService INSTANCE = new WebParamService();

    public static WebParamService getInstance() {
        return INSTANCE;
    }
}

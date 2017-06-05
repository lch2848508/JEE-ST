package com.estudio.web.servlet.config;

/**
 * ����ͬ������ص�һЩ��
 * 
 * @author Administrator
 * 
 */
public final class WebParamService extends AbstractParamService {

    public static final String APP_NAME = "AppName"; // Ӧ�ó�����
    public static final String LOGO_CAPTION = "LogoCaption"; // Logo�����������
    public static final String COPYRIGHT = "Copyright"; // ��Ȩ��Ϣ
    public static final String NAVIGATOR_CONTROL = "NavigatorControl"; // �����ؼ�����
    public static final String NAVIGATOR_WIDTH = "NavigatorWidth"; // ���������
    public static final String SHARE_DOCUMENT_ROOT = "ShareDocumentRoot";// �ĵ���Դ
    public static final String ALLOW_FILE_EXTS = "AllowListFileExts"; // ����ʹ�õ��ļ���չ��
    public static final String DOWNLOAD_PATH = "DownloadPath"; // ����·��
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

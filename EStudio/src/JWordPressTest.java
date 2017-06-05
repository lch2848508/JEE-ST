import java.util.Calendar;

import com.estudio.DaemonService.WordPress;

public class JWordPressTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // // Set up XML-RPC connection to server
        // XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        // config.setServerURL(new
        // URL("http://192.173.23.2:90/wptest/xmlrpc.php"));
        // XmlRpcClient client = new XmlRpcClient();
        // client.setConfig(config);
        //
        // // Set up parameters required by newPost method
        // Map<String, String> post = new HashMap<String, String>();
        // post.put("title", "Hello, Blog!");
        // post.put("link", "http://maimode.iteye.com");
        // post.put("description", "This is the content of a trivial post.");
        // Object[] params = new Object[] { "1", "admin", "520geok.", post,
        // Boolean.TRUE };
        //
        // // Call newPost
        // String result = (String) client.execute("metaWeblog.newPost",
        // params);
        // System.out.println(" Created with blogid " + result);
        WordPress wp = new WordPress();
        wp.init("http://192.173.23.2:90/wptest/xmlrpc.php", "admin", "520geok.");
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        wp.post("大额资金使用","大额资金使用456", "hello123", "资金 测试", calendar.getTime());
    }

}

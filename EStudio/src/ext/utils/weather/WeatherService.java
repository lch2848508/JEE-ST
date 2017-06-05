package ext.utils.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.estudio.utils.ThreadUtils;

public class WeatherService {

    /**
     * @param args
     */
    public static void main(String[] args) {
        getCityWeather();
    }

    public static void execute() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    getCityWeather();
                    ThreadUtils.sleepMinute(30);
                }
            }
        }).start();
    }

    /**
     * 获取天气预报
     */
    protected static void getCityWeather() {
        try {
            String jsonResult = request();
            System.out.println(jsonResult);
            // 把结果保存到数据库中
        } catch (Exception e) {

        } finally {

        }
    }

    /**
     * @param urlAll
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request() {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = "http://api.map.baidu.com/telematics/v3/weather?location=%E6%96%B0%E9%83%BD&output=json&ak=B122767f9cf32ad2c5a17d97835d053e";

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            // connection.setRequestProperty("apikey",
            // "03191f5694b587652ebf1e828b2eb847");
            // connection.setRequestProperty("Apikey",
            // "d41d8cd98f00b204e9800998ecf8427e");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

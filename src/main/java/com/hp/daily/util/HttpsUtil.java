package com.hp.daily.util;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

public class HttpsUtil {

    public  static  SSLContext sslContext =null;

    public static SSLContext getSSL() throws Exception{
        // 创建SSLContext
        if (sslContext !=null)return  sslContext;

        sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {new X509TrustManager() {
            /*
             * 实例化一个信任连接管理器
             * 空实现是所有的连接都能访问
             */
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        // 初始化
        sslContext.init(null, trustManagers, new SecureRandom());
        return  sslContext;
    }

    /*
     * https请求是在http请求的基础上加上一个ssl层
     */

    public static String doPost(String requestUrl, String bodyStr) throws Exception {
        String charset ="utf-8";

        SSLSocketFactory sslSocketFactory = getSSL().getSocketFactory();

        URL url = new URL(requestUrl);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setSSLSocketFactory(sslSocketFactory);

        // 以下参照http请求
        httpsURLConnection.setDoOutput(true);
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setUseCaches(false);
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setRequestProperty("Accept-Charset", charset);
        if (null != bodyStr) {
            httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(bodyStr.length()));
        }
        httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpsURLConnection.connect();

        // 读写内容
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer;
        try {
            if (null != bodyStr) {
                outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(bodyStr.getBytes(charset));
                outputStream.close();
            }

            if (httpsURLConnection.getResponseCode() >= 300) {
                throw new Exception("https post failed, response code " + httpsURLConnection.getResponseCode());
            }

            inputStream = httpsURLConnection.getInputStream();
            streamReader = new InputStreamReader(inputStream, charset);
            bufferedReader = new BufferedReader(streamReader);
            stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (streamReader != null) {
                streamReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuffer.toString();
    }

    public static String doGet(String requestUrl) throws Exception {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(requestUrl);
            // 打开和URL之间的连接
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            connection.setRequestProperty("Charsert", "utf-8");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            SSLSocketFactory sslSocketFactory = getSSL().getSocketFactory();
            connection.setSSLSocketFactory(sslSocketFactory);

            // 建立实际的连接
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 获取所有响应头字段
                Map<String, List<String>> map = connection.getHeaderFields();
                // 遍历所有的响应头字段
//                for (String key : map.keySet()) {
//                    logger.info(key + "--->" + map.get(key));
//                }
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}

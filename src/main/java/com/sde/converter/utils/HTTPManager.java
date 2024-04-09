package com.sde.converter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

public class HTTPManager {

    private static Logger log = LoggerFactory.getLogger(HTTPManager.class);

    public HTTPManager() {
    }

    public static String sendAndReceive(String var0, String var1, int var2, int var3, Map<String, String> var4, String var5) throws Exception {
        return sendAndReceive(var0, null, var1, var2, var3, var4, var5);
    }

    public static String sendAndReceive(String var0, MultipartFile var1, String var2, int var3, int var4, Map<String, String> var5, String var6) throws Exception {
        Object var7 = null;
        Object var8 = null;
        String var9 = null;

        String var13;
        try {
            URL var10 = new URL(var2);
            var9 = "***" + System.currentTimeMillis() + "***";
            if (var2.startsWith("https")) {
                var7 = var10.openConnection();
            } else {
                var7 = var10.openConnection();
            }

            ((HttpURLConnection)var7).setRequestMethod(var6);
            ((HttpURLConnection)var7).setRequestProperty("Accept-Charset", "UTF-8");
            ((HttpURLConnection)var7).setReadTimeout(var4 * 1000);
            ((HttpURLConnection)var7).setConnectTimeout(var3 * 1000);
            if (var1 != null) {
                var5.replace("Content-Type", "multipart/form-data; boundary=" + var9);
            }

            if (var5 != null) {
                Iterator var11 = var5.entrySet().iterator();

                while(var11.hasNext()) {
                    Map.Entry var12 = (Map.Entry)var11.next();
                    ((HttpURLConnection)var7).setRequestProperty((String)var12.getKey(), (String)var12.getValue());
                }
            }

            ((HttpURLConnection)var7).setInstanceFollowRedirects(false);
            ((HttpURLConnection)var7).setUseCaches(false);
            ((HttpURLConnection)var7).setDoOutput(true);
            ((HttpURLConnection)var7).setDoInput(true);
            if (!var6.equalsIgnoreCase("GET")) {
                OutputStream var27 = ((HttpURLConnection)var7).getOutputStream();
                if (var1 != null) {
                    StringBuffer var29 = new StringBuffer("");
                    var29.append("--" + var9);
                    var29.append("\r\n");
                    var29.append("Content-Disposition: form-data; name=\"" + var1.getName() + "\"; filename=\"" + var1.getOriginalFilename() + "\"");
                    var29.append("\r\n");
                    var29.append("Content-Type: " + var1.getContentType());
                    var29.append("\r\n");
                    var29.append("\r\n");
                    var27.write(var29.toString().getBytes());
                    var27.write(var1.getBytes());
                    var29 = new StringBuffer("");
                    var29.append("\r\n");
                    var29.append("--" + var9);
                    var29.append("\r\n");
                    var29.append("Content-Disposition: form-data; name=\"request\"");
                    var29.append("\r\n");
                    var29.append("\r\n");
                    var29.append(var0);
                    var29.append("\r\n");
                    var29.append("--" + var9 + "--");
                    var27.write(var29.toString().getBytes());
                    var27.flush();
                } else if (null != var0) {
                    var27.write(var0.getBytes());
                    var27.flush();
                }

                var27.close();
            }

            InputStream var28;
            if (((HttpURLConnection)var7).getResponseCode() == 200) {
                var28 = ((HttpURLConnection)var7).getInputStream();
            } else {
                var28 = ((HttpURLConnection)var7).getErrorStream();
            }

            String var30 = readInputStreamToString(var28);
            var13 = var30;
        } catch (Exception var25) {
            throw var25;
        } finally {
            if (var7 != null) {
                try {
                    ((HttpURLConnection)var7).disconnect();
                } catch (Exception var24) {
                    log.error(var24.getMessage(), var24);
                }
            }

            if (var8 != null) {
                try {
                    ((BufferedReader)var8).close();
                } catch (Exception var23) {
                    log.error(var23.getMessage(), var23);
                }
            }

        }

        return var13;
    }

    public static String readInputStreamToString(InputStream var0) throws Exception {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        byte[] var2 = new byte[4096];

        int var3;
        while((var3 = var0.read(var2)) != -1) {
            var1.write(var2, 0, var3);
        }

        return var1.toString("UTF-8");
    }

    static {
        if (RuntimeServerUtil.tomcatPresent) {
            try {
                TrustManager[] var0 = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] var1, String var2) {
                    }

                    public void checkServerTrusted(X509Certificate[] var1, String var2) {
                    }
                }};
                SSLContext var1 = SSLContext.getInstance("TLS");
                var1.init(null, var0, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(var1.getSocketFactory());
                HostnameVerifier var2 = new HostnameVerifier() {
                    public boolean verify(String var1, SSLSession var2) {
                        return true;
                    }
                };
                HttpsURLConnection.setDefaultHostnameVerifier(var2);
            } catch (Exception var3) {
                log.error(var3.getMessage(), var3);
            }
        }

    }
}

package com.tiptop.ai;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpPostUtils {
    public static String postJsonWithBearer(String url, String entity, String bearer) {
        return _post(url, "application/json", entity, bearer);
    }

    public static String postJson(String url, String entity) {
        return _post(url, "application/json", entity, "");
    }

    private static String _post(String url, String contentType, String entity, String bearer) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            StringEntity se = new StringEntity(entity, "UTF-8");
            se.setContentType(contentType);
            httpPost.setEntity(se);

            if (!TextUtils.isEmpty(bearer)) {
                httpPost.addHeader("Authorization", bearer);
            }

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity1 = response.getEntity();
            String resStr = null;
            if (entity1 != null) {
                resStr = EntityUtils.toString(entity1, "UTF-8");
            }
            httpClient.close();
            response.close();
            return resStr;
        } catch (Throwable e) {
            if (GPTProxy.DEBUG)
                Log.e("GPTProxy", "post: ", e);
        }
        return "";
    }
}

package com.tiptop.ai;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GPTProxy {
    private static final String TAG = "GPTProxy";
    public static boolean DEBUG = true;
    //info={"model":"gpt-3.5-turbo-0301","max_tokens":800,"messages":[{"role":"user","content":"مقالة عن كمبوند ايفر 6 اكتوبر مصر"}]}

    //testGpt response info={"id":"chatcmpl-6zcDB7m1f6XbO6nPL6N5b1sdp9ezF","object":"chat.completion","created":1680142913,"model":"gpt-3.5-turbo-0301","usage":{"prompt_tokens":34,"completion_tokens":800,"total_tokens":834},"choices":[{"message":{"role":"assistant","content":"يعتبر كمبوند إيفر 6 أحد أهم وأفضل المشاريع العقارية في مدينة 6 أكتوبر بجمهورية مصر العربية، حيث يجمع بين الفخامة والراحة والأمن والخدمات الاستثنائية. \n\nيتميز كمبوند إيفر 6 بموقعه المتميز حيث يقع على بعد دقائق فقط من ميدان جهينة ومول العرب ونادي الصيد، ويتميز بقربه من شارع الـ90 وطريق الواحات. \n\nيضم الكمبوند مجموعة متنوعة من الوحدات السكنية بمساحات تتراوح من 150 إلى 450 متر مربع، وتتوزع على فلل وتاون هاوس وشقق، وتم تصميم تلك الوحدات السكنية بعناية فائقة لتلائم جميع الأذواق والاحتياجات، وذلك بأسعار تبدأ من 4 مليون جنيه مصري. \n\nيوفر كمبوند إيفر 6 الكثير من الخدمات والمرافق الترفيهية التي تجعل الحياة سهلة ومريحة للسكان، ومن أبرزها ملاعب التنس والأندية الصحية والمنتجعات الصحية والمراكز التجارية والمساحات الخضراء وحمامات السباحة. \n\nتم تصميم الكمبوند بطريقة تضمن الأمن والسلامة للسكان، وذلك من خلال وجود نظام أمني متطور يعمل على مدار الساعة، كما يتوفر داخل الكمبوند حراسة ومراقبة بالكاميرات المثبتة على الحواجز الأمنية. \n\nبالإضافة إلى ذلك، يضم كمبوند إيفر 6 مدرسة خاصة تحتوي على مجموعة متنوعة من المناهج الدراسية العالمية، مما يجعلها مثالية للأسر التي ترغب في توفير بيئة تعليمية رائعة لأولادها. كما يتوفر بالكمبوند أيضً"},"finish_reason":"length","index":0}]}

    public interface IGTPCallback {
        void onReceiveMsg(String msg);
    }

    public static void requestInThread(String userId, int version, String msg, IGTPCallback callback) {
        //在线程中操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                String output = "";
                try {
                    output = _request(userId, version, msg);
                    if (DEBUG) Log.i(TAG, "_response: output=" + output);
                } catch (Throwable e) {
                    if (DEBUG) Log.e(TAG, "_response: ", e);
                }
                final String result = output;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(result))
                            callback.onReceiveMsg("网络异常，请稍后重试...");
                        else
                            callback.onReceiveMsg(result);
                    }
                });
            }
        }).start();
    }

    public static String _request(String userId, int version, String msg) throws JSONException {
        String url = "http://ai.superwisdom.org/chat/test";
        String postData = "{" +
                "\"model\":\"gpt-3.5-turbo-0301\"," +
                "\"max_tokens\":800," +
                "\"version\":" + version + "," +
                "\"userId\":\"" + userId + "\"," +
                "\"messages\":[" +
                "{\"role\":\"user\",\"content\":\"" + msg + "\"}" +
                "]}";
        if (DEBUG) {
            Log.i(TAG, "_request: url=" + url);
            Log.i(TAG, "_request: postData=" + postData);
        }
        String result = HttpPostUtils.postJson(url, postData);
        if (TextUtils.isEmpty(result)) {
            return "";
        } else {
            return _parseResult(result);
        }
    }

    private static String _parseResult(String msg) throws JSONException {
        JSONObject jsonObject = new JSONObject(msg);
        JSONArray jsonArray = jsonObject.getJSONArray("choices");
        if (jsonArray == null || jsonArray.length() != 1)
            return "";
        else {
            JSONObject object = (JSONObject) jsonArray.get(0);
            JSONObject msgObject = (JSONObject) object.get("message");
            String result = (String) msgObject.get("content");
            if (DEBUG) {
                Log.i(TAG, "_response: result=" + result);
            }
            return result;
        }
    }
}

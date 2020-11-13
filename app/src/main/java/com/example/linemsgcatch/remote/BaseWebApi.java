package com.example.linemsgcatch.remote;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.linemsgcatch.R;
import com.example.linemsgcatch.data.manager.ApiDataManager;
import com.example.linemsgcatch.remote.input.FileEntity;
import com.example.linemsgcatch.remote.output.ErrorOutput;
import com.example.linemsgcatch.ui.base.MyApplication;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Simon Chang on 2017/11/3.
 */
//TODO simon test 看之後能不能全部都改成 使用 StringRequest 方法來傳遞，JSONObject、JSONArray、bodyParam 參數格式
public class BaseWebApi {
    private static final String TAG = "BaseWebApi";
    private static final int SOCKET_TIMEOUT_MS = 30000; // Http Request Timeout //Bill 因為額度轉換的API連線時間過長所以改成30秒，不然會出現Timeout Error

    private static RequestQueue mQueue;
/*
    public BaseWebApi() {
        HTTPSTrustManager.allowAllSSL();//20190115 信任所有证书

        //20190604 記錄問題: 使用單例實體實現 RequestQueue，以利改善請求太多 OOM 問題
        if (mQueue == null) {

            //20191105 記錄問題：解決 android 4.4 機型在某些網域下SSL憑證信任錯誤問題
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                HttpStack stack = null;
                try {
                    stack = new HurlStack(null, new TLSSocketFactory());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                    stack = new HurlStack();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                    stack = new HurlStack();
                }
                mQueue = Volley.newRequestQueue(MyApplication.getAppContext(), stack);
            } else {
                mQueue = Volley.newRequestQueue(MyApplication.getAppContext());
            }

        }
    }
*/


    public interface ResultListener {
        void onResult(String response);

        void onError(ErrorOutput errorOutput);
    }

    protected RequestQueue getRequestQueue() {
        return mQueue;
    }

    private ErrorOutput createErrorOutput(VolleyError error) {
        ErrorOutput errorOutput;
        try {
            String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            Log.e(TAG, response);
            errorOutput = new Gson().fromJson(response, ErrorOutput.class);

        } catch (Exception e) {
            e.printStackTrace();

            errorOutput = new ErrorOutput();
            if (error instanceof TimeoutError) {
                errorOutput.getError().setCode(-1);
                errorOutput.getError().setMessage(MyApplication.getAppContext().getString(R.string.network_timeout));
            } else if (error instanceof ServerError) {
                errorOutput.getError().setCode(-1);
                errorOutput.getError().setMessage(MyApplication.getAppContext().getString(R.string.server_error));

                //20190814 記錄問題: 500、501、403、404、401 正常情況 http 回傳的這幾個 statusCode，server 會回傳 json errorOutput，不該解析失敗執行到這
                //error.networkResponse 有機會為 null 無法獲取 statusCode
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    errorOutput.getError().setMessage(errorOutput.getError().getMessage()+"【"+statusCode+"】");
                }
            } else {
                errorOutput.getError().setCode(-1);
                errorOutput.getError().setMessage(MyApplication.getAppContext().getString(R.string.network_io_fail));
            }
        }

        return errorOutput;
    }

    //Cheryl
    protected void createStringRequest(String url, final ResultListener resultListener){
        RequestQueue requestQueue = getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (resultListener != null)
                            resultListener.onResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(),volleyError);
                if (resultListener != null)
                    resultListener.onError(createErrorOutput(volleyError));
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    //Cheryl
    protected void createStringPostRequest(String url,
                                           final Map<String, String>  header,
                                           final Map<String, String>  body,
                                           final ResultListener resultListener){
        RequestQueue requestQueue = getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (resultListener != null)
                            resultListener.onResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(),volleyError);
                if (resultListener != null)
                    resultListener.onError(createErrorOutput(volleyError));
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return header;
            }
        };

        requestQueue.add(stringRequest);
    }

    //新版API使用
    protected JsonObjectRequest createJsonObjectRequest(String url,
                                                        JSONObject jsonRequest,
                                                        ResultListener resultListener) {
        return  createJsonObjectRequest(url, null, jsonRequest, resultListener);
    }

    /**
     * Constructor which defaults to GET if jsonRequest  is null , POST otherwise.
     * @see JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     */
    protected JsonObjectRequest createJsonObjectRequest(String url,
                                                        final Map<String, String> headerParams,
                                                        JSONObject jsonRequest,
                                                        final ResultListener resultListener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (resultListener != null)
                            resultListener.onResult(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage(), error);
                        if (resultListener != null)
                            resultListener.onError(createErrorOutput(error));
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() {
                if (headerParams == null)
                    return Collections.emptyMap();
                else
                    return headerParams;
            }
        };

        // default timeout : 2,500 ms
        // Set Request Timeout : 10,000 ms
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        String body;
        try {
            body = URLDecoder.decode(new String(jsonObjectRequest.getBody(), StandardCharsets.UTF_8), "UTF-8");
        } catch (UnsupportedEncodingException | NullPointerException e) {
//            e.printStackTrace();
            body = "null";
        }

        Log.d(TAG, "Send request ==> Body: " + body);
        return jsonObjectRequest;
    }

    //20191213 傳遞 JSONArray 參數的 request
    protected StringRequest createJSONArrayRequest(int method, final String url,
                                                   final Map<String, String> headerParams,
                                                   final JSONArray jsonArray,
                                                   final ResultListener resultListener) {
        StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: (" + url + ") ==> " + response);
                        if (resultListener != null)
                            resultListener.onResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage(), error);
                        if (resultListener != null)
                            resultListener.onError(createErrorOutput(error));
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                String requestBody = (jsonArray == null) ? null : jsonArray.toString();
                try {
                    Log.e("simon test", "requestBody: "+requestBody);
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf(
                            "Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                if (headerParams == null)
                    return Collections.emptyMap();
                else
                    return headerParams;
            }


            //20181003 紀錄問題 by Simon Chang: 為解決接收到的訊息包含簡體，造成亂碼，強制使用 utf-8 解碼
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                parsed = new String(response.data, StandardCharsets.UTF_8);
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

        };

        // default timeout : 2,500 ms
        // Set Request Timeout : 10,000 ms
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        String body;
        try {
            body = URLDecoder.decode(new String(stringRequest.getBody(), StandardCharsets.UTF_8), "UTF-8");
        } catch (AuthFailureError | UnsupportedEncodingException | NullPointerException e) {
//            e.printStackTrace();
            body = "null";
        }

        Log.d(TAG, "Send request: (" + url + ") ==> Body: " + body);
        return stringRequest;
    }


    //新API使用
    protected MultipartRequest createMultipartRequest(final String url,
                                                      Map<String, String> headerParams,
                                                      Map<String, Object> bodyParams,
                                                      FileEntity fileEntity,
                                                      final ResultListener resultListener) {
        return new MultipartRequest(url, headerParams, bodyParams, fileEntity,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: (" + url + ") ==> " + response);
                        if (resultListener != null) {
                            resultListener.onResult(response);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage(), error);
                        if (resultListener != null)
                            resultListener.onError(BaseWebApi.this.createErrorOutput(error));
                    }
                });
    }

    //判斷 header value 裡面要包含 "checkCode=[\\w];" 中括弧內字串不能為空
    private boolean judgeCheckCodeValue(String text) {
        return Pattern.compile(".*checkCode=\\S+;.*").matcher(text).matches();
    }

    public interface BitmapResultListener {
        void onResult(Bitmap bitmap);

        void onHeader(Map<String, String> headers); //驗證碼需要回傳 response header

        void onError(ErrorOutput error);
    }
}

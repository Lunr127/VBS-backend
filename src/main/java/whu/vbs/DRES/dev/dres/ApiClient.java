package whu.vbs.DRES.dev.dres;

import okhttp3.*;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import whu.vbs.DRES.dev.dres.auth.ApiKeyAuth;
import whu.vbs.DRES.dev.dres.auth.Authentication;
import whu.vbs.DRES.dev.dres.auth.HttpBasicAuth;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClient {
    private String basePath = "http://localhost";
    private boolean debugging = false;
    private Map<String, String> defaultHeaderMap = new HashMap();
    private Map<String, String> defaultCookieMap = new HashMap();
    private String tempFolderPath = null;
    private Map<String, Authentication> authentications;
    private DateFormat dateFormat;
    private DateFormat datetimeFormat;
    private boolean lenientDatetimeFormat;
    private int dateLength;
    private InputStream sslCaCert;
    private boolean verifyingSsl;
    private KeyManager[] keyManagers;
    private OkHttpClient httpClient;
    private JSON json;
    private HttpLoggingInterceptor loggingInterceptor;

    public ApiClient() {
        this.init();
        this.initHttpClient();
        this.authentications = Collections.unmodifiableMap(this.authentications);
    }

    public ApiClient(OkHttpClient client) {
        this.init();
        this.httpClient = client;
        this.authentications = Collections.unmodifiableMap(this.authentications);
    }

    private void initHttpClient() {
        this.initHttpClient(Collections.emptyList());
    }

    private void initHttpClient(List<Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(this.getProgressInterceptor());
        Iterator var3 = interceptors.iterator();

        while(var3.hasNext()) {
            Interceptor interceptor = (Interceptor)var3.next();
            builder.addInterceptor(interceptor);
        }

        this.httpClient = builder.build();
    }

    private void init() {
        this.verifyingSsl = true;
        this.json = new JSON();
        this.setUserAgent("OpenAPI-Generator/1.0/java");
        this.authentications = new HashMap();
    }

    public String getBasePath() {
        return this.basePath;
    }

    public ApiClient setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public OkHttpClient getHttpClient() {
        return this.httpClient;
    }

    public ApiClient setHttpClient(OkHttpClient newHttpClient) {
        this.httpClient = (OkHttpClient)Objects.requireNonNull(newHttpClient, "HttpClient must not be null!");
        return this;
    }

    public JSON getJSON() {
        return this.json;
    }

    public ApiClient setJSON(JSON json) {
        this.json = json;
        return this;
    }

    public boolean isVerifyingSsl() {
        return this.verifyingSsl;
    }

    public ApiClient setVerifyingSsl(boolean verifyingSsl) {
        this.verifyingSsl = verifyingSsl;
        this.applySslSettings();
        return this;
    }

    public InputStream getSslCaCert() {
        return this.sslCaCert;
    }

    public ApiClient setSslCaCert(InputStream sslCaCert) {
        this.sslCaCert = sslCaCert;
        this.applySslSettings();
        return this;
    }

    public KeyManager[] getKeyManagers() {
        return this.keyManagers;
    }

    public ApiClient setKeyManagers(KeyManager[] managers) {
        this.keyManagers = managers;
        this.applySslSettings();
        return this;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    public ApiClient setDateFormat(DateFormat dateFormat) {
        this.json.setDateFormat(dateFormat);
        return this;
    }

    public ApiClient setSqlDateFormat(DateFormat dateFormat) {
        this.json.setSqlDateFormat(dateFormat);
        return this;
    }

    public ApiClient setOffsetDateTimeFormat(DateTimeFormatter dateFormat) {
        this.json.setOffsetDateTimeFormat(dateFormat);
        return this;
    }

    public ApiClient setLocalDateFormat(DateTimeFormatter dateFormat) {
        this.json.setLocalDateFormat(dateFormat);
        return this;
    }

    public ApiClient setLenientOnJson(boolean lenientOnJson) {
        this.json.setLenientOnJson(lenientOnJson);
        return this;
    }

    public Map<String, Authentication> getAuthentications() {
        return this.authentications;
    }

    public Authentication getAuthentication(String authName) {
        return (Authentication)this.authentications.get(authName);
    }

    public void setUsername(String username) {
        Iterator var2 = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!var2.hasNext()) {
                throw new RuntimeException("No HTTP basic authentication configured!");
            }

            auth = (Authentication)var2.next();
        } while(!(auth instanceof HttpBasicAuth));

        ((HttpBasicAuth)auth).setUsername(username);
    }

    public void setPassword(String password) {
        Iterator var2 = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!var2.hasNext()) {
                throw new RuntimeException("No HTTP basic authentication configured!");
            }

            auth = (Authentication)var2.next();
        } while(!(auth instanceof HttpBasicAuth));

        ((HttpBasicAuth)auth).setPassword(password);
    }

    public void setApiKey(String apiKey) {
        Iterator var2 = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!var2.hasNext()) {
                throw new RuntimeException("No API key authentication configured!");
            }

            auth = (Authentication)var2.next();
        } while(!(auth instanceof ApiKeyAuth));

        ((ApiKeyAuth)auth).setApiKey(apiKey);
    }

    public void setApiKeyPrefix(String apiKeyPrefix) {
        Iterator var2 = this.authentications.values().iterator();

        Authentication auth;
        do {
            if (!var2.hasNext()) {
                throw new RuntimeException("No API key authentication configured!");
            }

            auth = (Authentication)var2.next();
        } while(!(auth instanceof ApiKeyAuth));

        ((ApiKeyAuth)auth).setApiKeyPrefix(apiKeyPrefix);
    }

    public void setAccessToken(String accessToken) {
        throw new RuntimeException("No OAuth2 authentication configured!");
    }

    public ApiClient setUserAgent(String userAgent) {
        this.addDefaultHeader("User-Agent", userAgent);
        return this;
    }

    public ApiClient addDefaultHeader(String key, String value) {
        this.defaultHeaderMap.put(key, value);
        return this;
    }

    public ApiClient addDefaultCookie(String key, String value) {
        this.defaultCookieMap.put(key, value);
        return this;
    }

    public boolean isDebugging() {
        return this.debugging;
    }

    public ApiClient setDebugging(boolean debugging) {
        if (debugging != this.debugging) {
            if (debugging) {
                this.loggingInterceptor = new HttpLoggingInterceptor();
                this.loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                this.httpClient = this.httpClient.newBuilder().addInterceptor(this.loggingInterceptor).build();
            } else {
                OkHttpClient.Builder builder = this.httpClient.newBuilder();
                builder.interceptors().remove(this.loggingInterceptor);
                this.httpClient = builder.build();
                this.loggingInterceptor = null;
            }
        }

        this.debugging = debugging;
        return this;
    }

    public String getTempFolderPath() {
        return this.tempFolderPath;
    }

    public ApiClient setTempFolderPath(String tempFolderPath) {
        this.tempFolderPath = tempFolderPath;
        return this;
    }

    public int getConnectTimeout() {
        return this.httpClient.connectTimeoutMillis();
    }

    public ApiClient setConnectTimeout(int connectionTimeout) {
        this.httpClient = this.httpClient.newBuilder().connectTimeout((long)connectionTimeout, TimeUnit.MILLISECONDS).build();
        return this;
    }

    public int getReadTimeout() {
        return this.httpClient.readTimeoutMillis();
    }

    public ApiClient setReadTimeout(int readTimeout) {
        this.httpClient = this.httpClient.newBuilder().readTimeout((long)readTimeout, TimeUnit.MILLISECONDS).build();
        return this;
    }

    public int getWriteTimeout() {
        return this.httpClient.writeTimeoutMillis();
    }

    public ApiClient setWriteTimeout(int writeTimeout) {
        this.httpClient = this.httpClient.newBuilder().writeTimeout((long)writeTimeout, TimeUnit.MILLISECONDS).build();
        return this;
    }

    public String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (!(param instanceof Date) && !(param instanceof OffsetDateTime) && !(param instanceof LocalDate)) {
            if (param instanceof Collection) {
                StringBuilder b = new StringBuilder();

                Object o;
                for(Iterator var3 = ((Collection)param).iterator(); var3.hasNext(); b.append(String.valueOf(o))) {
                    o = var3.next();
                    if (b.length() > 0) {
                        b.append(",");
                    }
                }

                return b.toString();
            } else {
                return String.valueOf(param);
            }
        } else {
            String jsonStr = this.json.serialize(param);
            return jsonStr.substring(1, jsonStr.length() - 1);
        }
    }

    public List<Pair> parameterToPair(String name, Object value) {
        List<Pair> params = new ArrayList();
        if (name != null && !name.isEmpty() && value != null && !(value instanceof Collection)) {
            params.add(new Pair(name, this.parameterToString(value)));
            return params;
        } else {
            return params;
        }
    }

    public List<Pair> parameterToPairs(String collectionFormat, String name, Collection value) {
        List<Pair> params = new ArrayList();
        if (name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
            if ("multi".equals(collectionFormat)) {
                Iterator var10 = value.iterator();

                while(var10.hasNext()) {
                    Object item = var10.next();
                    params.add(new Pair(name, this.escapeString(this.parameterToString(item))));
                }

                return params;
            } else {
                String delimiter = ",";
                if ("ssv".equals(collectionFormat)) {
                    delimiter = this.escapeString(" ");
                } else if ("tsv".equals(collectionFormat)) {
                    delimiter = this.escapeString("\t");
                } else if ("pipes".equals(collectionFormat)) {
                    delimiter = this.escapeString("|");
                }

                StringBuilder sb = new StringBuilder();
                Iterator var7 = value.iterator();

                while(var7.hasNext()) {
                    Object item = var7.next();
                    sb.append(delimiter);
                    sb.append(this.escapeString(this.parameterToString(item)));
                }

                params.add(new Pair(name, sb.substring(delimiter.length())));
                return params;
            }
        } else {
            return params;
        }
    }

    public String collectionPathParameterToString(String collectionFormat, Collection value) {
        if ("multi".equals(collectionFormat)) {
            return this.parameterToString(value);
        } else {
            String delimiter = ",";
            if ("ssv".equals(collectionFormat)) {
                delimiter = " ";
            } else if ("tsv".equals(collectionFormat)) {
                delimiter = "\t";
            } else if ("pipes".equals(collectionFormat)) {
                delimiter = "|";
            }

            StringBuilder sb = new StringBuilder();
            Iterator var5 = value.iterator();

            while(var5.hasNext()) {
                Object item = var5.next();
                sb.append(delimiter);
                sb.append(this.parameterToString(item));
            }

            return sb.substring(delimiter.length());
        }
    }

    public String sanitizeFilename(String filename) {
        return filename.replaceAll(".*[/\\\\]", "");
    }

    public boolean isJsonMime(String mime) {
        String jsonMime = "(?i)^(application/json|[^;/ \t]+/[^;/ \t]+[+]json)[ \t]*(;.*)?$";
        return mime != null && (mime.matches(jsonMime) || mime.equals("*/*"));
    }

    public String selectHeaderAccept(String[] accepts) {
        if (accepts.length == 0) {
            return null;
        } else {
            String[] var2 = accepts;
            int var3 = accepts.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String accept = var2[var4];
                if (this.isJsonMime(accept)) {
                    return accept;
                }
            }

            return StringUtil.join(accepts, ",");
        }
    }

    public String selectHeaderContentType(String[] contentTypes) {
        if (contentTypes.length != 0 && !contentTypes[0].equals("*/*")) {
            String[] var2 = contentTypes;
            int var3 = contentTypes.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String contentType = var2[var4];
                if (this.isJsonMime(contentType)) {
                    return contentType;
                }
            }

            return contentTypes[0];
        } else {
            return "application/json";
        }
    }

    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException var3) {
            return str;
        }
    }

    public <T> T deserialize(Response response, Type returnType) throws ApiException {
        if (response != null && returnType != null) {
            if ("byte[]".equals(returnType.toString())) {
                try {
                    return (T) response.body().bytes();
                } catch (IOException var5) {
                    throw new ApiException(var5);
                }
            } else if (returnType.equals(File.class)) {
                return (T) this.downloadFileFromResponse(response);
            } else {
                String respBody;
                try {
                    if (response.body() != null) {
                        respBody = response.body().string();
                    } else {
                        respBody = null;
                    }
                } catch (IOException var6) {
                    throw new ApiException(var6);
                }

                if (respBody != null && !"".equals(respBody)) {
                    String contentType = response.headers().get("Content-Type");
                    if (contentType == null) {
                        contentType = "application/json";
                    }

                    if (this.isJsonMime(contentType)) {
                        return this.json.deserialize(respBody, returnType);
                    } else if (returnType.equals(String.class)) {
                        return (T) respBody;
                    } else {
                        throw new ApiException("Content type \"" + contentType + "\" is not supported for type: " + returnType, response.code(), response.headers().toMultimap(), respBody);
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public RequestBody serialize(Object obj, String contentType) throws ApiException {
        if (obj instanceof byte[]) {
            return RequestBody.create(MediaType.parse(contentType), (byte[])obj);
        } else if (obj instanceof File) {
            return RequestBody.create(MediaType.parse(contentType), (File)obj);
        } else if (this.isJsonMime(contentType)) {
            String content;
            if (obj != null) {
                content = this.json.serialize(obj);
            } else {
                content = null;
            }

            return RequestBody.create(MediaType.parse(contentType), content);
        } else {
            throw new ApiException("Content type \"" + contentType + "\" is not supported");
        }
    }

    public File downloadFileFromResponse(Response response) throws ApiException {
        try {
            File file = this.prepareDownloadFile(response);
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();
            return file;
        } catch (IOException var4) {
            throw new ApiException(var4);
        }
    }

    public File prepareDownloadFile(Response response) throws IOException {
        String filename = null;
        String contentDisposition = response.header("Content-Disposition");
        Pattern prefix;
        if (contentDisposition != null && !"".equals(contentDisposition)) {
            prefix = Pattern.compile("filename=['\"]?([^'\"\\s]+)['\"]?");
            Matcher matcher = prefix.matcher(contentDisposition);
            if (matcher.find()) {
                filename = this.sanitizeFilename(matcher.group(1));
            }
        }

        prefix = null;
        String suffix = null;
        String preFix;
        if (filename == null) {
            preFix = "download-";
            suffix = "";
        } else {
            int pos = filename.lastIndexOf(".");
            if (pos == -1) {
                preFix = filename + "-";
            } else {
                String var10000 = filename.substring(0, pos);
                preFix = var10000 + "-";
                suffix = filename.substring(pos);
            }

            if (preFix.length() < 3) {
                preFix = "download-";
            }
        }

        return this.tempFolderPath == null ? File.createTempFile(preFix, suffix) : File.createTempFile(preFix, suffix, new File(this.tempFolderPath));
    }

    public <T> ApiResponse<T> execute(Call call) throws ApiException {
        return this.execute(call, (Type)null);
    }

    public <T> ApiResponse<T> execute(Call call, Type returnType) throws ApiException {
        try {
            Response response = call.execute();
            T data = this.handleResponse(response, returnType);
            return new ApiResponse(response.code(), response.headers().toMultimap(), data);
        } catch (IOException var5) {
            throw new ApiException(var5);
        }
    }

    public <T> void executeAsync(Call call, ApiCallback<T> callback) {
        this.executeAsync(call, (Type)null, callback);
    }

    public <T> void executeAsync(Call call, final Type returnType, final ApiCallback<T> callback) {
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new ApiException(e), 0, (Map)null);
            }

            public void onResponse(Call call, Response response) throws IOException {
                Object result;
                try {
                    result = ApiClient.this.handleResponse(response, returnType);
                } catch (ApiException var5) {
                    callback.onFailure(var5, response.code(), response.headers().toMultimap());
                    return;
                } catch (Exception var6) {
                    callback.onFailure(new ApiException(var6), response.code(), response.headers().toMultimap());
                    return;
                }

                callback.onSuccess((T) result, response.code(), response.headers().toMultimap());
            }
        });
    }

    public <T> T handleResponse(Response response, Type returnType) throws ApiException {
        if (response.isSuccessful()) {
            if (returnType != null && response.code() != 204) {
                return this.deserialize(response, returnType);
            } else {
                if (response.body() != null) {
                    try {
                        response.body().close();
                    } catch (Exception var5) {
                        throw new ApiException(response.message(), var5, response.code(), response.headers().toMultimap());
                    }
                }

                return null;
            }
        } else {
            String respBody = null;
            if (response.body() != null) {
                try {
                    respBody = response.body().string();
                } catch (IOException var6) {
                    throw new ApiException(response.message(), var6, response.code(), response.headers().toMultimap());
                }
            }

            throw new ApiException(response.message(), response.code(), response.headers().toMultimap(), respBody);
        }
    }

    public Call buildCall(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String[] authNames, ApiCallback callback) throws ApiException {
        Request request = this.buildRequest(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, callback);
        return this.httpClient.newCall(request);
    }

    public Request buildRequest(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String[] authNames, ApiCallback callback) throws ApiException {
        this.updateParamsForAuth(authNames, queryParams, headerParams, cookieParams);
        String url = this.buildUrl(path, queryParams, collectionQueryParams);
        Request.Builder reqBuilder = (new Request.Builder()).url(url);
        this.processHeaderParams(headerParams, reqBuilder);
        this.processCookieParams(cookieParams, reqBuilder);
        String contentType = (String)headerParams.get("Content-Type");
        if (contentType == null) {
            contentType = "application/json";
        }

        RequestBody reqBody;
        if (!HttpMethod.permitsRequestBody(method)) {
            reqBody = null;
        } else if ("application/x-www-form-urlencoded".equals(contentType)) {
            reqBody = this.buildRequestBodyFormEncoding(formParams);
        } else if ("multipart/form-data".equals(contentType)) {
            reqBody = this.buildRequestBodyMultipart(formParams);
        } else if (body == null) {
            if ("DELETE".equals(method)) {
                reqBody = null;
            } else {
                reqBody = RequestBody.create(MediaType.parse(contentType), "");
            }
        } else {
            reqBody = this.serialize(body, contentType);
        }

        reqBuilder.tag(callback);
        Request request = null;
        if (callback != null && reqBody != null) {
            ProgressRequestBody progressRequestBody = new ProgressRequestBody(reqBody, callback);
            request = reqBuilder.method(method, progressRequestBody).build();
        } else {
            request = reqBuilder.method(method, reqBody).build();
        }

        return request;
    }

    public String buildUrl(String path, List<Pair> queryParams, List<Pair> collectionQueryParams) {
        StringBuilder url = new StringBuilder();
        url.append(this.basePath).append(path);
        String prefix;
        Iterator var6;
        Pair param;
        String value;
        if (queryParams != null && !queryParams.isEmpty()) {
            prefix = path.contains("?") ? "&" : "?";
            var6 = queryParams.iterator();

            while(var6.hasNext()) {
                param = (Pair)var6.next();
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append("&");
                    }

                    value = this.parameterToString(param.getValue());
                    url.append(this.escapeString(param.getName())).append("=").append(this.escapeString(value));
                }
            }
        }

        if (collectionQueryParams != null && !collectionQueryParams.isEmpty()) {
            prefix = url.toString().contains("?") ? "&" : "?";
            var6 = collectionQueryParams.iterator();

            while(var6.hasNext()) {
                param = (Pair)var6.next();
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append("&");
                    }

                    value = this.parameterToString(param.getValue());
                    url.append(this.escapeString(param.getName())).append("=").append(value);
                }
            }
        }

        return url.toString();
    }

    public void processHeaderParams(Map<String, String> headerParams, Request.Builder reqBuilder) {
        Iterator var3 = headerParams.entrySet().iterator();

        Map.Entry header;
        while(var3.hasNext()) {
            header = (Map.Entry)var3.next();
            reqBuilder.header((String)header.getKey(), this.parameterToString(header.getValue()));
        }

        var3 = this.defaultHeaderMap.entrySet().iterator();

        while(var3.hasNext()) {
            header = (Map.Entry)var3.next();
            if (!headerParams.containsKey(header.getKey())) {
                reqBuilder.header((String)header.getKey(), this.parameterToString(header.getValue()));
            }
        }

    }

    public void processCookieParams(Map<String, String> cookieParams, Request.Builder reqBuilder) {
        Iterator var3 = cookieParams.entrySet().iterator();

        Map.Entry param;
        while(var3.hasNext()) {
            param = (Map.Entry)var3.next();
            reqBuilder.addHeader("Cookie", String.format("%s=%s", param.getKey(), param.getValue()));
        }

        var3 = this.defaultCookieMap.entrySet().iterator();

        while(var3.hasNext()) {
            param = (Map.Entry)var3.next();
            if (!cookieParams.containsKey(param.getKey())) {
                reqBuilder.addHeader("Cookie", String.format("%s=%s", param.getKey(), param.getValue()));
            }
        }

    }

    public void updateParamsForAuth(String[] authNames, List<Pair> queryParams, Map<String, String> headerParams, Map<String, String> cookieParams) {
        String[] var5 = authNames;
        int var6 = authNames.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String authName = var5[var7];
            Authentication auth = (Authentication)this.authentications.get(authName);
            if (auth == null) {
                throw new RuntimeException("Authentication undefined: " + authName);
            }

            auth.applyToParams(queryParams, headerParams, cookieParams);
        }

    }

    public RequestBody buildRequestBodyFormEncoding(Map<String, Object> formParams) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        Iterator var3 = formParams.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry)var3.next();
            formBuilder.add((String)param.getKey(), this.parameterToString(param.getValue()));
        }

        return formBuilder.build();
    }

    public RequestBody buildRequestBodyMultipart(Map<String, Object> formParams) {
        MultipartBody.Builder mpBuilder = (new MultipartBody.Builder()).setType(MultipartBody.FORM);
        Iterator var3 = formParams.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry)var3.next();
            if (param.getValue() instanceof File) {
                File file = (File)param.getValue();
                String[] var10000 = new String[]{"Content-Disposition", null};
                String var10003 = (String)param.getKey();
                var10000[1] = "form-data; name=\"" + var10003 + "\"; filename=\"" + file.getName() + "\"";
                Headers partHeaders = Headers.of(var10000);
                MediaType mediaType = MediaType.parse(this.guessContentTypeFromFile(file));
                mpBuilder.addPart(partHeaders, RequestBody.create(mediaType, file));
            } else {
                Headers partHeaders = Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + (String)param.getKey() + "\""});
                mpBuilder.addPart(partHeaders, RequestBody.create((MediaType)null, this.parameterToString(param.getValue())));
            }
        }

        return mpBuilder.build();
    }

    public String guessContentTypeFromFile(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        return contentType == null ? "application/octet-stream" : contentType;
    }

    private Interceptor getProgressInterceptor() {
        return new Interceptor() {
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request request = chain.request();
                Response originalResponse = chain.proceed(request);
                if (request.tag() instanceof ApiCallback) {
                    ApiCallback callback = (ApiCallback)request.tag();
                    return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), callback)).build();
                } else {
                    return originalResponse;
                }
            }
        };
    }

    private void applySslSettings() {
        try {
            TrustManager[] trustManagers;
            Object hostnameVerifier;
            if (!this.verifyingSsl) {
                trustManagers = new TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }};
                hostnameVerifier = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
            } else {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                if (this.sslCaCert == null) {
                    trustManagerFactory.init((KeyStore)null);
                } else {
                    char[] password = null;
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(this.sslCaCert);
                    if (certificates.isEmpty()) {
                        throw new IllegalArgumentException("expected non-empty set of trusted certificates");
                    }

                    KeyStore caKeyStore = this.newEmptyKeyStore((char[])password);
                    int index = 0;
                    Iterator var9 = certificates.iterator();

                    while(var9.hasNext()) {
                        Certificate certificate = (Certificate)var9.next();
                        int var10000 = index++;
                        String certificateAlias = "ca" + Integer.toString(var10000);
                        caKeyStore.setCertificateEntry(certificateAlias, certificate);
                    }

                    trustManagerFactory.init(caKeyStore);
                }

                trustManagers = trustManagerFactory.getTrustManagers();
                hostnameVerifier = OkHostnameVerifier.INSTANCE;
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(this.keyManagers, trustManagers, new SecureRandom());
            this.httpClient = this.httpClient.newBuilder().sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustManagers[0]).hostnameVerifier((HostnameVerifier)hostnameVerifier).build();
        } catch (GeneralSecurityException var12) {
            throw new RuntimeException(var12);
        }
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load((InputStream)null, password);
            return keyStore;
        } catch (IOException var3) {
            throw new AssertionError(var3);
        }
    }
}

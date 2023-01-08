package whu.vbs.DRES.dev.dres.client;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import whu.vbs.DRES.dev.dres.*;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.LoginRequest;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.SuccessStatus;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.UserDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApi {
    private ApiClient localVarApiClient;

    public UserApi() {
        this(Configuration.getDefaultApiClient());
    }

    public UserApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public Call getApiV1LogoutCall(String session, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/logout";
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();
        Map<String, String> localVarHeaderParams = new HashMap();
        Map<String, String> localVarCookieParams = new HashMap();
        Map<String, Object> localVarFormParams = new HashMap();
        if (session != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("session", session));
        }

        String[] localVarAccepts = new String[]{"application/json"};
        String localVarAccept = this.localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        String[] localVarContentTypes = new String[0];
        String localVarContentType = this.localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[0];
        return this.localVarApiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    private Call getApiV1LogoutValidateBeforeCall(String session, ApiCallback _callback) throws ApiException {
        Call localVarCall = this.getApiV1LogoutCall(session, _callback);
        return localVarCall;
    }

    public SuccessStatus getApiV1Logout(String session) throws ApiException {
        ApiResponse<SuccessStatus> localVarResp = this.getApiV1LogoutWithHttpInfo(session);
        return (SuccessStatus)localVarResp.getData();
    }

    public ApiResponse<SuccessStatus> getApiV1LogoutWithHttpInfo(String session) throws ApiException {
        Call localVarCall = this.getApiV1LogoutValidateBeforeCall(session, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<SuccessStatus>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1LogoutAsync(String session, ApiCallback<SuccessStatus> _callback) throws ApiException {
        Call localVarCall = this.getApiV1LogoutValidateBeforeCall(session, _callback);
        Type localVarReturnType = (new TypeToken<SuccessStatus>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }

    public Call getApiV1UserCall(ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/user";
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();
        Map<String, String> localVarHeaderParams = new HashMap();
        Map<String, String> localVarCookieParams = new HashMap();
        Map<String, Object> localVarFormParams = new HashMap();
        String[] localVarAccepts = new String[]{"application/json"};
        String localVarAccept = this.localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        String[] localVarContentTypes = new String[0];
        String localVarContentType = this.localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[0];
        return this.localVarApiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    private Call getApiV1UserValidateBeforeCall(ApiCallback _callback) throws ApiException {
        Call localVarCall = this.getApiV1UserCall(_callback);
        return localVarCall;
    }

    public UserDetails getApiV1User() throws ApiException {
        ApiResponse<UserDetails> localVarResp = this.getApiV1UserWithHttpInfo();
        return (UserDetails)localVarResp.getData();
    }

    public ApiResponse<UserDetails> getApiV1UserWithHttpInfo() throws ApiException {
        Call localVarCall = this.getApiV1UserValidateBeforeCall((ApiCallback)null);
        Type localVarReturnType = (new TypeToken<UserDetails>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1UserAsync(ApiCallback<UserDetails> _callback) throws ApiException {
        Call localVarCall = this.getApiV1UserValidateBeforeCall(_callback);
        Type localVarReturnType = (new TypeToken<UserDetails>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }

    public Call postApiV1LoginCall(LoginRequest loginRequest, ApiCallback _callback) throws ApiException {
        String localVarPath = "/api/v1/login";
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();
        Map<String, String> localVarHeaderParams = new HashMap();
        Map<String, String> localVarCookieParams = new HashMap();
        Map<String, Object> localVarFormParams = new HashMap();
        String[] localVarAccepts = new String[]{"application/json"};
        String localVarAccept = this.localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        String[] localVarContentTypes = new String[]{"application/json"};
        String localVarContentType = this.localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[0];
        return this.localVarApiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, loginRequest, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    private Call postApiV1LoginValidateBeforeCall(LoginRequest loginRequest, ApiCallback _callback) throws ApiException {
        Call localVarCall = this.postApiV1LoginCall(loginRequest, _callback);
        return localVarCall;
    }

    public UserDetails postApiV1Login(LoginRequest loginRequest) throws ApiException {
        ApiResponse<UserDetails> localVarResp = this.postApiV1LoginWithHttpInfo(loginRequest);
        return (UserDetails)localVarResp.getData();
    }

    public ApiResponse<UserDetails> postApiV1LoginWithHttpInfo(LoginRequest loginRequest) throws ApiException {
        Call localVarCall = this.postApiV1LoginValidateBeforeCall(loginRequest, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<UserDetails>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call postApiV1LoginAsync(LoginRequest loginRequest, ApiCallback<UserDetails> _callback) throws ApiException {
        Call localVarCall = this.postApiV1LoginValidateBeforeCall(loginRequest, _callback);
        Type localVarReturnType = (new TypeToken<UserDetails>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

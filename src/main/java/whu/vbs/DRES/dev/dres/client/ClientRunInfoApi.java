package whu.vbs.DRES.dev.dres.client;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import whu.vbs.DRES.dev.dres.*;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.ClientRunInfoList;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.ClientTaskInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRunInfoApi {
    private ApiClient localVarApiClient;

    public ClientRunInfoApi() {
        this(Configuration.getDefaultApiClient());
    }

    public ClientRunInfoApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public Call getApiV1ClientRunInfoCurrenttaskWithRunidCall(String runId, String session, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/client/run/info/currentTask/{runId}".replaceAll("\\{runId\\}", this.localVarApiClient.escapeString(runId.toString()));
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

    private Call getApiV1ClientRunInfoCurrenttaskWithRunidValidateBeforeCall(String runId, String session, ApiCallback _callback) throws ApiException {
        if (runId == null) {
            throw new ApiException("Missing the required parameter 'runId' when calling getApiV1ClientRunInfoCurrenttaskWithRunid(Async)");
        } else if (session == null) {
            throw new ApiException("Missing the required parameter 'session' when calling getApiV1ClientRunInfoCurrenttaskWithRunid(Async)");
        } else {
            Call localVarCall = this.getApiV1ClientRunInfoCurrenttaskWithRunidCall(runId, session, _callback);
            return localVarCall;
        }
    }

    public ClientTaskInfo getApiV1ClientRunInfoCurrenttaskWithRunid(String runId, String session) throws ApiException {
        ApiResponse<ClientTaskInfo> localVarResp = this.getApiV1ClientRunInfoCurrenttaskWithRunidWithHttpInfo(runId, session);
        return (ClientTaskInfo)localVarResp.getData();
    }

    public ApiResponse<ClientTaskInfo> getApiV1ClientRunInfoCurrenttaskWithRunidWithHttpInfo(String runId, String session) throws ApiException {
        Call localVarCall = this.getApiV1ClientRunInfoCurrenttaskWithRunidValidateBeforeCall(runId, session, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<ClientTaskInfo>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1ClientRunInfoCurrenttaskWithRunidAsync(String runId, String session, ApiCallback<ClientTaskInfo> _callback) throws ApiException {
        Call localVarCall = this.getApiV1ClientRunInfoCurrenttaskWithRunidValidateBeforeCall(runId, session, _callback);
        Type localVarReturnType = (new TypeToken<ClientTaskInfo>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }

    public Call getApiV1ClientRunInfoListCall(String session, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/client/run/info/list";
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

    private Call getApiV1ClientRunInfoListValidateBeforeCall(String session, ApiCallback _callback) throws ApiException {
        if (session == null) {
            throw new ApiException("Missing the required parameter 'session' when calling getApiV1ClientRunInfoList(Async)");
        } else {
            Call localVarCall = this.getApiV1ClientRunInfoListCall(session, _callback);
            return localVarCall;
        }
    }

    public ClientRunInfoList getApiV1ClientRunInfoList(String session) throws ApiException {
        ApiResponse<ClientRunInfoList> localVarResp = this.getApiV1ClientRunInfoListWithHttpInfo(session);
        return (ClientRunInfoList)localVarResp.getData();
    }

    public ApiResponse<ClientRunInfoList> getApiV1ClientRunInfoListWithHttpInfo(String session) throws ApiException {
        Call localVarCall = this.getApiV1ClientRunInfoListValidateBeforeCall(session, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<ClientRunInfoList>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1ClientRunInfoListAsync(String session, ApiCallback<ClientRunInfoList> _callback) throws ApiException {
        Call localVarCall = this.getApiV1ClientRunInfoListValidateBeforeCall(session, _callback);
        Type localVarReturnType = (new TypeToken<ClientRunInfoList>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

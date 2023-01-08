package whu.vbs.DRES.dev.dres.client;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import whu.vbs.DRES.dev.dres.*;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.QueryEventLog;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.QueryResultLog;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.SuccessStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogApi {
    private ApiClient localVarApiClient;

    public LogApi() {
        this(Configuration.getDefaultApiClient());
    }

    public LogApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public Call postApiV1LogQueryCall(String session, QueryEventLog queryEventLog, ApiCallback _callback) throws ApiException {
        String localVarPath = "/api/v1/log/query";
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

        String[] localVarContentTypes = new String[]{"application/json"};
        String localVarContentType = this.localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[0];
        return this.localVarApiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, queryEventLog, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    private Call postApiV1LogQueryValidateBeforeCall(String session, QueryEventLog queryEventLog, ApiCallback _callback) throws ApiException {
        if (session == null) {
            throw new ApiException("Missing the required parameter 'session' when calling postApiV1LogQuery(Async)");
        } else {
            Call localVarCall = this.postApiV1LogQueryCall(session, queryEventLog, _callback);
            return localVarCall;
        }
    }

    public SuccessStatus postApiV1LogQuery(String session, QueryEventLog queryEventLog) throws ApiException {
        ApiResponse<SuccessStatus> localVarResp = this.postApiV1LogQueryWithHttpInfo(session, queryEventLog);
        return (SuccessStatus)localVarResp.getData();
    }

    public ApiResponse<SuccessStatus> postApiV1LogQueryWithHttpInfo(String session, QueryEventLog queryEventLog) throws ApiException {
        Call localVarCall = this.postApiV1LogQueryValidateBeforeCall(session, queryEventLog, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<SuccessStatus>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call postApiV1LogQueryAsync(String session, QueryEventLog queryEventLog, ApiCallback<SuccessStatus> _callback) throws ApiException {
        Call localVarCall = this.postApiV1LogQueryValidateBeforeCall(session, queryEventLog, _callback);
        Type localVarReturnType = (new TypeToken<SuccessStatus>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }

    public Call postApiV1LogResultCall(String session, QueryResultLog queryResultLog, ApiCallback _callback) throws ApiException {
        String localVarPath = "/api/v1/log/result";
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

        String[] localVarContentTypes = new String[]{"application/json"};
        String localVarContentType = this.localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[0];
        return this.localVarApiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, queryResultLog, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    private Call postApiV1LogResultValidateBeforeCall(String session, QueryResultLog queryResultLog, ApiCallback _callback) throws ApiException {
        if (session == null) {
            throw new ApiException("Missing the required parameter 'session' when calling postApiV1LogResult(Async)");
        } else {
            Call localVarCall = this.postApiV1LogResultCall(session, queryResultLog, _callback);
            return localVarCall;
        }
    }

    public SuccessStatus postApiV1LogResult(String session, QueryResultLog queryResultLog) throws ApiException {
        ApiResponse<SuccessStatus> localVarResp = this.postApiV1LogResultWithHttpInfo(session, queryResultLog);
        return (SuccessStatus)localVarResp.getData();
    }

    public ApiResponse<SuccessStatus> postApiV1LogResultWithHttpInfo(String session, QueryResultLog queryResultLog) throws ApiException {
        Call localVarCall = this.postApiV1LogResultValidateBeforeCall(session, queryResultLog, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<SuccessStatus>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call postApiV1LogResultAsync(String session, QueryResultLog queryResultLog, ApiCallback<SuccessStatus> _callback) throws ApiException {
        Call localVarCall = this.postApiV1LogResultValidateBeforeCall(session, queryResultLog, _callback);
        Type localVarReturnType = (new TypeToken<SuccessStatus>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

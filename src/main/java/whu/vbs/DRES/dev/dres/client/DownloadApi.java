package whu.vbs.DRES.dev.dres.client;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import whu.vbs.DRES.dev.dres.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadApi {
    private ApiClient localVarApiClient;

    public DownloadApi() {
        this(Configuration.getDefaultApiClient());
    }

    public DownloadApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public Call getApiV1DownloadCompetitionWithCompetitionidCall(String competitionId, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/download/competition/{competitionId}".replaceAll("\\{competitionId\\}", this.localVarApiClient.escapeString(competitionId.toString()));
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

    private Call getApiV1DownloadCompetitionWithCompetitionidValidateBeforeCall(String competitionId, ApiCallback _callback) throws ApiException {
        if (competitionId == null) {
            throw new ApiException("Missing the required parameter 'competitionId' when calling getApiV1DownloadCompetitionWithCompetitionid(Async)");
        } else {
            Call localVarCall = this.getApiV1DownloadCompetitionWithCompetitionidCall(competitionId, _callback);
            return localVarCall;
        }
    }

    public String getApiV1DownloadCompetitionWithCompetitionid(String competitionId) throws ApiException {
        ApiResponse<String> localVarResp = this.getApiV1DownloadCompetitionWithCompetitionidWithHttpInfo(competitionId);
        return (String)localVarResp.getData();
    }

    public ApiResponse<String> getApiV1DownloadCompetitionWithCompetitionidWithHttpInfo(String competitionId) throws ApiException {
        Call localVarCall = this.getApiV1DownloadCompetitionWithCompetitionidValidateBeforeCall(competitionId, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1DownloadCompetitionWithCompetitionidAsync(String competitionId, ApiCallback<String> _callback) throws ApiException {
        Call localVarCall = this.getApiV1DownloadCompetitionWithCompetitionidValidateBeforeCall(competitionId, _callback);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }

    public Call getApiV1DownloadRunWithRunidCall(String runId, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/download/run/{runId}".replaceAll("\\{runId\\}", this.localVarApiClient.escapeString(runId.toString()));
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

    private Call getApiV1DownloadRunWithRunidValidateBeforeCall(String runId, ApiCallback _callback) throws ApiException {
        if (runId == null) {
            throw new ApiException("Missing the required parameter 'runId' when calling getApiV1DownloadRunWithRunid(Async)");
        } else {
            Call localVarCall = this.getApiV1DownloadRunWithRunidCall(runId, _callback);
            return localVarCall;
        }
    }

    public String getApiV1DownloadRunWithRunid(String runId) throws ApiException {
        ApiResponse<String> localVarResp = this.getApiV1DownloadRunWithRunidWithHttpInfo(runId);
        return (String)localVarResp.getData();
    }

    public ApiResponse<String> getApiV1DownloadRunWithRunidWithHttpInfo(String runId) throws ApiException {
        Call localVarCall = this.getApiV1DownloadRunWithRunidValidateBeforeCall(runId, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1DownloadRunWithRunidAsync(String runId, ApiCallback<String> _callback) throws ApiException {
        Call localVarCall = this.getApiV1DownloadRunWithRunidValidateBeforeCall(runId, _callback);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }

    public Call getApiV1DownloadRunWithRunidScoresCall(String runId, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/download/run/{runId}/scores".replaceAll("\\{runId\\}", this.localVarApiClient.escapeString(runId.toString()));
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();
        Map<String, String> localVarHeaderParams = new HashMap();
        Map<String, String> localVarCookieParams = new HashMap();
        Map<String, Object> localVarFormParams = new HashMap();
        String[] localVarAccepts = new String[]{"text/csv", "application/json"};
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

    private Call getApiV1DownloadRunWithRunidScoresValidateBeforeCall(String runId, ApiCallback _callback) throws ApiException {
        if (runId == null) {
            throw new ApiException("Missing the required parameter 'runId' when calling getApiV1DownloadRunWithRunidScores(Async)");
        } else {
            Call localVarCall = this.getApiV1DownloadRunWithRunidScoresCall(runId, _callback);
            return localVarCall;
        }
    }

    public String getApiV1DownloadRunWithRunidScores(String runId) throws ApiException {
        ApiResponse<String> localVarResp = this.getApiV1DownloadRunWithRunidScoresWithHttpInfo(runId);
        return (String)localVarResp.getData();
    }

    public ApiResponse<String> getApiV1DownloadRunWithRunidScoresWithHttpInfo(String runId) throws ApiException {
        Call localVarCall = this.getApiV1DownloadRunWithRunidScoresValidateBeforeCall(runId, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1DownloadRunWithRunidScoresAsync(String runId, ApiCallback<String> _callback) throws ApiException {
        Call localVarCall = this.getApiV1DownloadRunWithRunidScoresValidateBeforeCall(runId, _callback);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

package whu.vbs.DRES.dev.dres.client;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import whu.vbs.DRES.dev.dres.*;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.CurrentTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusApi {
    private ApiClient localVarApiClient;

    public StatusApi() {
        this(Configuration.getDefaultApiClient());
    }

    public StatusApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public Call getApiV1StatusTimeCall(ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/status/time";
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

    private Call getApiV1StatusTimeValidateBeforeCall(ApiCallback _callback) throws ApiException {
        Call localVarCall = this.getApiV1StatusTimeCall(_callback);
        return localVarCall;
    }

    public CurrentTime getApiV1StatusTime() throws ApiException {
        ApiResponse<CurrentTime> localVarResp = this.getApiV1StatusTimeWithHttpInfo();
        return (CurrentTime)localVarResp.getData();
    }

    public ApiResponse<CurrentTime> getApiV1StatusTimeWithHttpInfo() throws ApiException {
        Call localVarCall = this.getApiV1StatusTimeValidateBeforeCall((ApiCallback)null);
        Type localVarReturnType = (new TypeToken<CurrentTime>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1StatusTimeAsync(ApiCallback<CurrentTime> _callback) throws ApiException {
        Call localVarCall = this.getApiV1StatusTimeValidateBeforeCall(_callback);
        Type localVarReturnType = (new TypeToken<CurrentTime>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

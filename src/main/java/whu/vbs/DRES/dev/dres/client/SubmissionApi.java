package whu.vbs.DRES.dev.dres.client;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import whu.vbs.DRES.dev.dres.*;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.SuccessfulSubmissionsStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionApi {
    private ApiClient localVarApiClient;

    public SubmissionApi() {
        this(Configuration.getDefaultApiClient());
    }

    public SubmissionApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public Call getApiV1SubmitCall(String collection, String item, String text, Integer frame, Integer shot, String timecode, String session, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/submit";
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();
        Map<String, String> localVarHeaderParams = new HashMap();
        Map<String, String> localVarCookieParams = new HashMap();
        Map<String, Object> localVarFormParams = new HashMap();
        if (collection != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("collection", collection));
        }

        if (item != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("item", item));
        }

        if (text != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("text", text));
        }

        if (frame != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("frame", frame));
        }

        if (shot != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("shot", shot));
        }

        if (timecode != null) {
            localVarQueryParams.addAll(this.localVarApiClient.parameterToPair("timecode", timecode));
        }

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

    private Call getApiV1SubmitValidateBeforeCall(String collection, String item, String text, Integer frame, Integer shot, String timecode, String session, ApiCallback _callback) throws ApiException {
        Call localVarCall = this.getApiV1SubmitCall(collection, item, text, frame, shot, timecode, session, _callback);
        return localVarCall;
    }

    public SuccessfulSubmissionsStatus getApiV1Submit(String collection, String item, String text, Integer frame, Integer shot, String timecode, String session) throws ApiException {
        ApiResponse<SuccessfulSubmissionsStatus> localVarResp = this.getApiV1SubmitWithHttpInfo(collection, item, text, frame, shot, timecode, session);
        return (SuccessfulSubmissionsStatus)localVarResp.getData();
    }

    public ApiResponse<SuccessfulSubmissionsStatus> getApiV1SubmitWithHttpInfo(String collection, String item, String text, Integer frame, Integer shot, String timecode, String session) throws ApiException {
        Call localVarCall = this.getApiV1SubmitValidateBeforeCall(collection, item, text, frame, shot, timecode, session, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<SuccessfulSubmissionsStatus>() {
        }).getType();
        return this.localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    public Call getApiV1SubmitAsync(String collection, String item, String text, Integer frame, Integer shot, String timecode, String session, ApiCallback<SuccessfulSubmissionsStatus> _callback) throws ApiException {
        Call localVarCall = this.getApiV1SubmitValidateBeforeCall(collection, item, text, frame, shot, timecode, session, _callback);
        Type localVarReturnType = (new TypeToken<SuccessfulSubmissionsStatus>() {
        }).getType();
        this.localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}

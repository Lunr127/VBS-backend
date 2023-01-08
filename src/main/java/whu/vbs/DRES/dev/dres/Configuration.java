package whu.vbs.DRES.dev.dres;

public class Configuration {
    private static ApiClient defaultApiClient = new ApiClient();

    public Configuration() {
    }

    public static ApiClient getDefaultApiClient() {
        return defaultApiClient;
    }

    public static void setDefaultApiClient(ApiClient apiClient) {
        defaultApiClient = apiClient;
    }
}

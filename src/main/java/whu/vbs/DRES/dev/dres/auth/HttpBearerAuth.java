package whu.vbs.DRES.dev.dres.auth;

import whu.vbs.DRES.dev.dres.Pair;

import java.util.List;
import java.util.Map;

public class HttpBearerAuth implements Authentication {
    private final String scheme;
    private String bearerToken;

    public HttpBearerAuth(String scheme) {
        this.scheme = scheme;
    }

    public String getBearerToken() {
        return this.bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams, Map<String, String> cookieParams) {
        if (this.bearerToken != null) {
            String var10002 = this.scheme != null ? upperCaseBearer(this.scheme) + " " : "";
            headerParams.put("Authorization", var10002 + this.bearerToken);
        }
    }

    private static String upperCaseBearer(String scheme) {
        return "bearer".equalsIgnoreCase(scheme) ? "Bearer" : scheme;
    }
}

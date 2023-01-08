package whu.vbs.DRES.dev.dres.auth;

import whu.vbs.DRES.dev.dres.Pair;

import java.util.List;
import java.util.Map;

public interface Authentication {
    void applyToParams(List<Pair> var1, Map<String, String> var2, Map<String, String> var3);
}

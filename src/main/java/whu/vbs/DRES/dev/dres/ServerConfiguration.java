package whu.vbs.DRES.dev.dres;

import java.util.Iterator;
import java.util.Map;

public class ServerConfiguration {
    public String URL;
    public String description;
    public Map<String, ServerVariable> variables;

    public ServerConfiguration(String URL, String description, Map<String, ServerVariable> variables) {
        this.URL = URL;
        this.description = description;
        this.variables = variables;
    }

    public String URL(Map<String, String> variables) {
        String url = this.URL;

        String name;
        String value;
        for(Iterator var3 = this.variables.entrySet().iterator(); var3.hasNext(); url = url.replaceAll("\\{" + name + "\\}", value)) {
            Map.Entry<String, ServerVariable> variable = (Map.Entry)var3.next();
            name = (String)variable.getKey();
            ServerVariable serverVariable = (ServerVariable)variable.getValue();
            value = serverVariable.defaultValue;
            if (variables != null && variables.containsKey(name)) {
                value = (String)variables.get(name);
                if (serverVariable.enumValues.size() > 0 && !serverVariable.enumValues.contains(value)) {
                    throw new RuntimeException("The variable " + name + " in the server URL has invalid value " + value + ".");
                }
            }
        }

        return url;
    }

    public String URL() {
        return this.URL((Map)null);
    }
}

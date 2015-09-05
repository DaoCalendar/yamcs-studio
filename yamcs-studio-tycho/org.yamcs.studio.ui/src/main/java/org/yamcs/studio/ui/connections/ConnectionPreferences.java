package org.yamcs.studio.ui.connections;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 * Access point for UI preferences related to yamcs connections
 */
public class ConnectionPreferences {

    private static final String KEY_AUTO_CONNECT = "autoConnect";
    private static final String KEY_LAST_USED_CONFIGURATION = "lastUsedConf";
    private static final String KEY_CONFIGURATIONS = "confs";

    public static boolean isAutoConnect() {
        Preferences prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        return prefs.getBoolean(KEY_AUTO_CONNECT, false);
    }

    public static void setAutoConnect(boolean enabled) {
        Preferences prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        prefs.putBoolean(KEY_AUTO_CONNECT, enabled);
    }

    public static YamcsConfiguration getLastUsedConfiguration() {
        Preferences prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        String confString = prefs.get(KEY_LAST_USED_CONFIGURATION, null);
        if (confString != null)
            return new Gson().fromJson(confString, YamcsConfiguration.class);
        else
            return null;
    }

    public static void setLastUsedConfiguration(YamcsConfiguration conf) {
        Preferences prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        prefs.put(KEY_LAST_USED_CONFIGURATION, new Gson().toJson(conf));
    }

    @SuppressWarnings("serial")
    public static List<YamcsConfiguration> getConfigurations() {
        Preferences prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        String confsString = prefs.get(KEY_CONFIGURATIONS, null);
        if (confsString != null) {
            Type type = new TypeToken<List<YamcsConfiguration>>() {
            }.getType();
            List<YamcsConfiguration> confs = new Gson().fromJson(confsString, type);
            return confs;
        } else {
            return Collections.emptyList();
        }
    }

    public static void setConfigurations(List<YamcsConfiguration> confs) {
        Preferences prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        String confsString = new Gson().toJson(confs);
        prefs.put(KEY_CONFIGURATIONS, confsString);
    }
}
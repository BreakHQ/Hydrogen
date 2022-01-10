/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.frozenorb.hydrogen.util;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PermissionUtils {
    public static Map<String, Boolean> mergePermissions(Map<String, Boolean> current, Map<String, Boolean> merge) {
        if (current == null) {
            return new HashMap<String, Boolean>(merge);
        }
        if (merge == null) {
            return new HashMap<String, Boolean>(current);
        }
        HashMap<String, Boolean> result = new HashMap<String, Boolean>(current);
        result.putAll(merge);
        return result;
    }

    public static Map<String, Boolean> convertFromList(List<String> permissionsList) {
        if (permissionsList == null) {
            return ImmutableMap.of();
        }
        HashMap<String, Boolean> permissionsMap = new HashMap<String, Boolean>();
        permissionsList.forEach(permission -> {
            if (permission.startsWith("-")) {
                permissionsMap.put(permission.substring(1), false);
            } else {
                permissionsMap.put((String)permission, true);
            }
        });
        return permissionsMap;
    }

    private PermissionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}


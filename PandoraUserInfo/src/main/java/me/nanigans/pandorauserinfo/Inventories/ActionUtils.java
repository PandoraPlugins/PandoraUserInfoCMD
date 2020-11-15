package me.nanigans.pandorauserinfo.Inventories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ActionUtils {

    public static Map<String, Object> getSQLPunishData(UserInfoInventory info, ResultSet rs, String method, String type) throws SQLException {

        final String reason = rs.getString("reason");
        final String warnedByUUID = rs.getString("banned_by_uuid");
        final long time = rs.getLong("time");
        final long until = rs.getLong("until");
        final long id = rs.getLong("id");
        final boolean active = rs.getBoolean("active");

        final Map<String, Object> warnData = new HashMap<>();
        warnData.put("reason", reason);
        warnData.put("punishedByUUID", warnedByUUID);
        warnData.put("timePunished", time);
        warnData.put("expires", until);
        warnData.put("id", id);
        warnData.put("active", active);
        warnData.put("punishedUserUUID", info.getUser().getName());
        warnData.put("type", type);
        warnData.put("method", method);

        return warnData;

    }

}

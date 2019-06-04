package us.blockgame.practice.managers;

import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;
import us.blockgame.practice.sql.SQL;
import us.blockgame.practice.sql.SQLConnection;

public class SQLManager extends Manager {

    private SQLConnection sql;
    private String ip;
    private String username;
    private String password;
    private String database;

    public SQLManager(ManagerHandler managerHandler) {
        super(managerHandler);
        load();
    }

    private void load() {
        ip = managerHandler.getPlugin().getConfig().getString("sql.ip");
        username = managerHandler.getPlugin().getConfig().getString("sql.username");
        password = managerHandler.getPlugin().getConfig().getString("sql.password");
        database = managerHandler.getPlugin().getConfig().getString("sql.database");
        try {
            sql = new SQL().ip(ip).database(database).user(username).password(password).build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SQLConnection getSQL() {
        return sql;
    }
}

package io.github.akashpandit;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class KillCounterDB {
    private String dbname = "killcounter.db";

    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:plugins/Connections/" + dbname);

        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS killCounter (
                    uuid VARCHAR(36) PRIMARY KEY,
                    BLAZE int not null,
                    BOGGED int not null,
                    BREEZE int not null,
                    CREEPER int not null,
                    ELDER_GUARDIAN int not null,
                    ENDER_DRAGON int not null,
                    ENDERMAN int not null,
                    ENDERMITE int not null,
                    EVOKER int not null,
                    GHAST int not null,
                    GUARDIAN int not null,
                    HOGLIN int not null,
                    HUSK int not null,
                    IRON_GOLEM: hide
                    MAGMA_CUBE int not null,
                    PHANTOM int not null,
                    PIGLIN int not null,
                    PIGLIN_BRUTE int not null,
                    PILLAGER int not null,
                    RAVAGER int not null,
                    SHULKER int not null,
                    SILVERFISH int not null,
                    SKELETON int not null,
                    SLIME int not null,
                    SPIDER int not null,
                    STRAY int not null,
                    VINDICATOR int not null,
                    WARDEN int not null,
                    WITCH int not null,
                    WITHER int not null,
                    WITHER_SKELETON int not null,
                    ZOMBIE int not null
                );""");
    }

    public void writeToDB(HashMap<UUID, HashMap<EntityType, Integer>> killTracker) {

    }

    public void queryPlayerKills(Player player) {
        
    }
}

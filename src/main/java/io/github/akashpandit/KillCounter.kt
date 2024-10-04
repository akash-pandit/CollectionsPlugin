package io.github.akashpandit

import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.collections.MutableMap


class KillCounter(plugin: Collections) {
    private val dbname = "killCounter.db"
    private val tableName = "killCounter"

    private val configMobs = plugin.config.getConfigurationSection("mobs")!!.getKeys(false)
    private val entityTypeList: List<EntityType> = EntityType.entries.filter { it.name in configMobs }
    private val map: MutableMap<UUID, MutableMap<String, Int>> = mutableMapOf()


    fun connect(): Connection? {
//        val conn = DriverManager.getConnection("jdbc:sqlite:plugins/Connections/$dbname")
        val projectPath = "C:/Users/Akash/Coding/Java/Collections"
        val conn = DriverManager.getConnection("jdbc:sqlite:$projectPath/src/main/java/resources/$dbname")

        // create table if not exists (not sure why this would be the case unless first download / reset but ok)
        var statement = "CREATE TABLE IF NOT EXISTS killCounter(\n"
        for (mob in  entityTypeList)
            statement += "$mob.name int not null,\n"
        statement = "${statement.dropLast(2)});"

        conn.prepareStatement(statement).executeUpdate()
        return conn;
    }

    fun writePlayerMap(player: Player, conn: Connection) {
        val uuid = player.uniqueId
        var playerMap = this.map[uuid]
        if (playerMap == null) {
            playerMap = entityTypeList.associate { it.name to 0 }.toMutableMap()
            this.map[uuid] = playerMap
        }
        val update = conn.prepareStatement("INSERT OR REPLACE INTO $tableName ${mapToSql(playerMap)}")
        update.executeUpdate()
    }

    fun readToPlayerMap(player: Player, conn: Connection): MutableMap<String, Int> {
        val uuid = player.uniqueId
        val resultSet = conn.prepareStatement("SELECT * FROM $tableName WHERE uuid = $uuid").executeQuery()
        return entityTypeList.associate { it.name to resultSet.getInt(it.name) }.toMutableMap()
    }

    private fun mapToSql(playerMap: MutableMap<String, Int>): String {
        var columns = "("
        var values = "VALUES ("
        for ((mobName, killCount) in playerMap) {
            columns += "'$mobName', "
            values += "$killCount, "
        }
        return "${columns.dropLast(2)}) ${values.dropLast(2)});"
    }
}
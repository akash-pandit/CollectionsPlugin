package io.github.akashpandit

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class Collections2 : JavaPlugin(), Listener {
    private val killCounter = KillCounter(this)
    private val conn = killCounter.connect()!!

    override fun onEnable() {
        saveDefaultConfig()
        Bukkit.getPluginManager().registerEvents(this, this)
        server.pluginManager.registerEvents(MobKillListener(this), this)
    }

    override fun onDisable() {
        conn.close()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        killCounter.readToPlayerMap(player, conn)
        player.sendMessage("[Status] Mob kill data has been loaded into memory. Check with [TBD]")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        killCounter.writePlayerMap(player, conn)
        this.logger.info("[Status] Mob kill data for player ${player.name} has been written to db. Check db.")
    }
}
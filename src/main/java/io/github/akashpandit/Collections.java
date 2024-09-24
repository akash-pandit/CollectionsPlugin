package io.github.akashpandit;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

public class Collections extends JavaPlugin implements Listener {

    public HashMap<EntityType, Boolean> mobMap = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new MobKillListener(this), this);
        loadMobSet();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Yahallo " + event.getPlayer().getName() + "!"));
    }

    public void loadMobSet() {
        FileConfiguration config = this.getConfig();

        if (!config.contains("mobs")) {
            this.getLogger().warning("[WARNING] No mobs section found in config.yml");
            return;
        }
        Objects.requireNonNull(config.getConfigurationSection(
                "mobs")).getKeys(false).forEach(mobName -> {
            try {
                EntityType mob = EntityType.valueOf(mobName);
                mobMap.put(mob, config.getBoolean("mob." + mobName));
            } catch (IllegalArgumentException e) {
                this.getLogger().warning("[WARNING] Invalid mob type in config.yml: " + mobName);
            }
        });
        this.getLogger().info("[STATUS] Mob list loaded successfully");
    }
}
package io.github.akashpandit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.WeakHashMap;


public class MobKillListener implements Listener {
    private final Collections collectionsPlugin;
    private final WeakHashMap<UUID, DamageRecord> lastDmgMap = new WeakHashMap<>();
    private final long dmgTimeout = 10 * 60 * 1000;

    public HashMap<UUID, HashMap<EntityType, Integer>> killTracker = new HashMap<>();

    private static class DamageRecord {
        Player player;
        long timestamp;

        DamageRecord(Player player, long timestamp) {
            this.player = player;
            this.timestamp = timestamp;
        }
    }

    public MobKillListener(Collections collectionsPlugin) {
        this.collectionsPlugin = collectionsPlugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!collectionsPlugin.mobMap.containsKey(event.getEntityType()))
            return;

        if (event.getDamager() instanceof Player player)
            lastDmgMap.put(
                    event.getEntity().getUniqueId(),
                    new DamageRecord(player, System.currentTimeMillis())
            );
        collectionsPlugin.getLogger().info("onPlayerAdd map size %d".formatted(lastDmgMap.size()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        // check if entity is supported by plugin
        if (!collectionsPlugin.mobMap.containsKey(event.getEntityType()))
            return;

        Entity entity = event.getEntity();
        // check for entity : dmgRecord presence in map
        if (!(lastDmgMap.get(entity.getUniqueId()) instanceof DamageRecord record))
            return;

        // check for timeout
        if (System.currentTimeMillis() - record.timestamp > dmgTimeout) {
            lastDmgMap.remove(entity.getUniqueId());
            collectionsPlugin.getLogger().info("onTimeout map size %d".formatted(lastDmgMap.size()));
            return;
        }

        // grab player entity kill stats
        UUID uuid = record.player.getUniqueId();
        HashMap<EntityType, Integer> playerMobKills = killTracker.get(uuid);
        if (playerMobKills == null)
            playerMobKills = generateEmptyMap();

        // update player entity kill stats
        try {
            playerMobKills.compute(entity.getType(), (k, currentKills) -> currentKills + 1);
        } catch (NullPointerException e) {
            collectionsPlugin.getLogger().warning(
                    "[WARNING] Player " + record.player.name() +
                            " got a NullPointerException when adding a kill to " + entity.getName() + ", set to 1 kill");
            playerMobKills.put(entity.getType(), 1);
        }
        killTracker.put(uuid, playerMobKills);
        if (collectionsPlugin.mobMap.containsKey(entity.getType()))
            record.player.sendMessage("+1 kill to " + entity.getName() + ", total kills: " + playerMobKills.get(entity.getType()));
        lastDmgMap.remove(entity.getUniqueId());
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player loggedOut = event.getPlayer();
        lastDmgMap.values().removeIf(record -> record.player.equals(loggedOut));
        collectionsPlugin.getLogger().info("onLogout map size %d".formatted(lastDmgMap.size()));
    }

    public HashMap<EntityType, Integer> generateEmptyMap() {
        HashMap<EntityType, Integer> map = new HashMap<>();
        for (EntityType entityType : collectionsPlugin.mobMap.keySet())
            map.put(entityType, 0);
        return map;
    }
}



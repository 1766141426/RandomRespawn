package com.randomrespawn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import com.randomrespawn.RandomRespawn;

public class PlayerListener implements Listener {
    
    private final RandomRespawn plugin;
    
    public PlayerListener(RandomRespawn plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorld = event.getFrom().getName();
        String toWorld = player.getWorld().getName();
        
        // 检查是否从目标世界离开
        if (plugin.getConfigManager().getTargetWorlds().contains(fromWorld)) {
            // 离开目标世界，恢复原始复活点
            plugin.getRespawnManager().restoreOriginalRespawn(player);
        }
        
        // 检查是否进入目标世界
        if (plugin.getConfigManager().getTargetWorlds().contains(toWorld)) {
            // 进入目标世界，设置随机复活点
            plugin.getRespawnManager().setRandomRespawn(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 如果玩家在目标世界登录，设置随机复活点
        if (plugin.getRespawnManager().isInTargetWorld(player)) {
            plugin.getRespawnManager().setRandomRespawn(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 如果玩家在目标世界退出，恢复原始复活点
        if (plugin.getRespawnManager().isInTargetWorld(player)) {
            plugin.getRespawnManager().restoreOriginalRespawn(player);
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        // 如果玩家在目标世界死亡，确保复活点在随机位置
        if (plugin.getRespawnManager().isInTargetWorld(player)) {
            String worldName = player.getWorld().getName();
            Location randomRespawn = plugin.getConfigManager().getRandomRespawnPoint(worldName);
            
            if (randomRespawn != null) {
                event.setRespawnLocation(randomRespawn);
                player.sendMessage("§e你在随机复活世界死亡，将在随机位置复活！");
            }
        }
    }
}
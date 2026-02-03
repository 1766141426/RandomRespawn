package com.randomrespawn.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import com.randomrespawn.RandomRespawn;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RespawnManager {
    
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private FileConfiguration playerData;
    private File playerDataFile;
    
    // 缓存玩家原始复活点
    private Map<UUID, Location> originalRespawnLocations;
    
    public RespawnManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configManager = ((RandomRespawn) plugin).getConfigManager();
        this.originalRespawnLocations = new HashMap<>();
        loadPlayerData();
    }
    
    private void loadPlayerData() {
        playerDataFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("创建玩家数据文件失败: " + e.getMessage());
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }
    
    public void saveOriginalRespawnData() {
        for (Map.Entry<UUID, Location> entry : originalRespawnLocations.entrySet()) {
            Location loc = entry.getValue();
            if (loc != null) {
                String path = "original-respawn." + entry.getKey().toString();
                playerData.set(path + ".world", loc.getWorld().getName());
                playerData.set(path + ".x", loc.getX());
                playerData.set(path + ".y", loc.getY());
                playerData.set(path + ".z", loc.getZ());
                playerData.set(path + ".yaw", (double) loc.getYaw());
                playerData.set(path + ".pitch", (double) loc.getPitch());
            }
        }
        
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保存玩家数据失败: " + e.getMessage());
        }
    }
    
    public void setRandomRespawn(Player player) {
        String worldName = player.getWorld().getName();
        
        if (!configManager.getTargetWorlds().contains(worldName)) {
            return;
        }
        
        // 保存原始复活点
        Location originalRespawn = player.getBedSpawnLocation();
        if (originalRespawn == null) {
            originalRespawn = player.getWorld().getSpawnLocation();
        }
        
        originalRespawnLocations.put(player.getUniqueId(), originalRespawn);
        
        // 获取随机复活点
        Location randomRespawn = configManager.getRandomRespawnPoint(worldName);
        if (randomRespawn != null) {
            player.setBedSpawnLocation(randomRespawn, true);
            player.sendMessage("§a你已进入随机复活世界！复活点已设置为随机位置。");
        }
    }
    
    public void restoreOriginalRespawn(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (originalRespawnLocations.containsKey(playerId)) {
            Location originalLocation = originalRespawnLocations.get(playerId);
            if (originalLocation != null) {
                player.setBedSpawnLocation(originalLocation, true);
                player.sendMessage("§a你已离开随机复活世界！复活点已恢复为原始设置。");
            }
            originalRespawnLocations.remove(playerId);
        } else {
            // 尝试从文件加载
            String path = "original-respawn." + playerId.toString();
            if (playerData.contains(path)) {
                String worldName = playerData.getString(path + ".world");
                double x = playerData.getDouble(path + ".x");
                double y = playerData.getDouble(path + ".y");
                double z = playerData.getDouble(path + ".z");
                float yaw = (float) playerData.getDouble(path + ".yaw");
                float pitch = (float) playerData.getDouble(path + ".pitch");
                
                org.bukkit.World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    Location originalLocation = new Location(world, x, y, z, yaw, pitch);
                    player.setBedSpawnLocation(originalLocation, true);
                    player.sendMessage("§a你已离开随机复活世界！复活点已恢复为原始设置。");
                }
                
                // 清理数据
                playerData.set(path, null);
                try {
                    playerData.save(playerDataFile);
                } catch (IOException e) {
                    plugin.getLogger().severe("清理玩家数据失败: " + e.getMessage());
                }
            }
        }
    }
    
    public boolean isInTargetWorld(Player player) {
        return configManager.getTargetWorlds().contains(player.getWorld().getName());
    }
    
    public void clearPlayerData(UUID playerId) {
        originalRespawnLocations.remove(playerId);
        
        String path = "original-respawn." + playerId.toString();
        if (playerData.contains(path)) {
            playerData.set(path, null);
            try {
                playerData.save(playerDataFile);
            } catch (IOException e) {
                plugin.getLogger().severe("清理玩家数据失败: " + e.getMessage());
            }
        }
    }
}
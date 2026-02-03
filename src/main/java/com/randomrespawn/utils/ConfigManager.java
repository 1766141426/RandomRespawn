package com.randomrespawn.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration data;
    private File dataFile;
    
    // 配置缓存
    private Set<String> targetWorlds;
    private Map<String, List<Location>> respawnPoints;
    private int randomRadius;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.respawnPoints = new HashMap<>();
        this.targetWorlds = new HashSet<>();
    }
    
    public void loadConfig() {
        // 创建默认配置
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // 加载目标世界
        targetWorlds.clear();
        List<String> worlds = config.getStringList("target-worlds");
        if (worlds.isEmpty()) {
            // 默认配置
            worlds = Arrays.asList("world_nether", "world_the_end");
            config.set("target-worlds", worlds);
            plugin.saveConfig();
        }
        targetWorlds.addAll(worlds);
        
        // 加载随机半径
        randomRadius = config.getInt("random-radius", 10);
        
        // 加载数据文件
        loadDataFile();
        
        // 加载复活点
        loadRespawnPoints();
    }
    
    private void loadDataFile() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    private void loadRespawnPoints() {
        respawnPoints.clear();
        
        for (String worldName : targetWorlds) {
            List<Location> points = new ArrayList<>();
            List<Map<String, Object>> pointList = (List<Map<String, Object>>) data.getList("respawn-points." + worldName);
            
            if (pointList != null) {
                for (Map<String, Object> pointData : pointList) {
                    World world = plugin.getServer().getWorld(worldName);
                    if (world != null) {
                        double x = (double) pointData.get("x");
                        double y = (double) pointData.get("y");
                        double z = (double) pointData.get("z");
                        float yaw = ((Double) pointData.get("yaw")).floatValue();
                        float pitch = ((Double) pointData.get("pitch")).floatValue();
                        
                        Location loc = new Location(world, x, y, z, yaw, pitch);
                        points.add(loc);
                    }
                }
            }
            
            respawnPoints.put(worldName, points);
        }
    }
    
    public void saveRespawnPoint(String worldName, Location location) {
        List<Location> points = respawnPoints.getOrDefault(worldName, new ArrayList<>());
        points.add(location);
        respawnPoints.put(worldName, points);
        
        // 保存到文件
        saveRespawnPointsToFile();
    }
    
    public void removeRespawnPoint(String worldName, int index) {
        List<Location> points = respawnPoints.get(worldName);
        if (points != null && index >= 0 && index < points.size()) {
            points.remove(index);
            saveRespawnPointsToFile();
        }
    }
    
    private void saveRespawnPointsToFile() {
        for (String worldName : respawnPoints.keySet()) {
            List<Map<String, Object>> pointList = new ArrayList<>();
            List<Location> points = respawnPoints.get(worldName);
            
            for (Location loc : points) {
                Map<String, Object> pointData = new HashMap<>();
                pointData.put("x", loc.getX());
                pointData.put("y", loc.getY());
                pointData.put("z", loc.getZ());
                pointData.put("yaw", (double) loc.getYaw());
                pointData.put("pitch", (double) loc.getPitch());
                pointList.add(pointData);
            }
            
            data.set("respawn-points." + worldName, pointList);
        }
        
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保存复活点数据失败: " + e.getMessage());
        }
    }
    
    public Location getRandomRespawnPoint(String worldName) {
        List<Location> points = respawnPoints.get(worldName);
        if (points == null || points.isEmpty()) {
            return null;
        }
        
        Random random = new Random();
        Location baseLocation = points.get(random.nextInt(points.size()));
        
        // 如果设置了随机半径，在半径内随机偏移
        if (randomRadius > 0) {
            double offsetX = (random.nextDouble() * 2 - 1) * randomRadius;
            double offsetZ = (random.nextDouble() * 2 - 1) * randomRadius;
            
            return baseLocation.clone().add(offsetX, 0, offsetZ);
        }
        
        return baseLocation.clone();
    }
    
    public Set<String> getTargetWorlds() {
        return new HashSet<>(targetWorlds);
    }
    
    public List<Location> getRespawnPoints(String worldName) {
        return new ArrayList<>(respawnPoints.getOrDefault(worldName, new ArrayList<>()));
    }
    
    public int getRandomRadius() {
        return randomRadius;
    }
    
    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }
}
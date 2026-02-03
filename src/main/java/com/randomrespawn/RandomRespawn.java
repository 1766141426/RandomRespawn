package com.randomrespawn;

import org.bukkit.plugin.java.JavaPlugin;
import com.randomrespawn.commands.RandomRespawnCommand;
import com.randomrespawn.listeners.PlayerListener;
import com.randomrespawn.utils.ConfigManager;
import com.randomrespawn.utils.RespawnManager;

public class RandomRespawn extends JavaPlugin {
    
    private static RandomRespawn instance;
    private ConfigManager configManager;
    private RespawnManager respawnManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化管理器
        configManager = new ConfigManager(this);
        respawnManager = new RespawnManager(this);
        
        // 加载配置
        configManager.loadConfig();
        
        // 注册监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // 注册命令
        getCommand("randomrespawn").setExecutor(new RandomRespawnCommand(this));
        getCommand("rr").setExecutor(new RandomRespawnCommand(this));
        
        getLogger().info("随机复活点插件已启用！");
        getLogger().info("目标世界: " + String.join(", ", configManager.getTargetWorlds()));
    }
    
    @Override
    public void onDisable() {
        // 保存玩家原始复活点数据
        respawnManager.saveOriginalRespawnData();
        
        getLogger().info("随机复活点插件已禁用！");
    }
    
    public static RandomRespawn getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public RespawnManager getRespawnManager() {
        return respawnManager;
    }
}
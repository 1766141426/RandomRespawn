package com.randomrespawn.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import com.randomrespawn.RandomRespawn;
import com.randomrespawn.utils.ConfigManager;

import java.util.*;

public class RandomRespawnCommand implements CommandExecutor, TabCompleter {
    
    private final RandomRespawn plugin;
    private final ConfigManager configManager;
    
    public RandomRespawnCommand(RandomRespawn plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "add":
                return handleAdd(sender, args);
            case "remove":
                return handleRemove(sender, args);
            case "list":
                return handleList(sender, args);
            case "reload":
                return handleReload(sender);
            case "help":
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }
        
        if (!sender.hasPermission("randomrespawn.add") && !sender.hasPermission("randomrespawn.admin")) {
            sender.sendMessage("§c你没有权限添加复活点！");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§c用法: /" + label + " add <世界名称>");
            return true;
        }
        
        Player player = (Player) sender;
        String worldName = args[1];
        
        // 检查世界是否存在
        if (plugin.getServer().getWorld(worldName) == null) {
            sender.sendMessage("§c世界 '" + worldName + "' 不存在！");
            return true;
        }
        
        // 检查是否为目标世界
        if (!configManager.getTargetWorlds().contains(worldName)) {
            sender.sendMessage("§c世界 '" + worldName + "' 不是目标世界，请先在配置中添加！");
            return true;
        }
        
        Location location = player.getLocation();
        configManager.saveRespawnPoint(worldName, location);
        
        sender.sendMessage("§a成功在世界 '" + worldName + "' 添加复活点！");
        sender.sendMessage("§7坐标: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
        return true;
    }
    
    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("randomrespawn.remove") && !sender.hasPermission("randomrespawn.admin")) {
            sender.sendMessage("§c你没有权限删除复活点！");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage("§c用法: /" + label + " remove <世界名称> <序号>");
            return true;
        }
        
        String worldName = args[1];
        int index;
        
        try {
            index = Integer.parseInt(args[2]) - 1; // 转换为0基索引
        } catch (NumberFormatException e) {
            sender.sendMessage("§c序号必须是数字！");
            return true;
        }
        
        List<Location> points = configManager.getRespawnPoints(worldName);
        if (points.isEmpty()) {
            sender.sendMessage("§c世界 '" + worldName + "' 没有复活点！");
            return true;
        }
        
        if (index < 0 || index >= points.size()) {
            sender.sendMessage("§c序号无效！有效范围: 1-" + points.size());
            return true;
        }
        
        configManager.removeRespawnPoint(worldName, index);
        sender.sendMessage("§a成功删除世界 '" + worldName + "' 的第 " + (index + 1) + " 个复活点！");
        return true;
    }
    
    private boolean handleList(CommandSender sender, String[] args) {
        if (!sender.hasPermission("randomrespawn.list") && !sender.hasPermission("randomrespawn.admin")) {
            sender.sendMessage("§c你没有权限查看复活点列表！");
            return true;
        }
        
        if (args.length < 2) {
            // 列出所有世界的复活点
            sender.sendMessage("§6=== 随机复活点列表 ===");
            for (String worldName : configManager.getTargetWorlds()) {
                List<Location> points = configManager.getRespawnPoints(worldName);
                sender.sendMessage("§e世界: §f" + worldName + " §7(共 " + points.size() + " 个复活点)");
                
                for (int i = 0; i < points.size(); i++) {
                    Location loc = points.get(i);
                    sender.sendMessage("  §7" + (i + 1) + ". §f" + 
                        loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                }
            }
        } else {
            // 列出指定世界的复活点
            String worldName = args[1];
            List<Location> points = configManager.getRespawnPoints(worldName);
            
            sender.sendMessage("§6=== 世界 '" + worldName + "' 的复活点 ===");
            sender.sendMessage("§7共 " + points.size() + " 个复活点");
            
            for (int i = 0; i < points.size(); i++) {
                Location loc = points.get(i);
                sender.sendMessage("§e" + (i + 1) + ". §f" + 
                    loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
            }
        }
        
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("randomrespawn.reload") && !sender.hasPermission("randomrespawn.admin")) {
            sender.sendMessage("§c你没有权限重载配置！");
            return true;
        }
        
        configManager.reload();
        sender.sendMessage("§a随机复活点插件配置已重载！");
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== 随机复活点插件帮助 ===");
        sender.sendMessage("§e/" + label + " add <世界> §7- 添加当前坐标为复活点");
        sender.sendMessage("§e/" + label + " remove <世界> <序号> §7- 删除复活点");
        sender.sendMessage("§e/" + label + " list [世界] §7- 查看复活点列表");
        sender.sendMessage("§e/" + label + " reload §7- 重载配置");
        sender.sendMessage("§e/" + label + " help §7- 显示此帮助");
        
        if (sender.hasPermission("randomrespawn.admin")) {
            sender.sendMessage("§6目标世界: §f" + String.join(", ", configManager.getTargetWorlds()));
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 子命令补全
            List<String> subCommands = Arrays.asList("add", "remove", "list", "reload", "help");
            for (String subCmd : subCommands) {
                if (subCmd.startsWith(args[0].toLowerCase())) {
                    completions.add(subCmd);
                }
            }
        } else if (args.length == 2) {
            // 世界名称补全
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("add") || subCommand.equals("remove") || subCommand.equals("list")) {
                for (String worldName : configManager.getTargetWorlds()) {
                    if (worldName.startsWith(args[1])) {
                        completions.add(worldName);
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            // 删除时的序号补全
            String worldName = args[1];
            List<Location> points = configManager.getRespawnPoints(worldName);
            for (int i = 1; i <= points.size(); i++) {
                completions.add(String.valueOf(i));
            }
        }
        
        return completions;
    }
}
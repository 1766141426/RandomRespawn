# RandomRespawn 插件构建说明

## 项目结构
```
RandomRespawn/
├── src/main/java/com/randomrespawn/
│   ├── RandomRespawn.java              # 主类
│   ├── commands/RandomRespawnCommand.java  # 命令处理器
│   ├── listeners/PlayerListener.java   # 事件监听器
│   └── utils/
│       ├── ConfigManager.java          # 配置管理器
│       └── RespawnManager.java         # 复活点管理器
├── src/main/resources/
│   ├── plugin.yml                      # 插件描述文件
│   ├── config.yml                      # 配置文件模板
│   ├── data.yml                        # 数据文件模板
│   └── players.yml                     # 玩家数据模板
├── README.md                           # 使用说明
└── BUILD.md                            # 构建说明
```

## 编译步骤

### 方法一：使用 ScriptIrc 编译（推荐）
1. **导出项目**：点击左侧「导出项目」按钮，下载 `RandomRespawn.sirc` 文件
2. **放置源码**：将 `.sirc` 文件放入服务器目录：
   ```
   plugins/ScriptIrc/scripts/src/
   ```
3. **编译插件**：在游戏内或控制台执行：
   ```
   /scriptirc compiler RandomRespawn
   ```
4. **检查编译**：编译成功后，会在以下位置生成插件：
   ```
   plugins/ScriptIrc/scripts/compiled/RandomRespawn.jar
   ```
5. **启用插件**：将生成的 `.jar` 文件移动到 `plugins/` 目录，重启服务器

### 方法二：手动编译（需要 Java 开发环境）
1. **准备依赖**：下载 Spigot/Bukkit API（1.20.x 版本）
2. **编译命令**：
   ```bash
   javac -cp "spigot-api-1.20.x.jar" -d bin src/main/java/com/randomrespawn/**/*.java
   ```
3. **打包**：
   ```bash
   jar cf RandomRespawn.jar -C bin . -C src/main/resources .
   ```

## 文件说明

### 核心文件
1. **plugin.yml** - 插件元数据，包含：
   - 插件名称：RandomRespawn
   - 主类：com.randomrespawn.RandomRespawn
   - 版本：1.0.0
   - 命令和权限配置

2. **config.yml** - 主配置文件：
   - `target-worlds`: 启用随机复活的世界列表
   - `random-radius`: 随机半径（0=精确坐标，>0=半径内随机）

3. **data.yml** - 复活点数据存储（自动生成）

4. **players.yml** - 玩家原始复活点数据（自动生成）

## 验证编译

编译成功后，插件应具备以下功能：

1. **基础功能**：
   - 插件正常加载，无报错
   - 配置文件自动生成
   - 命令 `/randomrespawn help` 可用

2. **核心功能**：
   - 玩家进入目标世界时收到提示
   - 复活点随机设置
   - 离开目标世界时恢复原复活点

3. **管理功能**：
   - 管理员可以添加/删除/查看复活点
   - 配置重载功能正常

## 常见问题

### 编译失败
- **错误**: "package org.bukkit does not exist"
  - 原因：缺少 Bukkit/Spigot API
  - 解决：确保使用正确的服务端版本（1.20.x）

- **错误**: "cannot find symbol"
  - 原因：代码语法错误或导入错误
  - 解决：检查所有 Java 文件的导入语句

### 插件加载失败
- **错误**: "main class not found"
  - 原因：plugin.yml 中的主类路径错误
  - 解决：确认主类为 `com.randomrespawn.RandomRespawn`

- **错误**: "no such command"
  - 原因：命令未正确注册
  - 解决：检查 plugin.yml 中的 commands 配置

## 测试建议

1. **基础测试**：
   - 加载插件，检查控制台输出
   - 执行 `/randomrespawn help` 查看命令

2. **功能测试**：
   - 进入目标世界，检查复活点是否随机设置
   - 离开目标世界，检查复活点是否恢复
   - 在目标世界死亡，检查是否在随机位置复活

3. **管理测试**：
   - 添加复活点：`/randomrespawn add world_nether`
   - 查看列表：`/randomrespawn list`
   - 重载配置：`/randomrespawn reload`

## 版本信息
- Minecraft 版本：1.20.x
- 插件版本：1.0.0
- 创建时间：$(date)
- 作者：wp:1128
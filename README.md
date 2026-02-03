# RandomRespawn - 随机复活点插件

一个在指定世界随机设置玩家复活点的 Bukkit/Spigot 插件。

## 功能特性

- **智能复活点管理**：玩家进入指定世界时自动设置随机复活点，离开时恢复原复活点
- **多世界支持**：可配置多个世界启用随机复活功能
- **灵活配置**：每个世界的复活点数量可自定义，支持随机半径偏移
- **数据持久化**：保存玩家原始复活点，确保数据安全
- **完整管理命令**：管理员可以轻松添加、删除、查看复活点

## 安装方法

1. 将编译后的 `.jar` 文件放入服务器的 `plugins` 文件夹
2. 重启服务器
3. 编辑 `plugins/RandomRespawn/config.yml` 配置目标世界
4. 使用命令添加复活点

## 配置说明

### config.yml
```yaml
# 启用随机复活的世界列表
target-worlds:
  - world_nether
  - world_the_end
  # 可以添加更多世界

# 随机半径（格）
random-radius: 10
```

### 权限节点
- `randomrespawn.use` - 使用随机复活功能（默认所有玩家）
- `randomrespawn.admin` - 管理员权限（包含所有子权限）
- `randomrespawn.add` - 添加复活点
- `randomrespawn.remove` - 删除复活点
- `randomrespawn.list` - 查看复活点列表
- `randomrespawn.reload` - 重载配置

## 命令使用

### 玩家命令
- `/randomrespawn help` - 显示帮助信息
- 别名：`/rr help`

### 管理员命令
- `/randomrespawn add <世界名称>` - 在当前位置添加复活点
- `/randomrespawn remove <世界名称> <序号>` - 删除指定复活点
- `/randomrespawn list [世界名称]` - 查看复活点列表
- `/randomrespawn reload` - 重载插件配置

## 使用流程

1. **配置目标世界**
   - 编辑 `config.yml`，在 `target-worlds` 列表中添加世界名称

2. **添加复活点**
   - 进入目标世界
   - 站在想要设置为复活点的位置
   - 执行 `/randomrespawn add <世界名称>`

3. **验证功能**
   - 玩家进入目标世界时，会收到提示信息
   - 玩家死亡时会随机复活到预设点之一
   - 玩家离开目标世界时，复活点自动恢复

## 数据文件

- `config.yml` - 主配置文件
- `data.yml` - 复活点数据（自动生成）
- `players.yml` - 玩家原始复活点数据（自动生成）

## 注意事项

1. **世界名称必须正确**：配置中的世界名称必须与服务器中的世界文件夹名称一致
2. **权限管理**：建议给管理员分配 `randomrespawn.admin` 权限
3. **备份建议**：定期备份 `data.yml` 文件，防止数据丢失
4. **性能优化**：每个世界的复活点数量建议控制在 20 个以内

## 兼容性

- Minecraft 版本：1.20.x
- 服务端类型：Bukkit / Spigot / Paper
- 依赖插件：无

## 故障排除

### 问题：玩家进入目标世界没有效果
- 检查世界名称是否正确
- 检查是否添加了复活点
- 检查玩家是否有 `randomrespawn.use` 权限

### 问题：命令无法使用
- 检查插件是否正确加载
- 检查玩家是否有相应权限
- 检查命令拼写是否正确

### 问题：复活点位置不准确
- 检查 `random-radius` 配置
- 设置为 0 使用精确坐标
- 大于 0 时会在半径内随机偏移

## 更新日志

### v1.0.0
- 初始版本发布
- 基本随机复活功能
- 完整的管理命令系统
- 数据持久化支持
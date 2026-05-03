# ihub CLI

IHub 的命令行入口工具。提供项目初始化、AI 元数据生成、构建检查等命令。

## 定位

CLI 是开发者在终端中使用 IHub 的直接入口——通过一条命令即可生成 AI 友好元数据、检查项目健康度、快速接入 IHub 能力。

## 技术栈

- Java 17 + Picocli
- P2 评估 GraalVM Native Image 编译为独立二进制
- 详见 [ADR-0003](../docs/adr/0003-cli-technology-choice.md)

## 命令

| 命令 | 功能 | 状态 |
|------|------|------|
| `ihub init` | 初始化 IHub 项目结构 | 🚧 骨架 |
| `ihub meta` | 输出项目 AI 元数据（JSON） | 🚧 骨架 |
| `ihub check` | 检查项目健康度与最佳实践 | 🚧 骨架 |

## 运行

```bash
# 开发模式
./gradlew :cli:run --args="meta"

# 构建
./gradlew :cli:build
```

## 状态

🚧 P1 阶段：骨架搭建完成，命令实现待 P2。

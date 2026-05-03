# ihub CLI

IHub 的命令行入口工具。提供项目初始化、构建检查、AI 上下文生成等命令。

## 定位

CLI 是开发者在终端中使用 IHub 的直接入口——通过一条命令即可生成 AI 友好元数据、检查项目健康度、快速接入 IHub 能力。

## 计划命令

| 命令 | 功能 |
|------|------|
| `ihub init` | 初始化 IHub 项目结构 |
| `ihub meta` | 输出项目 AI 元数据（JSON） |
| `ihub check` | 检查项目健康度与最佳实践 |

## 技术方向

- 技术栈待 ADR 决策（Go / Java + GraalVM Native Image / Rust）
- 优先支持 Gradle 项目（与 plugins 板块协同）

## 状态

🚧 P1 阶段：骨架搭建 + 技术选型 ADR，待 P2 实现。

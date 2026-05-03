# IHub Agents

**IHub 的统一对外接口层** — MCP Server、CLI、Skills、Runtime 四个组件，为开发者提供从命令行到 AI 集成的完整入口。

## Architecture

IHub 采用三层架构，agents 位于对外接口层（L3）：

```
┌─────────────────────────────────────────────┐
│  L1  IHub Hub（私有中枢· ihub）               │
├─────────────────────────────────────────────┤
│  L2  能力板块（plugins / libs / integrations │
│      / modules）                             │
├─────────────────────────────────────────────┤
│  L3  对外接口（本仓库· agents）               │
│      MCP · CLI · Skills · Runtime           │
└─────────────────────────────────────────────┘
```

## Components

| 组件 | 定位 | 状态 |
|------|------|------|
| [mcp-server](./mcp-server/) | MCP Server 骨架，为 AI 工具提供标准化接口 | 🚧 规划中 |
| [cli](./cli/) | ihub CLI 命令行入口 | 🚧 规划中 |
| [skills](./skills/) | Skills 目录，Skill 即插件市场的基础形态 | 🚧 规划中 |
| [runtime](./runtime/) | Skills Runtime 引擎 | 🚧 规划中 |

## Quick Start

> 即将提供。当前阶段为架构搭建期，具体实现始于 P2。

## Contributing

- 所有架构决策通过 ADR 记录，存放于 `docs/adr/`
- PR 需包含变更说明，涉及架构变更需附 ADR
- 遵循 IHub 四大核心原则：整合者非发明者、AI 友好基础设施、自我 Dogfooding、丝滑迁移

## Roadmap

| 阶段 | 内容 |
|------|------|
| P1 · 基础建设 | 仓库初始化、组件骨架、接口定义 |
| P2 · 能力扩展 | MCP Server 实现、CLI 首版、Skills 运行时 |
| P3 · 生态闭环 | 多语言支持、插件/Skill 市场、社区建设 |

## License

Apache 2.0

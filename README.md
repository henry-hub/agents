# IHub Agents

**IHub 的统一对外接口层（L3）** — MCP Server、CLI、Skills、Runtime 四个组件，为开发者提供从命令行到 AI 集成的完整入口。

## 架构定位

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

## 项目结构

```
agents/
├── mcp-server/          # MCP Server（Spring Boot + Spring AI MCP）
│   ├── src/main/java/pub/ihub/agent/mcp/
│   │   ├── IHubMcpServerApplication.java
│   │   └── tools/CatalogTools.java
│   └── build.gradle.kts
├── cli/                 # ihub CLI（Java + Picocli）
│   ├── src/main/java/pub/ihub/agent/cli/
│   │   └── IHubCli.java
│   └── build.gradle.kts
├── skills/              # Skills 目录（语言无关的能力单元）
│   ├── manifest.schema.yaml
│   ├── README.md
│   └── examples/hello-world/
│       ├── manifest.yaml
│       └── run.sh
├── runtime/             # Skills Runtime 引擎（P2 实现）
│   └── README.md
├── docs/adr/            # 架构决策记录
├── build.gradle.kts     # 根构建（使用 IHub plugins）
├── settings.gradle.kts
└── gradle/libs.versions.toml
```

## 组件

| 组件 | 定位 | 技术栈 | 状态 |
|------|------|--------|------|
| [mcp-server](./mcp-server/) | AI 工具的统一 MCP 接口 | Spring Boot 4.x + Spring AI MCP | 🚧 骨架 |
| [cli](./cli/) | ihub 命令行入口 | Java 17 + Picocli | 🚧 骨架 |
| [skills](./skills/) | 可编排的能力单元 | 语言无关（Shell/Python/JS/Java） | 🚧 规范定义 |
| [runtime](./runtime/) | Skills 执行引擎 | 待定（P2 决策） | 🔮 规划 |

## 快速开始

### 构建

```bash
# 构建全部模块
./gradlew build

# 运行 MCP Server
./gradlew :mcp-server:bootRun

# 运行 CLI
./gradlew :cli:run

# 生成 AI 元数据
./gradlew iHubMeta
```

### Skills

Skills 是语言无关的能力单元，不参与 Gradle 编译。查看 [skills/README.md](./skills/README.md) 了解 Skill 的结构约定。

## Dogfooding

agents 自身使用 IHub plugins 构建：
- `pub.ihub.plugin.ihub-settings` — 仓库与版本管理
- `pub.ihub.plugin.ihub-java` — Java 编译配置
- `pub.ihub.plugin.ihub-boot` — Spring Boot（MCP Server）
- `pub.ihub.plugin.ihub-meta` — AI 元数据生成
- `pub.ihub.plugin.ihub-skills` — AI 技能文件安装

## 架构决策

所有重要架构决策通过 ADR 记录在 [docs/adr/](./docs/adr/)：
- [ADR-0002](docs/adr/0002-agents-gradle-build.md) — 使用 Gradle + IHub Plugins 构建
- [ADR-0003](docs/adr/0003-cli-technology-choice.md) — CLI 技术选型（Java + Picocli）

## 路线图

| 阶段 | 内容 |
|------|------|
| P1 · 基础建设 | 仓库骨架、接口定义、ADR、Skills 规范 |
| P2 · 能力扩展 | MCP Server 实现、CLI 首版、Skills Runtime |
| P3 · 生态闭环 | 多语言支持、Skill 市场、社区建设 |

## License

Apache 2.0

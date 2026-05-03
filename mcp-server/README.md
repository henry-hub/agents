# MCP Server

基于 Spring AI MCP (Model Context Protocol) 的标准化接口服务，为 AI 工具（Claude、Cursor、GitHub Copilot 等）提供 IHub 能力的统一访问入口。

## 定位

MCP Server 是 IHub 面向 AI 的"统一 API 层"——AI 工具通过 MCP 协议发现和调用 IHub 的能力（组件目录查询、项目管理、代码分析等），无需分别对接不同模块。

## 技术栈

- Spring Boot 4.x
- Spring AI MCP Server
- 支持 stdio 和 HTTP 两种传输模式

## 项目结构

```
mcp-server/
├── src/main/java/pub/ihub/agent/mcp/
│   ├── IHubMcpServerApplication.java   # Spring Boot 入口
│   └── tools/
│       └── CatalogTools.java           # MCP Tools — 能力目录查询
├── src/main/resources/
│   └── application.yml                 # MCP Server 配置
└── build.gradle.kts
```

## 已规划的 MCP Tools

| Tool | 说明 | 状态 |
|------|------|------|
| `listDomains` | 列出所有能力领域 | 🚧 骨架 |
| `searchCatalog` | 搜索能力目录 | 🚧 骨架 |
| `getComponent` | 获取组件详情 | 🔮 P2 |
| `getAlternatives` | 获取替代方案 | 🔮 P2 |
| `resolveDependencies` | GAV → 目录条目解析 | 🔮 P2 |
| `stageRecommendations` | 旅程阶段推荐组件 | 🔮 P2 |

## 运行

```bash
# 开发模式
./gradlew :mcp-server:bootRun

# 构建
./gradlew :mcp-server:build
```

## 状态

🚧 P1 阶段：骨架搭建完成，具体 Tool 实现待 P2。

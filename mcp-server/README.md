# MCP Server

基于 Spring AI 2.0 MCP (Model Context Protocol) 的标准化接口服务，为 AI 工具（Claude、Cursor、GitHub Copilot 等）提供 IHub 能力的统一访问入口。

## 定位

MCP Server 是 IHub 面向 AI 的"统一 API 层"——AI 工具通过 MCP 协议发现和调用 IHub 的能力（组件目录查询、业务模块编排、迁移分析等），无需分别对接不同模块。

## 技术栈

- Spring Boot 4.x
- Spring AI 2.0（MCP Java SDK 2.0，对齐 2025-11-25 MCP 规格）
- 支持 stdio 和 Streamable HTTP 传输模式

## 项目结构

```
mcp-server/
├── src/main/java/pub/ihub/agent/mcp/
│   ├── IHubMcpServerApplication.java   # Spring Boot 入口
│   ├── tools/
│   │   ├── CatalogTools.java           # 能力目录查询
│   │   ├── ModuleTools.java            # 业务模块编排
│   │   └── MigrateTools.java           # 迁移分析
│   ├── catalog/
│   │   ├── CatalogService.java         # catalog.json 加载与检索
│   │   ├── CatalogEntry.java           # 组件条目模型
│   │   └── CatalogRoot.java            # catalog 顶层结构
│   └── module/
│       ├── ModuleService.java          # 模块描述符服务
│       └── ModuleDescriptor.java       # 模块描述符模型
├── src/main/resources/
│   ├── application.yml                 # MCP Server 配置
│   └── ihub-catalog/catalog.json       # 能力目录数据（从 libs 同步）
└── build.gradle.kts
```

## MCP Tools

| Tool | 说明 | 状态 |
|------|------|------|
| `listDomains` | 列出所有能力领域及组件数量 | ✅ 已实现 |
| `listDomainComponents` | 列出指定领域的组件列表 | ✅ 已实现 |
| `searchCatalog` | 多关键词搜索能力目录 | ✅ 已实现 |
| `getComponent` | 获取组件完整详情（含 ai_context） | ✅ 已实现 |
| `listModules` | 列出可用的业务模块 | ✅ 已实现 |
| `getModule` | 获取模块描述符详情 | ✅ 已实现 |
| `analyzeProject` | 分析项目的迁移需求 | ✅ 已实现 |
| `getAlternatives` | 获取替代方案 | 📋 P2 |
| `stageRecommendations` | 旅程阶段推荐组件 | 📋 P2 |

## 数据来源

- **能力目录**：`ihub-catalog/catalog.json`（从 [libs](https://github.com/henryjxs/libs) 仓库同步，运行 `scripts/sync-catalog.sh`）
- **模块描述符**：从 [modules](https://github.com/henryjxs/modules) 仓库的 `module-descriptor.json` 加载

## 运行

```bash
# 开发模式
./gradlew :mcp-server:bootRun

# 构建
./gradlew :mcp-server:build
```

## 状态

✅ 已实现：核心 Catalog / Module / Migrate 工具可用，基于 Spring AI 2.0 + MCP SDK 2.0。

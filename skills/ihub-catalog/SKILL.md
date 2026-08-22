---
name: ihub-catalog
description: 为 Java/Spring Boot 项目提供 IHub 能力目录查询与技术选型推荐。当用户询问用什么 ORM、认证框架、分布式锁、工作流引擎、Excel 工具，或需要了解组件集成方式、替代方案、常见陷阱时使用。
---

# IHub 能力目录查询

当用户需要为 Java/Spring Boot 项目选择技术组件时，使用 IHub 能力目录提供结构化推荐。

## 触发场景

- 用户询问"用什么 ORM / 认证框架 / 分布式锁 / 工作流引擎"
- 用户需要技术选型建议
- 用户想了解某个组件的集成方式、替代方案、常见陷阱

## 执行步骤

### 1. 查询能力目录

IHub 能力目录位于 `libs/gradle/ihub-catalog/`，包含 13 个领域、39 个组件的结构化描述。

**方式 A：通过 MCP 工具（推荐）**

如果 IHub MCP Server 可用，使用以下工具：
- `listDomains` — 浏览所有能力领域
- `searchCatalog(query)` — 多关键词搜索（支持中英文，空格分隔 AND 语义）
- `getComponent(id)` — 获取组件完整详情，包含 `ai_context`（集成代码示例）

**方式 B：直接读取文件**

```
libs/gradle/ihub-catalog/domains/{domain}.json   # 按领域浏览
libs/gradle/ihub-catalog/catalog.json             # 完整目录（一次性加载）
```

### 2. 提供推荐

基于目录中的以下字段回答：
- `description` — 组件是什么
- `use_case` — 何时使用、典型场景
- `ai_context` — 集成模式、配置要点、约束限制、常见陷阱
- `alternatives` — 替代方案
- `gradle_ref` — Gradle 依赖别名（可直接用于 build.gradle.kts）

### 3. 输出格式

推荐时按以下结构输出：

```
## 推荐：{组件名}

**领域**：{domain} · **状态**：{status}
**Gradle 依赖**：`libs.{gradle_ref}`

### 适用场景
{use_case}

### 集成要点
{ai_context 中的关键信息}

### 替代方案
{alternatives 列表及各自优劣}
```

## 领域速查

| 领域 | 覆盖 |
|------|------|
| infrastructure | Spring Boot / Cloud / AI BOM、日志 |
| data | MyBatis-Plus、Easy-Query、动态数据源 |
| ddd | jMolecules、Spring Modulith |
| mapping | MapStruct、FastJSON、EasyExcel |
| security | Sa-Token、JustAuth |
| distributed | Redisson、Lock4j、SnailJob、DynamicTP |
| workflow | Warm-Flow |
| observability | SkyWalking |
| documentation | SpringDoc OpenAPI、Therapi Javadoc |
| testing | Spock、PMD、P3C |
| messaging | （待扩展） |
| utilities | Hutool、SMS4J、X-File-Storage |
| meta | IHub 体系使用指南 |

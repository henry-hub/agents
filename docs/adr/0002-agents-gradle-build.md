# ADR-0002: agents 使用 Gradle + IHub Plugins 构建

- 日期：2026-05-03
- 状态：已采纳

## 背景

`agents` 是 IHub 的 L3 对外接口层，包含 MCP Server、CLI、Skills 和 Runtime 四个组件。需要选择构建系统和项目组织方式。

三个选项：
1. 每个组件独立仓库 + 独立构建
2. 单仓库多模块 Gradle 项目，使用 IHub plugins
3. 非 JVM 构建系统（如 Go modules / Rust Cargo）

## 决策

我们决定使用 **单仓库多模块 Gradle 项目，通过 IHub plugins 构建**。

具体：
- MCP Server：Spring Boot + Spring AI MCP（Java）
- CLI：Java + Picocli（P1 骨架阶段用 Java，P2 可选 GraalVM Native Image 编译为独立二进制）
- Skills：语言无关的脚本集合，不参与 Gradle 编译
- Runtime：P2 阶段决定技术栈

## 后果

**正面影响**
- Dogfooding：直接验证 IHub plugins 在真实项目中的表现
- 统一依赖管理：通过 `gradle/libs.versions.toml` 统一版本
- AI 友好：`ihub-meta` 插件自动生成 `project-meta.json`，验证 P2 原则
- 开发效率：单仓库内跨组件修改无需跨 PR

**负面影响**
- CLI 最终可能需要编译为独立二进制（GraalVM Native Image），Java 起步增加了编译复杂度
- Skills 的非 JVM 脚本无法享受 Gradle 依赖管理
- 单仓库随组件增多可能变得庞大

**待观察**
- CLI 是否需要切换为 Go/Rust 以获得更小的二进制体积和更快的启动速度
- Runtime 的沙箱方案（Docker / Wasm / OS 级隔离）可能引入独立构建流程

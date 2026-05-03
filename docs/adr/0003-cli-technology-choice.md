# ADR-0003: CLI 技术选型 — Java + Picocli（P1 骨架阶段）

- 日期：2026-05-03
- 状态：已采纳

## 背景

`ihub` CLI 是开发者在终端使用 IHub 的直接入口，需支持 `ihub init`、`ihub meta`、`ihub check` 等命令。P1 阶段需搭建骨架，P2 阶段实现具体逻辑。

CLI 工具的技术选型考虑因素：
- 启动速度（用户对 CLI 的响应时间敏感）
- 二进制体积和分发便利性
- 与 IHub 生态的集成深度
- 开发维护成本

## 决策

**P1 阶段：Java 17 + Picocli**

- 使用 Picocli 作为 CLI 框架（JVM 生态最成熟，支持 GraalVM Native Image）
- 与 agents 其他组件共享 Gradle 构建和 IHub plugins
- 通过 `ihub-meta` 插件输出自身元数据

**P2 阶段评估：GraalVM Native Image 编译**

- 目标：将 CLI 编译为独立二进制，启动时间 < 50ms
- 通过 IHub `ihub-native` 插件实现
- 若 GraalVM Native Image 不满足需求，再评估 Go 重写

## 候选方案对比

| 方案 | 启动速度 | 二进制大小 | 生态集成 | 维护成本 |
|------|---------|-----------|----------|----------|
| Java + Picocli（当前） | ~1s | ~20MB（Fat JAR） | ★★★★★ | 低 |
| Java + GraalVM Native | <50ms | ~15MB | ★★★★★ | 中 |
| Go + Cobra | <10ms | ~5MB | ★★ | 高（双语种） |
| Rust + Clap | <5ms | ~3MB | ★ | 很高 |

## 后果

**正面影响**
- P1 阶段零额外成本，直接复用 Gradle 构建体系
- Picocli 的注解驱动开发体验与 Spring Boot 一致
- GraalVM Native Image 路径清晰，IHub 已有 `ihub-native` 插件支持

**负面影响**
- JVM 启动慢（约 1s），用户感知明显
- Fat JAR 体积大（约 20MB），分发不便
- P2 如需切换到 Go，则 P1 的 Java 代码成为沉没成本

**待观察**
- GraalVM Native Image 对 Picocli + Spring 的兼容性
- CLI 命令数量增长后 Native Image 编译时间是否可接受

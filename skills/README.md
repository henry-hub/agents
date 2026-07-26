# Skills

Skills 是 IHub 插件市场的基础形态——每个 Skill 是一个自包含的能力单元，可被 CLI、MCP Server 或 Runtime 加载和执行。

## Skill 是什么

一个 Skill 包含：
- **接口定义**：声明输入/输出类型和工具描述
- **实现脚本**：可执行的业务逻辑（语言无关，可混合使用）
- **元数据**：版本、依赖、作者、AI 提示词等结构化信息

## 与插件的关系

Skills 是"轻量级插件"——插件侧重构建约定和 Gradle 集成，Skills 侧重可编排的独立能力单元。两者互补：
- `plugins`：构建时代码约定（Gradle convention plugins）
- `skills`：运行时能力组合（AI 可编排）

## 目录约定

```
skills/
├── README.md           # 本文件
├── examples/           # 示例 Skill（演示结构约定）
│   └── hello-world/    # 最简 Skill 示例
├── ihub-catalog/       # IHub 能力目录查询技能
│   └── skill.md
├── ihub-init/          # IHub 项目初始化技能
│   └── skill.md
├── ihub-migrate/       # IHub 旧系统迁移分析技能
│   └── skill.md
└── manifest.schema.yaml
```

## 可用 Skills

| Skill | 触发场景 | 格式 |
|-------|---------|------|
| `ihub-catalog` | 技术选型、组件查询、替代方案比较 | Claude Code Skill |
| `ihub-init` | 创建新项目、配置 Gradle 构建 | Claude Code Skill |
| `ihub-migrate` | 升级 Spring Boot / Java 版本、技术债务分析 | Claude Code Skill |
| `hello-world` | 示例 / 演示 manifest 结构 | IHub manifest |

### 安装方式

Skills 通过 `pub.ihub.plugin.ihub-skills` Gradle 插件自动安装到项目的 `.claude/commands/` 目录，也可手动复制 `skill.md` 到目标项目。

## 状态

✅ P2 阶段：首批 3 个 Claude Code Skills 已发布，对齐主流 AI 编码代理格式。

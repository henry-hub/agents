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
├── install.sh          # 安装脚本（复制 Skills 到 .claude/skills/）
├── examples/           # 示例 Skill（演示结构约定）
│   └── hello-world/    # 最简 Skill 示例
├── ihub-catalog/       # IHub 能力目录查询技能
│   └── SKILL.md
├── ihub-init/          # IHub 项目初始化技能
│   └── SKILL.md
├── ihub-migrate/       # IHub 旧系统迁移分析技能
│   └── SKILL.md
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

每个 Skill 是符合 [Claude Code Skills](https://docs.claude.com/en/docs/claude-code/skills) 格式的
`SKILL.md`（含 YAML frontmatter：`name` + `description`）。

**方式 A：安装脚本（推荐）**

```bash
# 安装到当前项目
agents/skills/install.sh

# 安装到用户级（所有项目可用）
agents/skills/install.sh global

# 安装到指定项目
agents/skills/install.sh /path/to/project
```

**方式 B：手动复制**

```bash
cp -r skills/ihub-catalog /path/to/project/.claude/skills/
```

安装后 Claude Code 会根据 `description` 中的触发场景自动发现并使用，
也可用 `/ihub-catalog` 等命令显式调用。

> 注：`pub.ihub.plugin.ihub-skills` Gradle 插件安装的是构建诊断类斜杠命令
> （`.claude/commands/` 下的 `/ihub-diagnose`、`/ihub-configure`），与本目录的
> Claude Code Skills 是不同的分发渠道。

## 状态

✅ P2 阶段：首批 3 个 Claude Code Skills 已发布，对齐主流 AI 编码代理格式。

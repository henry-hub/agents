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
├── catalog.yaml        # Skill 目录索引（未来）
└── <skill-name>/       # 每个 Skill 一个目录
    ├── manifest.yaml   # Skill 元数据
    └── ...             # 实现文件
```

## 状态

🚧 P1 阶段：规范定义 + 示例 Skill，待 P2/P3 扩展。

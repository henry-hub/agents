# MCP Server

基于 Spring AI MCP (Model Context Protocol) 的标准化接口服务，为 AI 工具（Claude、Cursor、GitHub Copilot 等）提供 IHub 能力的统一访问入口。

## 定位

MCP Server 是 IHub 面向 AI 的"统一 API 层"——AI 工具通过 MCP 协议发现和调用 IHub 的能力（项目管理、代码分析、构建配置等），无需分别对接不同模块。

## 技术方向

- 基于 Spring AI MCP 实现
- 支持 stdio 和 HTTP 两种传输模式
- 暴露 IHub 四大能力板块的工具描述（Tools）、资源（Resources）、提示模板（Prompts）

## 状态

🚧 P1 阶段：接口定义 + 骨架搭建，待 P2 实现。

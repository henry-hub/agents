#!/usr/bin/env bash
# 安装 IHub Claude Code Skills 到 .claude/skills/
#
# 用法：
#   ./install.sh            # 安装到当前项目 .claude/skills/
#   ./install.sh global     # 安装到用户级 ~/.claude/skills/
#   ./install.sh /path/to/project   # 安装到指定项目
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SKILLS=(ihub-catalog ihub-init ihub-migrate)

TARGET="${1:-project}"
case "$TARGET" in
  global)  DEST="$HOME/.claude/skills" ;;
  project) DEST="$(pwd)/.claude/skills" ;;
  *)       DEST="$TARGET/.claude/skills" ;;
esac

for skill in "${SKILLS[@]}"; do
  mkdir -p "$DEST/$skill"
  cp "$SCRIPT_DIR/$skill/SKILL.md" "$DEST/$skill/SKILL.md"
  echo "✅ $skill -> $DEST/$skill/SKILL.md"
done

echo ""
echo "已安装 ${#SKILLS[@]} 个 Skills 到 $DEST"
echo "在 Claude Code 中直接描述需求即可触发（如：\"帮我选一个 ORM 框架\"），"
echo "或使用 /<skill-name> 显式调用。"

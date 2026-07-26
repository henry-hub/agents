#!/usr/bin/env bash
# sync-catalog.sh — 从 libs 仓库同步 catalog.json 到 agents mcp-server resources
#
# 用法：
#   ./scripts/sync-catalog.sh [libs-catalog-path]
#   默认路径：/Users/henry/Documents/GitHub/libs/gradle/ihub-catalog/catalog.json
#
# 前置条件：libs 仓库中已运行 merge_catalog.py 生成 catalog.json
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIBS_CATALOG="${1:-/Users/henry/Documents/GitHub/libs/gradle/ihub-catalog/catalog.json}"
AGENTS_TARGET="${SCRIPT_DIR}/../mcp-server/src/main/resources/ihub-catalog/catalog.json"

if [ ! -f "$LIBS_CATALOG" ]; then
    echo "ERROR: catalog not found at $LIBS_CATALOG"
    echo "Run 'python3 scripts/merge_catalog.py' in libs/gradle/ihub-catalog/ first."
    exit 1
fi

mkdir -p "$(dirname "$AGENTS_TARGET")"
cp "$LIBS_CATALOG" "$AGENTS_TARGET"

COUNT=$(python3 -c "import json; print(len(json.load(open('$AGENTS_TARGET'))['components']))")
echo "✅ Synced catalog: $LIBS_CATALOG → $AGENTS_TARGET ($COUNT components)"

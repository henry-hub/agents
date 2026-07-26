# IHub 旧系统迁移分析

当用户需要升级或迁移旧 Java 项目时，提供结构化的分析流程和迁移建议。

## 触发场景

- 用户说"升级 Spring Boot 版本"、"迁移到 JDK 21"、"这个项目太老了"
- 用户需要分析遗留系统的技术债务
- 用户想了解升级路径和风险

## 执行步骤

### 1. 项目现状分析

检查以下文件，确定当前技术栈：

```bash
# Gradle 项目
cat gradle/libs.versions.toml    # 版本目录
cat build.gradle.kts             # 构建脚本
cat gradle/wrapper/gradle-wrapper.properties  # Gradle 版本

# Maven 项目
cat pom.xml                      # 依赖和版本
```

关键信息收集：
- **Java 版本**：sourceCompatibility / targetCompatibility
- **Spring Boot 版本**：2.x / 3.x / 4.x
- **Gradle 版本**：8.x / 9.x
- **关键依赖**：javax vs jakarta、Spring Security 版本

### 2. 识别迁移需求

按优先级排列常见迁移路径：

| 迁移路径 | 关键变更 | 复杂度 |
|---------|---------|--------|
| Java 8/11 → 21 | Records、Sealed Classes、Pattern Matching | 中 |
| Spring Boot 2.x → 3.x | javax → jakarta、Security 6.x、AOT | 高 |
| Spring Boot 3.x → 4.x | Spring Framework 7、Jackson 3 | 中 |
| Gradle 7 → 9 | 配置缓存、约定插件、版本目录 | 中 |
| JUnit 4 → 5 | 注解迁移、断言 API | 低 |

### 3. 使用 IHub 迁移工具（如可用）

**方式 A：通过 MCP 工具**

如果 IHub MCP Server 可用：
- `analyzeProject(projectPath)` — 自动化分析，返回结构化报告
- `listMigrationRules()` — 查看可用迁移规则

**方式 B：使用 OpenRewrite**

```bash
# Spring Boot 3.x 升级
./gradlew rewriteRun \
  -Drewrite.activeRecipe=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_4

# Jakarta EE 迁移
./gradlew rewriteRun \
  -Drewrite.activeRecipe=org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
```

### 4. 生成迁移报告

按以下结构输出分析结果：

```markdown
# 迁移分析报告

## 项目概况
- 项目名：{name}
- 当前版本：Java {X} / Spring Boot {Y} / Gradle {Z}
- 目标版本：Java {X'} / Spring Boot {Y'} / Gradle {Z'}

## 发现的问题（按严重度排序）

### 🔴 阻塞项
- [ ] {问题描述} — {修复方式}

### 🟡 需处理
- [ ] {问题描述} — {修复方式}

### 🟢 建议优化
- [ ] {问题描述} — {修复方式}

## 推荐迁移顺序
1. {步骤 1}
2. {步骤 2}
...

## 参考资源
- IHub 能力目录中的组件升级建议
- OpenRewrite Recipe 列表
```

### 5. 执行迁移

遵循"丝滑迁移"原则：
1. **先升级构建工具**（Gradle wrapper → 最新版本）
2. **再升级框架**（Spring Boot 逐版本升级，不跳版本）
3. **最后升级依赖**（使用 IHub libs catalog 确认推荐版本）
4. **每步验证**（`./gradlew build` + 测试通过后再继续）

## 注意事项

- **不要一次性升级所有东西**——逐版本、逐模块推进
- **javax → jakarta 是最大的坑**——涉及所有 Servlet / JPA / Validation 注解
- **Spring Security 变更最复杂**——2.x 的 `WebSecurityConfigurerAdapter` 在 3.x 中已删除
- **优先使用 OpenRewrite 自动化**——确定性 Recipe 比手动改更可靠
- **IHub 能力目录可查推荐版本**——使用 `/ihub-catalog` 技能

---
name: ihub-init
description: 引导创建新的 Java/Spring Boot 项目并接入 IHub Plugins 构建体系（ihub-settings/ihub-java/ihub-boot）。当用户要创建新项目、起一个 Spring Boot 项目、配置 Gradle 构建、接入 IHub 插件时使用。
---

# IHub 项目初始化

当用户要创建新的 Java/Spring Boot 项目时，引导其使用 IHub Plugins 构建体系。

## 触发场景

- 用户说"创建一个新项目"、"起一个 Spring Boot 项目"
- 用户问"如何配置 Gradle 构建"
- 用户想接入 IHub 插件体系

## 执行步骤

### 1. 确认项目类型

询问用户：
- **项目类型**：Spring Boot Web 服务 / Gradle 插件 / 纯 Java 库
- **Java 版本**：推荐 JDK 21+（LTS）
- **构建脚本**：推荐 Kotlin DSL（build.gradle.kts）

### 2. 配置 settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("pub.ihub.plugin.ihub-settings") version "最新版本"
}

rootProject.name = "项目名称"
```

### 3. 配置 build.gradle.kts

**Spring Boot Web 服务：**
```kotlin
plugins {
    alias(ihub.plugins.java)
    alias(ihub.plugins.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
```

**纯 Java 库：**
```kotlin
plugins {
    alias(ihub.plugins.java)
    alias(ihub.plugins.publish)
}
```

### 4. 推荐 AI 工具集成

```kotlin
plugins {
    alias(ihub.plugins.meta)    // AI 元数据生成（./gradlew iHubMeta）
    alias(ihub.plugins.skills)  // AI 技能文件自动安装
}
```

### 5. 推荐依赖（按需）

根据项目需求，从 IHub 能力目录推荐组件：
- Web 服务 → `spring-boot-starter-web` + `springdoc-openapi`
- 数据层 → `mybatis-plus-bom` 或 `easy-query`
- 认证 → `sa-token`
- 工具 → `hutool`

使用 `/ihub-catalog` 技能查询具体组件的集成方式。

### 6. 验证

```bash
./gradlew build        # 构建验证
./gradlew iHubMeta     # 生成 AI 元数据
```

## IHub Plugins 速查

| 插件 ID | 功能 |
|---------|------|
| `pub.ihub.plugin.ihub-settings` | Settings 插件，自动聚合子项目 |
| `pub.ihub.plugin.ihub-java` | Java 编译配置（编码、版本、依赖管理） |
| `pub.ihub.plugin.ihub-boot` | Spring Boot 集成 |
| `pub.ihub.plugin.ihub-bom` | BOM 依赖管理 |
| `pub.ihub.plugin.ihub-publish` | Maven Central 发布 |
| `pub.ihub.plugin.ihub-verification` | 代码质量（PMD、Checkstyle） |
| `pub.ihub.plugin.ihub-meta` | AI 元数据 JSON 生成 |
| `pub.ihub.plugin.ihub-skills` | AI 技能文件自动安装 |

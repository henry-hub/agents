pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("pub.ihub.plugin.ihub-settings") version "2.0.0-m2"
}

rootProject.name = "ihub-agents"

include("mcp-server")
include("cli")

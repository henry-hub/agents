pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("pub.ihub.plugin.ihub-settings") version "agent-SNAPSHOT"
}

rootProject.name = "ihub-agents"

include("mcp-server")
include("cli")

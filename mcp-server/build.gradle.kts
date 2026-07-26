plugins {
    alias(ihub.plugins.java)
    alias(ihub.plugins.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.ai.starter.mcp.server)
}

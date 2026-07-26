plugins {
    alias(ihub.plugins.java)
    alias(ihub.plugins.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.ai.starter.mcp.server)
    implementation(libs.ihub.migrate.analyzer)
    testImplementation(libs.spring.boot.starter.test)
}

tasks.test {
    useJUnitPlatform()
}

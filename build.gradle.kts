plugins {
    alias(ihub.plugins.root)
}

// 应用版本：随发版 PR 递增，与 git tag（v0.1.0）对应
allprojects {
    version = "0.1.0"
}

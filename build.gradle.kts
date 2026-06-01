plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "dev.codex"
version = "0.1.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.1.2")
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Apple Music Controls"
        version = project.version.toString()

        ideaVersion {
            sinceBuild = "261"
        }
    }
}

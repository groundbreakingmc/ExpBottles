plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.github.groundbreakingmc"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    // https://github.com/groundbreakingmc/MyLib
    implementation("com.github.groundbreakingmc:MyLib:main-SNAPSHOT") {
        isChanging = true
    }
    // https://github.com/SpongePowered/Configurate
    compileOnly("org.spongepowered:configurate-yaml:4.0.0")
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    shadowJar {
        relocate("com.github.groundbreakingmc.mylib", "com.github.groundbreakingmc.expbottles.mylib")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

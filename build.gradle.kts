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
    compileOnly("com.github.groundbreakingmc:MyLib:main-SNAPSHOT")
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}


tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    shadowJar {
        relocate("com.github.groundbreakingmc.mylib", "com.github.groundbreakingmc.hldynamitesticks.mylib")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

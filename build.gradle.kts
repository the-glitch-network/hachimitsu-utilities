plugins {
    java
    `java-library`
}

val jupiter_version: String by project
val jetbrains_version: String by project

group = "net.kjp12.hachimitsu"
version = "0.0.0"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter-api", jupiter_version)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", jupiter_version)

    testCompileOnly(compileOnly("org.jetbrains", "annotations", jetbrains_version))
}

java {
    modularity.inferModulePath.set(true)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isDeprecation = true
        options.isWarnings = true
    }

    register<Jar>("sourcesJar") {
        dependsOn("classes")
        archiveClassifier.set("sources")
        from(sourceSets.main.get())
    }

    processResources {
        inputs.property("version", project.version)

        val srcDirs = sourceSets.main.get().resources.srcDirs
        from(srcDirs) {
            include("potato")
            expand("version" to project.version)
        }

        from(srcDirs) {
            exclude("potato")
        }
    }
}
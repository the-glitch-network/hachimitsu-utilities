plugins {
	java
	`java-library`
}

val jupiter_version: String by project
val jetbrains_version: String by project

group = "net.kjp12.hachimitsu"
version = "0.0.0"

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
	modularity.inferModulePath.set(true)
	withSourcesJar()
	withJavadocJar()
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.junit.jupiter", "junit-jupiter-api", jupiter_version)
	testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", jupiter_version)

	testCompileOnly(compileOnly("org.jetbrains", "annotations", jetbrains_version))
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.isDeprecation = true
		options.isWarnings = true
	}
}

plugins {
	java
	`java-library`
	`maven-publish`
}

val git: String by project

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
	testImplementation(libs.junit.jupiter.api)
	testRuntimeOnly(libs.junit.jupiter.engine)

	compileOnly(libs.jetbrains.annotations)
	testCompileOnly(libs.jetbrains.annotations)
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.isDeprecation = true
		options.isWarnings = true
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])

			pom {
				name.set("Hachimitsu Utilities")
				description.set("Various utilities for Java.")

				licenses {
					license {
						name.set("The Apache License, Version 2.0")
						url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
					}
				}

				issueManagement {
					system.set("GitHub Issues")
					url.set("https://$git/issues")
				}

				scm {
					connection.set("scm:git:git://$git.git")
					developerConnection.set("scm:git:ssh://$git.git")
					url.set("https://$git")
				}
			}
		}
	}
	repositories {
		System.getenv("MAVEN_URL")?.let {
			maven(it) {
				credentials {
					username = System.getenv("MAVEN_USERNAME")
					password = System.getenv("MAVEN_PASSWORD")
				}
			}
		}
	}
}

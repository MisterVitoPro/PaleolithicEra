import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")
	id("fabric-loom")
	id("maven-publish")
	java
}

group = property("maven_group")!!
version = property("mod_version")!!

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register("paleolithic-era") {
			sourceSet("main")
			sourceSet("client")
		}
	}
}

kotlin {
	sourceSets["client"].kotlin.srcDir("src/client/kotlin")
	sourceSets["main"].kotlin.srcDir("src/main/kotlin")
}

tasks {

	processResources {
		inputs.property("version", project.version)
		filesMatching("fabric.mod.json") {
			expand(mapOf("version" to project.version))
		}
	}

	jar {
		from("LICENSE")
	}

	publishing {
		publications {
			create<MavenPublication>("mavenJava") {
				artifact(remapJar) {
					builtBy(remapJar)
				}
				artifact(kotlinSourcesJar) {
					builtBy(remapSourcesJar)
				}
			}
		}
	}

	compileKotlin {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_21
		}
	}

}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

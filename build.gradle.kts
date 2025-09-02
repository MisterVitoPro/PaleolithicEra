import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")
	id("fabric-loom")
	id("maven-publish")
	java
}

// Expose versions from gradle.properties as typed vars
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project
val fabric_kotlin_version: String by project

group = property("maven_group")!!
version = property("mod_version")!!

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// To change the versions, see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")
	
	// Testing dependencies
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
	testImplementation("org.mockito:mockito-core:5.5.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
	testImplementation("net.fabricmc:fabric-loader-junit:${loader_version}")
	testImplementation(kotlin("test"))
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
		// Ensure changes to these props re-run resource processing
		inputs.property("version", project.version)
		inputs.property("minecraft_version", minecraft_version)
		inputs.property("loader_version", loader_version)
		inputs.property("fabric_version", fabric_version)
		inputs.property("fabric_kotlin_version", fabric_kotlin_version)

		filesMatching("fabric.mod.json") {
			expand(
				mapOf(
					"version" to project.version,
					"minecraft_version" to minecraft_version,
					"loader_version" to loader_version,
					"fabric_version" to fabric_version,
					"fabric_kotlin_version" to fabric_kotlin_version,
				)
			)
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
	
	test {
		useJUnitPlatform()
	}

	// Name the remapped jars with MC + mod version
	named<RemapJarTask>("remapJar") {
		archiveFileName.set("paleolithic-era-${minecraft_version}-${project.version}.jar")
	}
	named<RemapSourcesJarTask>("remapSourcesJar") {
		archiveFileName.set("paleolithic-era-${minecraft_version}-${project.version}-sources.jar")
	}

}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

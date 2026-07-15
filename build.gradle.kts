import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.kotlin.dsl.named
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Duration

plugins {
	kotlin("jvm")
	id("net.fabricmc.fabric-loom-remap")
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
		outputDirectory = rootProject.file("src/main/generated")
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
		dependsOn(project.tasks.matching { it.name == "stonecutterGenerate" })

		// Fabric datagen's cache is build bookkeeping, not a runtime resource.
		exclude(".cache/**")

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
		from(rootProject.file("LICENSE"))
		// Exclude test files from the jar to prevent bloating
		exclude("**/test/**")
		exclude("**/*Test.class")
		exclude("**/*Test$*.class")
		exclude("**/TestUtils.class")
		exclude("**/TestUtils$*.class")
		exclude("**/TestTags.class")
		exclude("**/TestTags$*.class")
	}

	publishing {
		publications {
			create<MavenPublication>("mavenJava") {
				artifactId = "${property("archives_base_name")}-${minecraft_version}"
				artifact(remapJar) {
					builtBy(remapJar)
				}
				artifact(remapSourcesJar) {
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
		
		// Configure test output
		testLogging {
			events("passed", "skipped", "failed", "standard_out", "standard_error")
			exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
			showCauses = true
			showExceptions = true
			showStackTraces = true
		}
		
		// Increase memory for tests
		maxHeapSize = "1G"
		
		// Set timeouts
		timeout.set(Duration.ofMinutes(10))
	}
	
	// Task to run only P0 (critical) tests
	register<Test>("testP0") {
		description = "Runs P0 (critical) tests only"
		group = "verification"
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("p0")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
	}
	
	// Task to run only P1 (important) tests  
	register<Test>("testP1") {
		description = "Runs P1 (important) tests only"
		group = "verification"
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("p1")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
	}
	
	// Task to run only P2 (nice-to-have) tests
	register<Test>("testP2") {
		description = "Runs P2 (nice-to-have) tests only"  
		group = "verification"
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("p2")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
	}
	
	// Task to run integration tests
	register<Test>("testIntegration") {
		description = "Runs integration tests only"
		group = "verification" 
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("integration")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
	}
	
	// Task to run unit tests
	register<Test>("testUnit") {
		description = "Runs unit tests only"
		group = "verification"
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("unit")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
	}
	
	// Task to run performance tests
	register<Test>("testPerformance") {
		description = "Runs performance tests only"
		group = "verification"
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("performance")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
		maxHeapSize = "2G" // More memory for performance tests
	}
	
	// Task to run quick tests (P0 + unit tests)
	register<Test>("testQuick") {
		description = "Runs quick tests (P0 + unit tests)"
		group = "verification"
		testClassesDirs = sourceSets["test"].output.classesDirs
		classpath = sourceSets["test"].runtimeClasspath
		useJUnitPlatform {
			includeTags("p0", "unit")
		}
		testLogging {
			events("passed", "skipped", "failed")
		}
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
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
	systemProperty("paleolithic.minecraftVersion", minecraft_version)
	systemProperty("paleolithic.loaderVersion", loader_version)
}

// Generated assets are shared by every node and are authored from the VCS version only.
tasks.matching { it.name == "runDatagen" }.configureEach {
	enabled = minecraft_version == "1.21.7"
}

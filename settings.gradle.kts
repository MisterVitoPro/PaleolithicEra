pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"
	create(rootProject) {
		versions("1.21.4", "1.21.5", "1.21.6", "1.21.7")
		vcsVersion = "1.21.7"
	}
}

rootProject.name = "paleolithic-era"

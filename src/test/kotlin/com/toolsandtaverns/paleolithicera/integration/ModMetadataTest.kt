package com.toolsandtaverns.paleolithicera.integration

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.InputStreamReader

@Tag("p1")
@Tag("integration")
class ModMetadataTest {

    private val classLoader = javaClass.classLoader
    private val metadata: JsonObject by lazy {
        val matchingMetadata = classLoader.getResources("fabric.mod.json").asSequence()
            .map { resource ->
                resource.openStream().use { stream ->
                    JsonParser.parseReader(InputStreamReader(stream)).asJsonObject
                }
            }
            .firstOrNull { it["id"]?.asString == MOD_ID }

        requireNotNull(matchingMetadata) {
            "$MOD_ID fabric.mod.json is missing from the test runtime classpath"
        }
    }

    @Test
    fun `metadata keeps common and client entrypoints separated`() {
        assertEquals(MOD_ID, metadata["id"].asString)
        assertEquals("*", metadata["environment"].asString)

        val entrypoints = metadata.getAsJsonObject("entrypoints")
        assertEquals(
            "com.toolsandtaverns.paleolithicera.PaleolithicEra",
            entrypoints.getAsJsonArray("main")[0].asJsonObject["value"].asString
        )
        assertEquals(
            "com.toolsandtaverns.paleolithicera.PaleolithicEraClient",
            entrypoints.getAsJsonArray("client")[0].asJsonObject["value"].asString
        )

        assertNotNull(classLoader.getResource("com/toolsandtaverns/paleolithicera/PaleolithicEra.class"))
        val clientClass = requireNotNull(
            classLoader.getResource("com/toolsandtaverns/paleolithicera/PaleolithicEraClient.class")
        )
        assertTrue(
            clientClass.path.replace('\\', '/').contains("/classes/kotlin/client/"),
            "client entrypoint must compile into the client source-set output"
        )
    }

    @Test
    fun `metadata requires the supported runtime versions`() {
        val dependencies = metadata.getAsJsonObject("depends")

        assertEquals(">=21", dependencies["java"].asString)
        assertEquals("~1.21.7", dependencies["minecraft"].asString)
        assertEquals(">=0.16.14", dependencies["fabricloader"].asString)
        assertFalse(metadata["version"].asString.contains('$'), "resource placeholders must be expanded")
    }

    @Test
    fun `client mixins are explicitly client scoped`() {
        val mixins = metadata.getAsJsonArray("mixins")
        assertTrue(mixins.any { it.isJsonPrimitive && it.asString == "paleolithic-era.mixins.json" })
        assertTrue(
            mixins.any {
                it.isJsonObject &&
                    it.asJsonObject["config"].asString == "paleolithic-era.client.mixins.json" &&
                    it.asJsonObject["environment"].asString == "client"
            }
        )
    }
}

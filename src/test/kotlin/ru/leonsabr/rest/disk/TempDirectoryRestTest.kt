package ru.leonsabr.rest.disk

import org.junit.AfterClass
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.BeforeClass

abstract class TempDirectoryRestTest : RestBaseTest() {
    companion object {
        var tempDir: String? = null

        // Because of probable simultaneous execution of many builds it makes sense to create temp disk directory each time
        @BeforeClass
        @JvmStatic
        fun createTempDirectory() {
            val dirName = "temp_${System.currentTimeMillis()}_dir"
            utilRequest()
                    .queryParams(PATH, dirName)
                    .put(resourcesEndpoint)
                    .apply {
                        println(statusCode)
                        if (statusCode == 201) tempDir = dirName
                    }
        }

        @AfterClass
        @JvmStatic
        fun deleteTempDirectory() {
            tempDir?.let {
                utilRequest()
                        .queryParams(PATH, it, PERMANENTLY, true)
                        .delete(resourcesEndpoint)
            }
        }
    }

    @Before
    fun assumeTempDirectoryIsCreated() {
        assumeTrue("Failed to create temporary directory", tempDir != null)
    }
}

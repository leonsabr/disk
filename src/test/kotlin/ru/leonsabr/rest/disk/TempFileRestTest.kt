package ru.leonsabr.rest.disk

import org.junit.Assume.assumeTrue
import org.junit.Before

abstract class TempFileRestTest : TempDirectoryRestTest() {

    protected val tempFileName = "${System.currentTimeMillis()}.png"
    protected val tempFilePath = "$tempDir/$tempFileName"

    @Before
    fun createFileToDelete() {
        val operationHref = utilRequest()
                .queryParams(PATH, tempFilePath, URL, googleLogoUrl)
                .post(resourcesUploadEndpoint)
                .then().extract().path<String>("href")

        assumeTrue("Failed to upload temporary file", waitForOperationStatus(operationHref, "success"))
    }
}

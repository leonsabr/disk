package ru.leonsabr.rest.disk.resources

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.startsWith
import org.junit.Test
import ru.leonsabr.rest.disk.Config
import ru.leonsabr.rest.disk.TempFileRestTest
import ru.yandex.qatools.allure.annotations.Step
import ru.yandex.qatools.allure.annotations.Title

@Title("DELETE /resources")
class DeleteResourceTest : TempFileRestTest() {

    @Test
    fun `delete file synchronously`() {
        diskRequest()
                .queryParams(PATH, tempFilePath)
                .delete(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(204)

        verifyFileIsNotAccessibleAnymore()
        verifyFileIsAccessibleInTrash()
    }

    @Test
    fun `delete file asynchronously`() {
        val operationHref = diskRequest()
                .queryParams(PATH, tempFilePath, FORCE_ASYNC, true)
                .delete(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(202)
                .body(hrefField, startsWith("${Config.diskV1Api}/operations/"))
                .body(methodField, equalTo("GET"))
                .body(templatedField, equalTo(false))
                .extract()
                .path<String>(hrefField)

        verifyOperationState(operationHref, "success")
        verifyFileIsNotAccessibleAnymore()
        verifyFileIsAccessibleInTrash()
    }

    @Test
    fun `delete file permanently`() {
        diskRequest()
                .queryParams(PATH, tempFilePath, PERMANENTLY, true)
                .delete(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(204)

        verifyFileIsNotAccessibleAnymore()
        verifyFileIsNotAccessibleInTrash()
    }

    // NEGATIVE CASES

    @Test
    fun `negative - unauthorized request gets 401 response`() {
        unauthorizedDiskRequest()
                .queryParams(PATH, tempFilePath)
                .delete(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(401)
                .verifyError401()
    }

    @Test
    fun `negative - request with missing "path" parameter gets 400 response`() {
        diskRequest()
                .delete(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(400)
                .verifyError("FieldValidationError", "Error validating field \"$PATH\": This field is required.",
                        "Ошибка проверки поля \"$PATH\": Это поле является обязательным.")
    }

    @Test
    fun `negative - request with incorrect "path" parameter gets 404 response`() {
        diskRequest()
                .queryParams(PATH, "ololo/$tempFilePath")
                .delete(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(404)
                .verifyError404()
    }

    @Step
    private fun verifyFileIsNotAccessibleAnymore() {
        utilRequest()
                .queryParams(PATH, tempFilePath)
                .get(resourcesEndpoint)
                .then()
                .statusCode(404)
    }

    private fun getFileInTrash() = utilRequest()
            .queryParams(PATH, tempFileName)
            .get(trashResourcesEndpoint)
            .then()

    @Step
    private fun verifyFileIsAccessibleInTrash() = getFileInTrash().statusCode(200)

    @Step
    private fun verifyFileIsNotAccessibleInTrash() = getFileInTrash().statusCode(404)
}

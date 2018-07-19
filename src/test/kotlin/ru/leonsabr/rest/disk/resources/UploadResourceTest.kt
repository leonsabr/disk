package ru.leonsabr.rest.disk.resources

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.startsWith
import org.junit.Test
import ru.leonsabr.rest.disk.Config
import ru.leonsabr.rest.disk.TempDirectoryRestTest
import ru.yandex.qatools.allure.annotations.Description
import ru.yandex.qatools.allure.annotations.Title

@Title("POST /resources/upload")
class UploadResourceTest : TempDirectoryRestTest() {

    private val tempName = "$tempDir/${System.currentTimeMillis()}.png"

    @Test
    fun `upload image`() {
        diskRequest()
                .queryParams(PATH, tempName, URL, googleLogoUrl)
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(202)
                .body(hrefField, startsWith("${Config.diskV1Api}/operations/"))
                .body(methodField, equalTo("GET"))
                .body(templatedField, equalTo(false))
    }

    @Test
    @Description("Only \"href\" and \"method\" fields should be in response body")
    fun `parameter "fields" sorts out fields in response`() {
        diskRequest()
                .queryParams(PATH, tempName, URL, googleLogoUrl, FIELDS, "$hrefField,$methodField")
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(202)
                .body(hrefField, notNullValue())   // listed in fields
                .body(methodField, notNullValue()) // listed in fields
                .body(templatedField, nullValue()) // not listed in fields
    }

    @Test
    @Description("\"disable_redirects=true\" parameter prohibits upload if \"url\" gets redirected answer")
    fun `parameter "disable_redirects" prohibits redirects in url parameter`() {
        val operationHref = diskRequest()
                .queryParams(PATH, tempName, URL, "http://yadi.sk/d/fLTPi1F83ZKTxG", DISABLE_REDIRECTS, true)
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .body(hrefField, notNullValue())
                .extract()
                .path<String>(hrefField)

        verifyOperationState(operationHref, "failed")
    }

    // NEGATIVE CASES

    @Test
    fun `negative - unauthorized request gets 401 response`() {
        unauthorizedDiskRequest()
                .queryParams(PATH, tempName, URL, googleLogoUrl)
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(401)
                .verifyError401()
    }

    @Test
    fun `negative - request with missing "path" parameter gets 400 response`() {
        diskRequest()
                .queryParams(URL, googleLogoUrl)
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(400)
                .verifyError("FieldValidationError", "Error validating field \"$PATH\": This field is required.",
                        "Ошибка проверки поля \"$PATH\": Это поле является обязательным.")
    }

    @Test
    fun `negative - request with missing "url" parameter gets 400 response`() {
        diskRequest()
                .queryParams(PATH, tempName)
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(400)
                .verifyError("FieldValidationError", "Error validating field \"$URL\": This field is required.",
                        "Ошибка проверки поля \"$URL\": Это поле является обязательным.")
    }

    @Test
    fun `negative - request with non-existing path parameter gets 409 response`() {
        val nonExistingPath = "ololo/${System.currentTimeMillis()}/qqq.png"
        diskRequest()
                .queryParams(PATH, nonExistingPath, URL, googleLogoUrl)
                .post(resourcesUploadEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(409)
                .verifyError("DiskPathDoesntExistsError", "Specified path \"$nonExistingPath\" doesn't exists.",
                        "Указанного пути \"$nonExistingPath\" не существует.")
    }
}

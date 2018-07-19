package ru.leonsabr.rest.disk.resources

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import ru.leonsabr.rest.disk.Config
import ru.leonsabr.rest.disk.TempFileRestTest
import ru.yandex.qatools.allure.annotations.Description
import ru.yandex.qatools.allure.annotations.Title
import java.net.URLEncoder

@Title("PUT /resources/publish")
class PublishResourceTest : TempFileRestTest() {

    @Test
    fun `publish file`() {
        diskRequest()
                .queryParams(PATH, tempFilePath)
                .put(resourcesPublishEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body(hrefField, equalTo("${Config.diskV1Api}/resources?path=${encode("disk:/$tempFilePath")}"))
                .body(methodField, equalTo("GET"))
                .body(templatedField, equalTo(false))
    }

    @Test
    @Description("Only \"href\" field should be in response body")
    fun `parameter "fields" sorts out fields in response`() {
        diskRequest()
                .queryParams(PATH, tempFilePath, FIELDS, hrefField)
                .put(resourcesPublishEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body(hrefField, notNullValue())   // listed in fields
                .body(methodField, nullValue())    // not listed in fields
                .body(templatedField, nullValue()) // not listed in fields
    }

    // NEGATIVE CASES

    @Test
    fun `negative - request with missing "path" parameter gets 400 response`() {
        diskRequest()
                .put(resourcesPublishEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(400)
                .verifyError("FieldValidationError", "Error validating field \"$PATH\": This field is required.",
                        "Ошибка проверки поля \"$PATH\": Это поле является обязательным.")
    }

    @Test
    fun `negative - unauthorized request gets 401 response`() {
        unauthorizedDiskRequest()
                .queryParams(PATH, tempFilePath)
                .put(resourcesPublishEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(401)
                .verifyError401()
    }

    @Test
    fun `negative - request with incorrect "path" parameter gets 404 response`() {
        diskRequest()
                .queryParams(PATH, "ololo/$tempFilePath")
                .put(resourcesPublishEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(404)
                .verifyError404()
    }

    private fun encode(url: String) = URLEncoder.encode(url, "UTF-8")
}

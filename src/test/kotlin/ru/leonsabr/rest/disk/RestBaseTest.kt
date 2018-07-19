package ru.leonsabr.rest.disk

import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import ru.leonsabr.rest.disk.utils.AllureTestRunner
import ru.leonsabr.rest.disk.utils.attachText
import ru.leonsabr.rest.disk.utils.waitForCondition
import ru.yandex.qatools.allure.annotations.Step
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@RunWith(AllureTestRunner::class)
abstract class RestBaseTest {

    companion object {
        const val resourcesEndpoint = "/resources"
        const val trashResourcesEndpoint = "/trash/resources"
        const val resourcesUploadEndpoint = "$resourcesEndpoint/upload"
        const val resourcesPublishEndpoint = "$resourcesEndpoint/publish"

        const val PATH = "path"
        const val URL = "url"
        const val FIELDS = "fields"
        const val DISABLE_REDIRECTS = "disable_redirects"
        const val PERMANENTLY = "permanently"
        const val FORCE_ASYNC = "force_async"

        const val hrefField = "href"
        const val methodField = "method"
        const val templatedField = "templated"

        fun utilRequest() = RestAssured.given()
                .baseUri(Config.diskV1Api)
                .contentType("application/json")
                .header("Authorization", "OAuth ${Config.authToken}")

        const val googleLogoUrl = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png"
    }

    private val requestLogsStream = ByteArrayOutputStream()
    private var status: String? = null
    private var response: Response? = null

    fun unauthorizedDiskRequest(out: ByteArrayOutputStream = requestLogsStream) = RestAssured.given()
            .filter(RequestLoggingFilter(PrintStream(out)))
            .baseUri(Config.diskV1Api)
            .accept("application/json")
            .contentType("application/json")

    fun diskRequest() = unauthorizedDiskRequest()
            .header("Authorization", "OAuth ${Config.authToken}")

    @After
    fun attachLogs() {
        if (requestLogsStream.size() > 0) attachText("request", requestLogsStream)
        status?.let { attachText("response status and headers", it) }
        response?.let { attachText("response body", it.prettyPrint()) }
    }

    fun Response.saveResponseBody() = apply {
        status = listOf(
                this.statusLine,
                "",
                *this.headers.map { "${it.name}: ${it.value}" }.toTypedArray()
        ).joinToString(separator = "\n")
        response = this
    }

    fun ValidatableResponse.verifyError(error: String, description: String, message: String) = apply {
        body("error", equalTo(error))
        body("description", equalTo(description))
        body("message", equalTo(message))
    }

    fun ValidatableResponse.verifyError404() = verifyError("DiskNotFoundError", "Resource not found.", "Не удалось найти запрошенный ресурс.")

    fun ValidatableResponse.verifyError401() = verifyError("UnauthorizedError", "Unauthorized", "Не авторизован.")

    fun waitForOperationStatus(operationHref: String, status: String) = waitForCondition {
        utilRequest()
                .get(operationHref)
                .then()
                .statusCode(200)
                .extract().path<String>("status") == status
    }

    @Step("Verify async operation state is [{1}]")
    fun verifyOperationState(operationHref: String, status: String) {
        attachText("href", operationHref)
        assertTrue("Failed to wait for operation response {\"status\": \"$status\"}",
                waitForOperationStatus(operationHref, status))
    }
}

package ru.leonsabr.rest.disk

object Config {

    val authToken = System.getProperty("disk.auth.token", "AQAAAAAoZ-cDAADLW7NXR4yF1k_ylCB0ikzyPl8")!! // it should be a secret, ok? :)

    private val baseUrl = System.getProperty("disk.base.url", "https://cloud-api.yandex.net:443")

    val diskV1Api = "$baseUrl/v1/disk"
}

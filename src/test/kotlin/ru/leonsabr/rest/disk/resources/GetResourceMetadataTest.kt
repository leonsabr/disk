package ru.leonsabr.rest.disk.resources

import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.startsWith
import org.junit.Test
import ru.leonsabr.rest.disk.RestBaseTest
import ru.yandex.qatools.allure.annotations.Description
import ru.yandex.qatools.allure.annotations.Title

@Title("GET /resources")
class GetResourceMetadataTest : RestBaseTest() {

    @Test
    fun `request text file data`() {
        diskRequest()
                .queryParams(PATH, "empty.txt")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .verifyFileMetadata(
                        name = "empty.txt",
                        path = "disk:/empty.txt",
                        size = 0,
                        mimeType = "text/plain",
                        mediaType = "document",
                        md5 = "d41d8cd98f00b204e9800998ecf8427e",
                        sha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                        created = "2018-07-18T17:00:24+00:00",
                        modified = "2018-07-18T17:00:24+00:00",
                        revision = 1532007027224296,
                        resourceId = "677897987:402067bf3523773cda64513abdaf3af8bdfb442d3f434078455164774a9febcb",
                        antivirusStatus = "not-scanned",
                        commentIdsPrivateResource = "677897987:402067bf3523773cda64513abdaf3af8bdfb442d3f434078455164774a9febcb",
                        commentIdsPublicResource = "677897987:402067bf3523773cda64513abdaf3af8bdfb442d3f434078455164774a9febcb"
                )
    }

    @Test
    fun `request image file data`() {
        diskRequest()
                .queryParams(PATH, "spb.jpg")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .verifyFileMetadata(
                        name = "spb.jpg",
                        path = "disk:/spb.jpg",
                        size = 2108737,
                        mimeType = "image/jpeg",
                        mediaType = "image",
                        md5 = "03327b821c0cfbbba19e8c214cb77db1",
                        sha256 = "610e8f6c7fdf284ed82b7b2d8177ce05f3a053813227e5249eeddcbbe9bb3312",
                        created = "2018-07-18T19:50:05+00:00",
                        modified = "2018-07-18T19:50:05+00:00",
                        revision = 1531943410482674,
                        resourceId = "677897987:d21e8bdc596b345fe9e11d1bdc7b2567f24d18610f72a6b2c8c8d2a83842a2f2",
                        antivirusStatus = "clean",
                        commentIdsPrivateResource = "677897987:d21e8bdc596b345fe9e11d1bdc7b2567f24d18610f72a6b2c8c8d2a83842a2f2",
                        commentIdsPublicResource = "677897987:d21e8bdc596b345fe9e11d1bdc7b2567f24d18610f72a6b2c8c8d2a83842a2f2",
                        exifDateTime = "2009-01-05T14:46:58+00:00",
                        preview = "https://downloader.disk.yandex.ru/preview/99e8f91b54b2b325b2720e19dbd2fe4941447872440956db469877084cf496df/inf/LClbSbKVwspX2bZyLmrNUz394uBG5D4Uqjr5-edKstYKy1RHxVM4xXPHN6foQe8W-evhnKgVX0YlI1dWNw6Pwg%3D%3D?uid=677897987&filename=spb.jpg&disposition=inline&hash=&limit=0&content_type=image%2Fjpeg&tknv=v2&size=S&crop=0"
                )
    }

    @Test
    fun `request directory data - general`() {
        diskRequest()
                .queryParams(PATH, "my_folder")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body("name", equalTo("my_folder"))
                .body("path", equalTo("disk:/my_folder"))
                .body("size", nullValue())
                .body("type", equalTo("dir"))
                .body("created", equalTo("2018-07-18T19:21:40+00:00"))
                .body("modified", equalTo("2018-07-18T19:21:40+00:00"))
                .body("revision", equalTo(1531941700499000))
                .body("resource_id", equalTo("677897987:ed07eabdb3fd49e69f7924a232c645ce6cfb2e4b8d224af9abf64f34a008ddf5"))
                .body("comment_ids.private_resource", equalTo("677897987:ed07eabdb3fd49e69f7924a232c645ce6cfb2e4b8d224af9abf64f34a008ddf5"))
                .body("comment_ids.public_resource", equalTo("677897987:ed07eabdb3fd49e69f7924a232c645ce6cfb2e4b8d224af9abf64f34a008ddf5"))
                .body("file", nullValue())
                .body("antivirus_status", nullValue())
                .body("preview", nullValue())
                .body("_embedded", notNullValue())
    }

    @Test
    fun `request directory data - embedded content`() {
        diskRequest()
                .queryParams(PATH, "my_folder", "sort", "size")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body("_embedded.path", equalTo("disk:/my_folder"))
                .body("_embedded.total", equalTo(2))
                .body("_embedded.limit", equalTo(20))
                .body("_embedded.offset", equalTo(0))
                .body("_embedded.sort", equalTo("size"))
                .body("_embedded.items", hasSize<Int>(2))
                .verifyFileMetadata(
                        prefix = "_embedded.items[0].", // this [0] also checks work of sort parameter
                        name = "text.txt",
                        path = "disk:/my_folder/text.txt",
                        size = 17,
                        mimeType = "text/plain",
                        mediaType = "document",
                        md5 = "5b11b7913e998c32cc86518c2cd5954f",
                        sha256 = "d6a99a0f29b580855a118672e52e61a0a28445d715d0f4177d6e27f72274d349",
                        created = "2018-07-18T19:21:53+00:00",
                        modified = "2018-07-18T19:21:53+00:00",
                        revision = 1531941714806366,
                        resourceId = "677897987:2da0c4cfc12d6d333a3f7f0168e10d852b32181486d20cacebb97ad647d684ed",
                        antivirusStatus = "clean",
                        commentIdsPrivateResource = "677897987:2da0c4cfc12d6d333a3f7f0168e10d852b32181486d20cacebb97ad647d684ed",
                        commentIdsPublicResource = "677897987:2da0c4cfc12d6d333a3f7f0168e10d852b32181486d20cacebb97ad647d684ed",
                        preview = "https://downloader.disk.yandex.ru/preview/9762f56768841e59b85cd9acf00dbca5b89c3762b56fcc029343fed8b7099a0e/inf/LClbSbKVwspX2bZyLmrNU75E9UrT6nUKtXjfpiKv20Y_ZDGIG1c_W6KJmZ_nZQBNLr-QVRir1fkNzsh_mBShoA%3D%3D?uid=677897987&filename=text.txt&disposition=inline&hash=&limit=0&content_type=image%2Fjpeg&tknv=v2&size=S&crop=0"
                )
                .verifyFileMetadata(
                        prefix = "_embedded.items[1].", // this [1] also checks work of sort parameter
                        name = "joom.jpg",
                        path = "disk:/my_folder/joom.jpg",
                        size = 10740,
                        mimeType = "image/jpeg",
                        mediaType = "image",
                        md5 = "739eee94c511ecd1ac8c44d41c5dc136",
                        sha256 = "51b69c9f0e4a338f2455c324e01e83448734d77286cbe1a86b4d563f9a19c4a4",
                        created = "2018-07-18T19:43:32+00:00",
                        modified = "2018-07-18T20:06:53+00:00",
                        revision = 1531944413698596,
                        resourceId = "677897987:c4218ec33bc60bbd585f58c5ba0d49ec2cdc755e4c43cc86ab782013fc2fb51e",
                        antivirusStatus = "clean",
                        commentIdsPrivateResource = "677897987:c4218ec33bc60bbd585f58c5ba0d49ec2cdc755e4c43cc86ab782013fc2fb51e",
                        commentIdsPublicResource = "677897987:c4218ec33bc60bbd585f58c5ba0d49ec2cdc755e4c43cc86ab782013fc2fb51e",
                        preview = "https://downloader.disk.yandex.ru/preview/83219c578419bf241e5e84de83ff0ff01eec4ecb57045a27dd9f0f7d51c0612d/inf/fH_O_qJ-sJ0KVAfs0pnY8s2gHKpujHCDpb1LhD7zLqdSvgnVeRX6C9JzSeHDFbFrOb0AjxqwQHm8Rdfh6ZzWqg%3D%3D?uid=677897987&filename=joom.jpg&disposition=inline&hash=&limit=0&content_type=image%2Fjpeg&tknv=v2&size=S&crop=0"
                )
    }

    @Test
    @Description("Limit embedded content to 3 items with offset of first 4 items")
    fun `request directory data - limit and offset`() {
        diskRequest()
                .queryParams(PATH, "dir_with_10_files", "limit", "3", "offset", "4")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body("_embedded.total", equalTo(10))
                .body("_embedded.limit", equalTo(3))
                .body("_embedded.offset", equalTo(4))
                .body("_embedded.sort", equalTo(""))
                .body("_embedded.items", hasSize<Int>(3))
                .body("_embedded.items[0].name", equalTo("4.txt"))
                .body("_embedded.items[1].name", equalTo("5.txt"))
                .body("_embedded.items[2].name", equalTo("6.txt"))
    }

    @Test
    @Description("Only \"name\" and \"path\" fields should be in response body")
    fun `parameter "fields" sorts out fields in response`() {
        diskRequest()
                .queryParams(PATH, "my_folder", FIELDS, "name,path,_embedded.items.name,_embedded.items.path")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body("name", equalTo("my_folder"))
                .body("path", equalTo("disk:/my_folder"))
                .body("type", nullValue()) // not listed in fields
                .body("_embedded.items[0].name", equalTo("joom.jpg"))
                .body("_embedded.items[0].path", equalTo("disk:/my_folder/joom.jpg"))
                .body("_embedded.items[0].type", nullValue()) // not listed in fields
                .body("_embedded.items[1].name", equalTo("text.txt"))
                .body("_embedded.items[1].path", equalTo("disk:/my_folder/text.txt"))
                .body("_embedded.items[1].type", nullValue()) // not listed in fields
    }

    @Test
    fun `parameter "preview_size" controls preview size`() {
        val size = "123x654"
        diskRequest()
                .queryParams(PATH, "spb.jpg", "preview_size", size)
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body("preview", containsString("&size=$size"))
    }

    @Test
    fun `parameter "preview_crop" controls preview crop`() {
        diskRequest()
                .queryParams(PATH, "spb.jpg", "preview_crop", "1")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
                .body("preview", containsString("&crop=1"))
    }

    // NEGATIVE CASES

    @Test
    fun `negative - unknown path returns 404 response`() {
        diskRequest()
                .queryParams(PATH, "no_such_path")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(404)
                .verifyError404()
    }

    @Test
    fun `negative - unauthorized request gets 401 response`() {
        unauthorizedDiskRequest()
                .queryParams(PATH, "/")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(401)
                .verifyError401()
    }

    @Test
    fun `negative - parameter "path" is required, request with missing path gets 400 response`() {
        diskRequest()
                .queryParams(PATH, "")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(400)
                .verifyError("FieldValidationError", "Error validating field \"path\": This field is required.",
                        "Ошибка проверки поля \"path\": Это поле является обязательным.")
    }

    @Test
    fun `this test fails because of demo reasons`() {
        diskRequest()
                .queryParams(PATH, "12345")
                .get(resourcesEndpoint)
                .saveResponseBody()
                .then()
                .statusCode(200)
    }

    private fun ValidatableResponse.verifyFileMetadata(
            prefix: String = "",
            name: String,
            path: String,
            size: Int,
            mimeType: String,
            mediaType: String,
            md5: String,
            sha256: String,
            created: String,
            modified: String,
            revision: Long,
            resourceId: String,
            antivirusStatus: String,
            commentIdsPrivateResource: String,
            commentIdsPublicResource: String,
            preview: String? = null,
            exifDateTime: String? = null
    ) = apply {
        body("${prefix}name", equalTo(name))
        body("${prefix}path", equalTo(path))
        body("${prefix}size", equalTo(size))
        body("${prefix}type", equalTo("file"))
        body("${prefix}mime_type", equalTo(mimeType))
        body("${prefix}media_type", equalTo(mediaType))
        body("${prefix}md5", equalTo(md5))
        body("${prefix}sha256", equalTo(sha256))
        body("${prefix}created", equalTo(created))
        body("${prefix}modified", equalTo(modified))
        body("${prefix}revision", equalTo(revision))
        body("${prefix}resource_id", equalTo(resourceId))
        body("${prefix}comment_ids.private_resource", equalTo(commentIdsPrivateResource))
        body("${prefix}comment_ids.public_resource", equalTo(commentIdsPublicResource))
        body("${prefix}file", startsWith("https://downloader.disk.yandex.ru/disk/"))
        body("${prefix}antivirus_status", equalTo(antivirusStatus))
        if (preview == null) {
            body("${prefix}preview", nullValue())
        } else {
            body("${prefix}preview", equalTo(preview))
        }
        if (exifDateTime == null) {
            body("${prefix}exif.date_time", nullValue())
        } else {
            body("${prefix}exif.date_time", equalTo(exifDateTime))
        }
    }
}

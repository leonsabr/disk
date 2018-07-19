package ru.leonsabr.rest.disk.utils

import ru.yandex.qatools.allure.annotations.Attachment
import java.io.ByteArrayOutputStream

@Suppress("unused_parameter")
@Attachment(value = "{0}", type = "text/plain")
fun attachText(name: String, out: ByteArrayOutputStream) = out.toByteArray()!!

@Suppress("unused_parameter")
@Attachment(value = "{0}", type = "text/plain")
fun attachText(name: String, text: String) = text

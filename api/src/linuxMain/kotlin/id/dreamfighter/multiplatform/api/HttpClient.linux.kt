package id.dreamfighter.multiplatform.api

import io.ktor.client.HttpClient

actual val client: HttpClient
    get() = TODO("Not yet implemented")

actual inline fun <reified T : Any> getProperties(obj: T) {
}

actual inline fun <reified T : Any> getProperties(obj: T): Map<String, Any> {
    TODO("Not yet implemented")
}

actual inline fun <reified T : Any> getProperties(obj: T): Map<String, Any?> {
    TODO("Not yet implemented")
}
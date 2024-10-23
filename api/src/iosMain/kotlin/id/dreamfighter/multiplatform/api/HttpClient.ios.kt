package id.dreamfighter.multiplatform.api

import id.dreamfighter.multiplatform.api.model.Request
import io.ktor.client.HttpClient

actual val client: HttpClient
    get() = TODO("Not yet implemented")


actual inline fun <reified T : Any> getRequest(obj: T): Request {
    TODO("Not yet implemented")
}
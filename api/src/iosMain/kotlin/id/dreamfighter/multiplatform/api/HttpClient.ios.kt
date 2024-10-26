package id.dreamfighter.multiplatform.api

import id.dreamfighter.multiplatform.api.model.Request
import io.ktor.client.HttpClient

actual val client: HttpClient = HttpClient()


actual inline fun <reified T : Any> getRequest(obj: T): Request {
    println(obj.toJson())
   return Request("","", path = emptyMap(), query = emptyMap())
}
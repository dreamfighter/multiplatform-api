package id.dreamfighter.multiplatform.api

import id.dreamfighter.multiplatform.annotation.Body
import id.dreamfighter.multiplatform.annotation.Get
import id.dreamfighter.multiplatform.annotation.Path
import id.dreamfighter.multiplatform.annotation.Post
import id.dreamfighter.multiplatform.annotation.Query
import id.dreamfighter.multiplatform.api.model.Request
import id.dreamfighter.multiplatform.api.model.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import io.ktor.http.parameters

object HttpMethod {
    val GET = "GET"
    val POST = "POST"
}
var BASE_URL = "https://localhost"
fun HttpClient.setBaseUrl(url: String) {
    BASE_URL = url
}

expect val client: HttpClient

fun Any.toJson(): String {
    return """{"name":"${this::class.simpleName}"}"""
}

expect inline fun <reified T:Any> getRequest(obj: T):Request

suspend inline fun <reified T> req(request: Request, interceptor: HttpRequestBuilder.() -> Unit = {}): Resource<T>{
    println(request.toJson())

    if(request.path.isNotEmpty()){
        request.path.forEach {
            request.url = request.url.replace("{${it.key}}",it.value.toString().encodeURLPathPart())
        }
    }
    try {
        val params = parameters {
            request.query.forEach {
                append(it.key, it.value.toString())
            }
        }
        var contentType = request.headers["Content-Type"]
        val response = when(request.method) {
            HttpMethod.POST -> {
                when(contentType){
                    "application/json" -> client.post(request.url) {
                        interceptor(this)
                        contentType(ContentType.Application.Json)
                        setBody(request.body)
                    }
                    else -> {
                        val formParams = parameters {
                            if(request.body != null && request.body is Map<*, *>){
                                request.body.forEach {
                                    append("${it.key}", it.value.toString())
                                }
                            }
                        }

                        client.submitForm(
                            url = request.url,
                            formParameters = formParams
                        )
                    }

                }



            }
            else -> client.get(request.url){
                interceptor(this)
                url {
                    parameters.appendAll(params)
                }
            }
        }
        return Resource.Success(response.body())
    } catch (e: RedirectResponseException) {
        // handle 3xx codes
        return(Resource.Error(e.response.status.description))

    } catch (e: ClientRequestException) {
        //handle 4xx error codes
        return(Resource.Error(e.response.status.description))

    } catch (e: ServerResponseException) {
        //handle 5xx error codes
        return(Resource.Error(e.response.status.description))
    } catch (e: Exception) {
        return (Resource.Error(e.message ?: "Something went wrong"))
    }
}
/*
@Post("/auth/google")
data class AuthGoogle(@Body val idToken: String, @Path val id:Int, @Query val name:String)

@Get("/auth/google")
data class Profile(@Path val id:Int, @Query val name:String)

 */
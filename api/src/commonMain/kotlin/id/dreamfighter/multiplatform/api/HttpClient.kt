package id.dreamfighter.multiplatform.api

import id.dreamfighter.multiplatform.api.model.Get
import id.dreamfighter.multiplatform.api.model.Path
import id.dreamfighter.multiplatform.api.model.Query
import id.dreamfighter.multiplatform.api.model.Request
import id.dreamfighter.multiplatform.api.model.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.encodeURLPathPart
import io.ktor.http.parameters
import io.ktor.http.plus

object HttpMethod {
    val GET = "GET"
    val POST = "POST"
}
var BASE_URL = "https://localhost"
fun HttpClient.setBaseUrl(url: String) {
    BASE_URL = url
}

expect val client: HttpClient

expect inline fun <reified T : Any> getRequest(obj:T):Request

suspend inline fun <reified T : Any> req(request: Any): Resource<T>{
    val req = getRequest(request)

    if(req.path.isNotEmpty()){
        req.path.forEach {
            req.url = req.url.replace("{${it.key}}",it.value.toString().encodeURLPathPart())
        }
    }
    try {
        val params = parameters {
            req.query.forEach {
                append(it.key, it.value.toString())
            }
        }
        val response = when(req.method) {
            HttpMethod.POST -> client.submitForm(
                url = req.url,
                formParameters = params
            )
            else -> client.get(req.url){
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

@Get("http://localhost:3000/transaction/{id}")
data class Transaction(@Query val id:Int)
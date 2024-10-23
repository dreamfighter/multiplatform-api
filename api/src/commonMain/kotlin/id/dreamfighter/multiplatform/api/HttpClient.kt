package id.dreamfighter.multiplatform.api

import id.dreamfighter.multiplatform.api.model.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlin.reflect.KFunction

expect val client: HttpClient

interface ApiClient{

}

suspend inline fun <reified T> post(api:(KFunction<out T>)->Unit):Resource<T>{

    try {
        val response = client.submitForm (
            url = "https://oauth2.googleapis.com/token",
            formParameters = parameters {
                append("grant_type","authorization_code")
                append("redirect_uri","http://localhost:9002/auth")
            }
        )
        return Resource.Success(response.body())
    } catch (e: RedirectResponseException) {
        // handle 3xx codes
        return (Resource.Error(e.response.status.description))

    } catch (e: ClientRequestException) {
        //handle 4xx error codes
        return (Resource.Error(e.response.status.description))

    } catch (e: ServerResponseException) {
        //handle 5xx error codes
        return (Resource.Error(e.response.status.description))
    } catch (e: Exception) {
        return (Resource.Error(e.message ?: "Something went wrong"))
    }
}

suspend fun testApi(){

}
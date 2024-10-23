package id.dreamfighter.multiplatform.api

import id.dreamfighter.multiplatform.api.model.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1

expect val client: HttpClient

expect inline fun <reified T : Any> getProperties(obj:T):Map<String,Any?>

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Request(val value: String)


inline fun <reified T : Any> post(request: Any, crossinline result:(Resource<T>)->Unit){

    //println("OKE DONE")
    val property = request::class
    val maps = getProperties(request)
    print(maps["id"])

    flow<Resource<T>>  {
        try {
            val response = client.submitForm (
                url = "https://oauth2.googleapis.com/token",
                formParameters = parameters {
                    append("grant_type","authorization_code")
                    append("redirect_uri","http://localhost:9002/auth")
                }
            )
            result(Resource.Success(response.body()))
        } catch (e: RedirectResponseException) {
            // handle 3xx codes
            result (Resource.Error(e.response.status.description))

        } catch (e: ClientRequestException) {
            //handle 4xx error codes
            result (Resource.Error(e.response.status.description))

        } catch (e: ServerResponseException) {
            //handle 5xx error codes
            result (Resource.Error(e.response.status.description))
        } catch (e: Exception) {
            result (Resource.Error(e.message ?: "Something went wrong"))
        }
    }
}

@Request("/transaction/{id}")
data class Transaction(val id:Int)
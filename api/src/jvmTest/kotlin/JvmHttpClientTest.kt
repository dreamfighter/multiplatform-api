import id.dreamfighter.multiplatform.api.Transaction
import id.dreamfighter.multiplatform.api.client
import id.dreamfighter.multiplatform.api.req
import id.dreamfighter.multiplatform.api.setBaseUrl
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import kotlin.test.Test

class JvmHttpClientTest {
    private val scope = CoroutineScope(Dispatchers.Default)
    @Test
    fun `test 3rd element`(): Unit = runBlocking {
        client.setBaseUrl("http://127.0.0.1:3000")
        val result = req<Transaction>(request = Transaction(id = 10))
        println(result)
    }
    @After
    fun tearDown() {
        scope.cancel()
    }
}
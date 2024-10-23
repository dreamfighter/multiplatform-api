import id.dreamfighter.multiplatform.api.Transaction
import id.dreamfighter.multiplatform.api.post
import kotlin.test.Test

class JvmHttpClientTest {

    @Test
    fun `test 3rd element`() {
        println("it")
        post<Transaction>(Transaction(id = 10)){

        }
    }
}
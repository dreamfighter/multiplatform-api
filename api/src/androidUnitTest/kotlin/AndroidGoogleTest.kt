import id.dreamfighter.multiplatform.api.google
import kotlin.test.Test

class AndroidGoogleTest {

    @Test
    fun `test google auth`() {
        google.auth( { _ -> })
        //assertEquals("IDR1,000.00", 1000.0.toCurrency("IDR"))
    }
}
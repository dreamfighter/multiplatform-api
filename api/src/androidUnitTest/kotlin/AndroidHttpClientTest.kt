import android.util.Log
import id.dreamfighter.multiplatform.api.ApiClient
import id.dreamfighter.multiplatform.api.Transaction
import id.dreamfighter.multiplatform.api.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test

class AndroidHttpClientTest {

    @Test
    fun testApi(){
        MainScope().launch(Dispatchers.Default) {
            Log.d("LOGGING","test")

        }

    }
}
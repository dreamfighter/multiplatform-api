package id.dreamfighter.multiplatform.api

import io.ktor.client.HttpClient
import java.util.Locale
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmMemberSignature.Field
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

actual val client: HttpClient
    get() = TODO("Not yet implemented")


actual inline fun <reified T : Any> getProperties(obj: T): Map<String, Any?> {
    val fields = mutableMapOf<String, Any?>()

    obj::class.java.methods.forEach { method ->
        if(method.name.startsWith("get"))
        println("${method.invoke(Unit)} = ${method.name}")
    }
    obj.javaClass.declaredFields.forEach { field ->

        fields[field.name] = "it.name"
    }
    return fields
}
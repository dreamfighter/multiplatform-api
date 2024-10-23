package id.dreamfighter.multiplatform.api.model

data class Request(
    var url:String,
    val method:String,
    val path:Map<String,Any?> = mapOf(),
    val body:Any? = null,
    val headers:Map<String,Any?> = mapOf(),
    val query:Map<String,Any?> = mapOf())

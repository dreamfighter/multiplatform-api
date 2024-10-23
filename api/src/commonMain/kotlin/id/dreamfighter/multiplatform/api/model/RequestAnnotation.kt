package id.dreamfighter.multiplatform.api.model

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Get(val url: String)

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Post(val url: String)

@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class Path(val name: String = "")

@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class Query(val name: String = "")

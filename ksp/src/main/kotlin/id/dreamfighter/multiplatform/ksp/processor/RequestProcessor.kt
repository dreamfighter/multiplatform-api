package id.dreamfighter.multiplatform.ksp.processor

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.ksp.toTypeName
import id.dreamfighter.multiplatform.annotation.Get
import id.dreamfighter.multiplatform.annotation.Path
import id.dreamfighter.multiplatform.annotation.Post
import id.dreamfighter.multiplatform.annotation.Query
import java.io.OutputStreamWriter

class RequestProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val allFiles = resolver.getSymbolsWithAnnotation(Get::class.qualifiedName.orEmpty()).map {
            it.containingFile!!
        }.toList()

        if (invoked) {
            return emptyList()
        }
        invoked = true

        codeGenerator.createNewFile(Dependencies(true), "", "ApiRequest", "kt").use { output ->
            OutputStreamWriter(output).use { writer ->
                if(allFiles.isNotEmpty()) {
                    val packageName = allFiles.first().packageName.asString()
                    writer.write("package $packageName\n\n")
                    writer.write("import id.dreamfighter.multiplatform.api.model.Request\n")
                    writer.write("object Req{\n")

                    val visitor = ClassVisitor(logger)
                    allFiles.forEach {
                        it.accept(visitor, writer)
                    }

                    writer.write("}\n")
                }
            }
        }
        return emptyList()
    }
}

class ClassVisitor(private val logger: KSPLogger) : KSTopDownVisitor<OutputStreamWriter, Unit>() {
    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) {
    }

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)
        val symbolName = classDeclaration.simpleName.asString()
        val methods = classDeclaration.annotations.filter {
            it.shortName.asString() == Get::class.simpleName || it.shortName.asString() == Post::class.simpleName
        }
        if(methods.iterator().hasNext()){
            var method = ""
            var url = ""
            val path: MutableMap<String, Any?> = mutableMapOf()
            val query:MutableMap<String,Any?> = mutableMapOf()
            val params:MutableMap<String,Any?> = mutableMapOf()
            val annotation = methods.first()
            logger.warn("${methods.first().arguments.first().value}")

            when(annotation.shortName.asString()){
                Get::class.simpleName -> {
                    method = "GET"
                    url = annotation.arguments.first().value as String
                }
                Post::class.simpleName -> {
                    method = "POST"
                    url = annotation.arguments.first().value as String
                }
            }

            classDeclaration.getDeclaredProperties().forEach { prop ->
                prop.annotations.forEach {
                    when(it.shortName.asString()){
                        Path::class.simpleName -> {
                            logger.info("path ${it.arguments[0].value}")
                            val name = if(it.arguments[0].value == null){
                                prop.simpleName.asString()
                            }else{
                                "${it.arguments[0].value}"
                            }
                            logger.info("name $name")
                            params[name] = prop.type
                            path[name] = prop.simpleName.asString()
                        }
                        Query::class.simpleName -> {
                            logger.info("query ${it.arguments[0]}")
                            val name = if(it.arguments[0].value == null){
                                prop.simpleName.asString()
                            }else{
                                "${it.arguments[0].value}"
                            }
                            query[name] = prop.type
                            params[name] = prop.simpleName.asString()
                        }
                    }
                }
            }

            val paramsString = params.map {
                "${it.key}:${it.value}"
            }.joinToString(",")

            data.write("""
    fun $symbolName($paramsString):Request {
        return Request(url = "$url", method = "$method", path = mapOf(${path.map {
                "\"${it.value}\" to ${it.value}"
            }.joinToString(",")}),query = mapOf(${query.map {
                "\"${it.value}\" to ${it.value}"
            }.joinToString(",")}))
    }

""".trimMargin())
        }

    }
}

class RequestProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return RequestProcessor(environment.codeGenerator, environment.logger)
    }
}
package ksp.processor

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import id.dreamfighter.multiplatform.ksp.ApiRequest

class KSPProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return BuilderProcessor(
            environment.codeGenerator,
            environment.logger
        )
    }
}

@ApiRequest
data class SampleJvm(val id:Int)

class BuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        resolver.getAllFiles().forEach {
            logger.warn("process ksp ${it.packageName}")
        }
        resolver.getSymbolsWithAnnotation(ApiRequest::class.qualifiedName.orEmpty(),inDepth = true).forEach {
            logger.warn("process ksp ${it.javaClass.packageName}")
            genFile(it.javaClass.packageName, it.javaClass.name,it.javaClass.simpleName).writeTo(codeGenerator, Dependencies(true))
        }
        return emptyList()
    }

    private fun getFactories(resolver: Resolver): Set<KSClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(ApiRequest::class.qualifiedName.orEmpty())
            .filterIsInstance<KSClassDeclaration>()
            .filter(KSNode::validate)
            .toSet()
    }

    private fun genFile(packageName:String, clz: String, simpleName: String): FileSpec {
        val key = clz
        val packageName = packageName
        val funcName = simpleName + "Factory"
        val enumName = simpleName + "Type"
        println("genFile")

        return FileSpec.builder(packageName, funcName)
            .addType(TypeSpec.classBuilder(simpleName).build())
            .addFunction(FunSpec.builder(funcName)
                .addParameter("key", ClassName(packageName, enumName))
                .beginControlFlow("return when (key)")
                .endControlFlow()
                .build())
            .build()
    }

    private fun getElements(
        resolver: Resolver,
        factories: Set<KSClassDeclaration>
    ): Map<KSClassDeclaration, List<ClassName>> {
        val result = mutableMapOf<KSClassDeclaration, MutableList<ClassName>>()
        factories.forEach {
            result[it] = mutableListOf()
        }
        logger.warn("test ${ApiRequest::class.qualifiedName.orEmpty()}")
        resolver.getSymbolsWithAnnotation(ApiRequest::class.qualifiedName.orEmpty())
            .filterIsInstance<KSClassDeclaration>()
            .filter(KSNode::validate)
            .forEach { d ->
                d.superTypes
                    .map { it.resolve().declaration.closestClassDeclaration() }
                    .filter { result.containsKey(it) }
                    .forEach { name ->
                        result[name]?.add(d.toClassName())
                    }
            }
        return result
    }
}
import com.github.gradle.node.npm.task.NpmTask

plugins {
  id("com.github.node-gradle.node") version "7.0.0"
  id("org.openapi.generator") version "7.7.0"
}

val generateFrontendApi by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
  inputSpec.set("$rootDir/docs/openapi/openapi.yaml")
  generatorName.set("typescript-angular")
  outputDir.set("$rootDir/web/src/app/generated")

  configOptions.set(
    mapOf(
      "ngVersion" to "16.0.0",
      "providedInRoot" to "true",
      "useTags" to "false",
      "modelPackage" to "models",
      "apiPackage" to "service",
      "apiModulePrefix" to ""
    )
  )

  globalProperties.set(
    mapOf(
      "models" to "",
      "apis" to "",
      "supportingFiles" to ""
    )
  )
}

node {
  download.set(true)
  version.set("20.0.0")
}

tasks.register<NpmTask>("npmBuild") {
  dependsOn("npmInstall")
  args.set(listOf("run", "build"))
}

tasks.register<NpmTask>("npmStart") {
  dependsOn("npmInstall")
  args.set(listOf("run", "start"))
}

plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.7.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "M450_LB1"

// === OpenAPI config ===
val openApiSpec = "$rootDir/docs/openapi/openapi.yaml"

openApiGenerate {
	inputSpec.set(openApiSpec)
	generatorName.set("spring")
	outputDir.set("$projectDir/src/main/java")

	apiPackage.set("com.example.rest.generated")
	modelPackage.set("com.example.rest.generated.model")
	invokerPackage.set("com.example.rest.generated")

	globalProperties.set(
		mapOf(
			"apis" to "",
			"models" to "",
			"supportingFiles" to "false"
		)
	)

	configOptions.set(
		mapOf(
			"useJakartaEe" to "true",
			"useSpringBoot3" to "true",
			"interfaceOnly" to "true",
			"skipDefaultInterface" to "true",
			"sourceFolder" to "",
			"additionalModelTypeAnnotations" to "@lombok.Data @lombok.AllArgsConstructor @lombok.Builder"
		)
	)
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    runtimeOnly("com.h2database:h2")

	// Swagger / OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

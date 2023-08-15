import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"

	jacoco

}


java {
	sourceCompatibility = JavaVersion.VERSION_17
}
extra["springCloudVersion"] = "2022.0.3"
repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
	implementation("org.springframework.boot:spring-boot-starter-logging")
	implementation("org.springframework.cloud:spring-cloud-starter")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("net.logstash.logback:logstash-logback-encoder:7.3")
	implementation("redis.clients:jedis:5.0.0-alpha2")
	implementation("org.apache.httpcomponents:httpclient:4.5.14")
	implementation("org.yaml:snakeyaml:2.0")
	implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
	implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("io.github.openfeign:feign-micrometer:12.3")
	implementation("io.github.openfeign:feign-jackson:12.3")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("mysql:mysql-connector-java:8.0.33")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	implementation("com.squareup.okhttp3:okhttp:4.11.0")
	implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("com.h2database:h2:2.2.220")
	testImplementation("org.mockito:mockito-inline:5.2.0")
	testImplementation(kotlin("test"))
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

springBoot {
	buildInfo()
}

jacoco {
	toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude("com/ouath/config/**")
				exclude("com/ouath/constant/**")
				exclude("com/ouath/data/**")
				exclude("com/ouath/model/**")
				exclude("com/ouath/client/**")
				exclude("com/ouath/exception/**")
			}
		})
	)
}

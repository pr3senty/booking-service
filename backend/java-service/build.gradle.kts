plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.4"
}

group = "edu.centraluniversity"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("org.postgresql:postgresql:42.7.2")
	implementation("io.grpc:grpc-netty-shaded:1.64.0")
	implementation("io.grpc:grpc-stub:1.64.0")
	implementation("io.grpc:grpc-protobuf:1.64.0")
	implementation("com.google.protobuf:protobuf-java:3.25.3")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.25.3"
	}
	plugins {
		create("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:1.64.0"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.builtins {
				it.builtins.named("java")
			}
			it.plugins {
				create("grpc")
			}
		}
	}
}


sourceSets {
	main {
		proto {
			srcDir("./proto")
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.avast.gradle.docker-compose") version "0.17.12"
}

group = "com.epam.demo"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

buildscript{
    extra.apply {
        set("springCloudAzureVersion", "5.20.1")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.azure.spring:spring-cloud-azure-starter-keyvault")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:${project.extra.get("springCloudAzureVersion")}")
    }
}

dockerCompose {
    useComposeFiles.set(listOf("docker-compose.yml"))
    forceRecreate = true
    waitForTcpPorts = false
    retainContainersOnStartupFailure = false
    captureContainersOutput = false

    setProjectName("spring-akv-mysql-demo")
    dockerComposeWorkingDirectory = project.file("${projectDir}/local")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "dev")
    systemProperty("javax.net.ssl.trustStore",
            file("$projectDir/local/lowkey-vault/lowkey-vault-keystore.p12"))
    systemProperty("javax.net.ssl.trustStorePassword", "changeit")
    systemProperty("javax.net.ssl.trustStoreType", "PKCS12")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to
    // simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    testLogging.showStandardStreams = true
    dependsOn(tasks.composeUp)
    finalizedBy(tasks.composeDown)
    doFirst {
        Thread.sleep(10000)
    }
}

tasks.bootRun {
    systemProperty("spring.profiles.active", "dev")
    systemProperty("javax.net.ssl.trustStore",
            file("$projectDir/local/lowkey-vault/lowkey-vault-keystore.p12"))
    systemProperty("javax.net.ssl.trustStorePassword", "changeit")
    systemProperty("javax.net.ssl.trustStoreType", "PKCS12")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to
    // simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT",
               "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    dependsOn(tasks.composeUp)
    finalizedBy(tasks.composeDown)
    doFirst {
        Thread.sleep(10000)
    }
}

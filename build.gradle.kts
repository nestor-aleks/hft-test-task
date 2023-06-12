val assertjVersion = "3.24.2"
val restAssuredVersion = "5.3.0"
val junitVersion = "5.9.1"

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    kotlin("plugin.serialization") version "1.8.21"
    id("io.gatling.gradle") version "3.9.5"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.github.serpro69:kotlin-faker:1.14.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:json-path:$restAssuredVersion")
    testImplementation("io.rest-assured:json-schema-validator:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.9.3")
    implementation("io.gatling:gatling-app:3.9.3")
    implementation("io.gatling:gatling-core:3.9.3")
}

gatling {
    // WARNING: options below only work when logback config file isn't provided
    logLevel = "WARN" // logback root level
    logHttp = io.gatling.gradle.LogHttp.NONE // set to 'ALL' for all HTTP traffic in TRACE, 'FAILURES' for failed HTTP traffic in DEBUG

//    enterprise.closureOf<Any> {
//        // Enterprise Cloud (https://cloud.gatling.io/) configuration reference: https://gatling.io/docs/gatling/reference/current/extensions/gradle_plugin/#working-with-gatling-enterprise-cloud
//        // Enterprise Self-Hosted configuration reference: https://gatling.io/docs/gatling/reference/current/extensions/gradle_plugin/#working-with-gatling-enterprise-self-hosted
//    }
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

//test {
//    useJUnitPlatform {
//        includeTags 'unitTest'
//    }
//}
//
//tasks.withType<Test> {
//    useJUnitPlatform()
//}
kotlin {
    jvmToolchain(11)
}
plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.7.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.17.2")
}

application {
    mainClass = "JavalinApp"
}

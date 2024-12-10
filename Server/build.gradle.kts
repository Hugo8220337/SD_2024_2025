plugins {
    id("java")
    id("application")
    kotlin("jvm")
}

group = "ipp.estg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("ipp.estg.Main")
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Gson
    implementation("com.google.code.gson:gson:2.10")

    /**
     *
     * // Usage:
     * JsonConverter converter = new JsonConverter();
     *
     * // Converting to JSON
     * List<User> users = userRepository.getPendingUsers(userType);
     * String json = converter.toJson(users);
     *
     * // Converting from JSON back to List<User>
     * List<User> userList = converter.fromJson(json, User.class);
     */
    implementation(kotlin("stdlib-jdk8"))



    // LogsForJava
    // SLF4J API
    implementation("org.slf4j:slf4j-api:2.0.7")

    // Logback Classic
    implementation("ch.qos.logback:logback-classic:1.4.8")

    // Log4j2 SLF4J implementation
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

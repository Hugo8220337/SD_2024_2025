plugins {
    id("java")
    id("application")
}

group = "ipp.estg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
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
}

tasks.test {
    useJUnitPlatform()
}
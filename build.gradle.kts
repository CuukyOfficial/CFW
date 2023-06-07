import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("java-library")
    id("maven-publish")
    id("checkstyle")
}

group = "de.varoplugin"
version = "1.0.0-ALPHA-2"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava { options.encoding = "UTF-8" }
tasks.javadoc { options.encoding = "UTF-8" }

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("com.github.cryptomorin:XSeries:9.3.1")
    implementation("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

tasks.jar {
    if (project.hasProperty("destinationDir"))
        destinationDirectory.set(file(project.property("destinationDir").toString()))
    if (project.hasProperty("fileName"))
        archiveFileName.set(project.property("fileName").toString())
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
        outputs.upToDateWhen { false }
    }
}

tasks.processResources {
    outputs.upToDateWhen { false }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(sourceSets.main.get().resources.srcDirs) {
        include("**/plugin.yml")
        expand("name" to project.name, "version" to project.version, "author" to "Cuuky")
    }
}

checkstyle {
    configDirectory.set(File("./checkstyle"))
    toolVersion = "9.3"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("CuukyFrameWork")
                description.set("A powerful bukkit framework that runs on 1.7+ and makes coding with bukkit less painful")
                url.set("https://github.com/CuukyOfficial/CFW")
            }
            artifactId = "cfw"
        }
    }

    repositories {
        maven {
            setUrl("https://repo.varoplugin.de/repository/maven-releases/")
            credentials {
                username = project.findProperty("repouser") as? String
                password = project.findProperty("repopassword") as? String
            }
        }
    }
}

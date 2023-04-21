import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.security.MessageDigest
import java.util.*

plugins {
    id("java-library")
    id("maven-publish")
//    id("checkstyle")
}

group = "de.varoplugin"
version = "1.0.0-ALPHA-1"

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

val internal: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val runtimeDownload: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

fun DependencyHandler.modularInternal(dependencyNotation: Any, localFileName: String) : Dependency? {
    val file = file("${rootDir}/libs/${localFileName}.jar")
    return if (file.exists())
        this.add("internal", files(file))
    else
        this.add("internal", dependencyNotation)
}

dependencies {
    modularInternal("com.github.cryptomorin:XSeries:9.3.1", "XSeries")
    implementation("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.jar {
    if (project.hasProperty("destinationDir"))
        destinationDirectory.set(file(project.property("destinationDir").toString()))
    if (project.hasProperty("fileName"))
        archiveFileName.set(project.property("fileName").toString())

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(internal.map { if (it.isDirectory) it else zipTree(it) })
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

val mdSha512: MessageDigest = MessageDigest.getInstance("SHA-512")
fun File.sha512() : ByteArray = mdSha512.digest(this.readBytes())
fun ByteArray.base64() : String = Base64.getEncoder().encodeToString(this)

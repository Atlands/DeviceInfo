plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}



val GROUP_ID = "com.github.Helios030"
val ARTIFACT_ID = "DeviceInfo"
val VERSION = latestGitTag().ifEmpty { "1.0.0" }


fun latestGitTag(): String {
    val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start()
    return  process.inputStream.bufferedReader().use {bufferedReader ->
        bufferedReader.readText().trim()
    }
}

// 创建一个task来发布源码
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    val sources = android.sourceSets.map { set -> set.java.getSourceFiles() }
    from(sources)
}

android {
    namespace = "com.qc.device"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }

    val GROUP_ID = "com.github.bqliang"
    val ARTIFACT_ID = "jitpack-lib-sample"
    val VERSION = latestGitTag().ifEmpty { "1.0.0-SNAPSHOT" }

    fun latestGitTag(): String {
        val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start()
        return  process.inputStream.bufferedReader().use {bufferedReader ->
            bufferedReader.readText().trim()
        }
    }

    afterEvaluate {
        publishing {
            publications {
                register<MavenPublication>("release") { // 注册一个名字为 release 的发布内容
                    groupId = GROUP_ID
                    artifactId = ARTIFACT_ID
                    version = VERSION

                    afterEvaluate { // 在所有的配置都完成之后执行
                        // 从当前 module 的 release 包中发布
                        from(components["release"])
                    }
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}



dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    api("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")


}








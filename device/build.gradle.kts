plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}
group = ("com.github.Helios030")

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

    afterEvaluate {
        publishing {
            repositories {
                maven {
                    // isAllowInsecureProtocol = true // 如果Maven仓库仅支持http协议, 请打开此注释
                    url = uri("https://github.com/Helios030/DeviceInfo.git") // 请填入你的仓库地址
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                    credentials {
                        username = "Helios030" // 请填入你的用户名
                        password = "Nb1300454585" // 请填入你的密码
                    }
                }
            }

            publications {
                create<MavenPublication>("product") {
                    from(components["release"])
                    groupId = "DeviceInfo" // 请填入你的组件名
                    artifactId = "DeviceInfo" // 请填入你的工件名
                    version = "v1.0.0" // 请填入工件的版本名
                    artifact(tasks["sourcesJar"]) // 打包源码到工件中
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








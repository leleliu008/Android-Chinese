import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.github.dcendents.android-maven")
    id("com.jfrog.bintray")
}

android {
    compileSdkVersion(26)
    buildToolsVersion("26.0.2")

    defaultConfig {
        minSdkVersion(14)
        targetSdkVersion(25)
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/libs")
            aidl.srcDirs("src/main/java")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    compileOptions {
        //使用JAVA8语法解析
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
}

dependencies {
    api(fileTree(mapOf(Pair("dir", "src/main/libs"), Pair("include", "*.jar"))))
    //http://kotlinlang.org/docs/reference/using-gradle.html#configuring-dependencies
    api("org.jetbrains.kotlin:kotlin-stdlib:1.2.21")
    api("com.android.support:support-annotations:26.1.0")
    api("com.android.support:appcompat-v7:26.1.0")
    api("com.android.support:design:26.1.0")
    api("com.fpliu:Android-BaseUI:1.0.0")
    api("com.fpliu:Android-CustomDimen:1.0.0")
    api("com.fpliu:Android-CustomDrawable:1.0.0")
    api("com.fpliu:Android-ExpandableList:1.0.0")
    api("com.fpliu:Android-List:1.0.0")
}

// 这里是groupId,必须填写,一般填你唯一的包名
group = "com.fpliu"

//这个是版本号，必须填写
version = "1.0.0"

//项目的主页,这个是说明，可随便填
val siteUrl = "https://github.com/leleliu008/Android-ChineseAdministrativeDivisions"

// GitHub仓库的URL,这个是说明，可随便填
val gitUrl = "https://github.com/leleliu008/Android-ChineseAdministrativeDivisions"

tasks {
    "install"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenInstaller {
                    pom.project {
                        withGroovyBuilder {
                            "packaging"("aar")
                            "name"(project.name)
                            "url"(siteUrl)
                            "licenses" {
                                "license" {
                                    "name"("The Apache Software License, Version 2.0")
                                    "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }
                            "developers" {
                                "developer" {
                                    "id"("fpliu")
                                    "name"("fpliu")
                                    "email"("leleliu008@gmail.com")
                                }
                            }
                            "scm" {
                                "connection"(gitUrl)
                                "developerConnection"(gitUrl)
                                "url"(siteUrl)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 生成jar包的task
val sourcesJarTask = task("sourcesJar", Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    baseName = "Android-ChineseAdministrativeDivisions"
    classifier = "sources"
}

// 生成jarDoc的task
task("javadoc", Javadoc::class) {
    source(android.sourceSets["main"].java.srcDirs)
    exclude("**/*.ks")
    classpath += project.files(android.bootClasspath)
    isFailOnError = false
}

// 生成javaDoc的jar
val javadocJarTask = task("javadocJar", Jar::class) {
    val javaDockTask = dependsOn("javadoc")
    baseName = "Android-ChineseAdministrativeDivisions"
    classifier = "javadoc"
    from(javaDockTask.path)
}

artifacts {
    add("archives", javadocJarTask)
    add("archives", sourcesJarTask)
}

val properties = Properties().apply { load(project.rootProject.file("local.properties").inputStream()) }
bintray {
    user = properties.getProperty("bintray.user")
    key = properties.getProperty("bintray.apikey")

    setConfigurations("archives")

    pkg = PackageConfig().apply {
        userOrg = "fpliu"
        repo = "newton"
        name = "Android-ChineseAdministrativeDivisions"
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        setLicenses("Apache-2.0")
        publish = true
    }
}

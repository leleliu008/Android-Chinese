buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        //用于构建Android工程的插件
        //https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
        classpath("com.android.tools.build:gradle:3.0.1")

        //Kotlin编译的插件
        //http://kotlinlang.org/docs/reference/using-gradle.html
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.21")

        //用于构建aar和maven包
        //https://github.com/dcendents/android-maven-gradle-plugin
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.0")

        //用于上传maven包到jCenter中
        //https://github.com/bintray/gradle-bintray-plugin
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
buildscript {
    repositories {
        jcenter { url = uri("https://maven.aliyun.com/repository/jcenter") }
        // google仓库在北京海淀区有CDN节点，所以，不需要替换
        google()
    }
    dependencies {
        //用于构建Android工程的插件
        //https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
        classpath("com.android.tools.build:gradle:3.2.1")

        //Kotlin编译的插件
        //http://kotlinlang.org/docs/reference/using-gradle.html
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
    }
}

allprojects {
    repositories {
        jcenter { url = uri("https://maven.aliyun.com/repository/jcenter") }
        // google仓库在北京海淀区有CDN节点，所以，不需要替换
        google()
        maven { url = uri("https://jitpack.io") }
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

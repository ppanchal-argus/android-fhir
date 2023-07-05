plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

kotlin { jvmToolchain(11) }

android {
  namespace = "com.google.android.fhir.testing"
  compileSdk = Sdk.compileSdk
  defaultConfig { minSdk = Sdk.minSdk }
  packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }
}

dependencies {
  api(project(":engine"))

  implementation(Dependencies.AndroidxTest.rules)
}

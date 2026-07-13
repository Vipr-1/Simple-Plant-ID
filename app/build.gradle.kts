import java.util.Properties
plugins {
	alias(libs.plugins.android.application)
}

android {
	namespace = "ca.pashko.simpleplantid"
	compileSdk {
		version = release(36) {
			minorApiLevel = 1
		}
	}
	
	defaultConfig {
		applicationId = "ca.pashko.simpleplantid"
		minSdk = 34
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"
		val properties = Properties()
		properties.load(project.rootProject.file("local.properties").inputStream())
		val apiKey = properties.getProperty("plantNetAPI") ?: "\"\""
		buildConfigField("String", "plantNetAPI", apiKey)
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	
	
	buildFeatures { buildConfig = true}
	
	buildTypes {
		release {
			optimization {
				enable = false
			}
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
}

dependencies {
	implementation(libs.androidx.activity.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.androidx.core.ktx)
	implementation("com.squareup.okhttp3:okhttp:5.4.0")
	implementation(libs.material)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.junit)
}
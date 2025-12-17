plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    modApi(libs.fabricapi.fabric)
    modApi(libs.puzzleslib.fabric)
    modCompileOnly(libs.appleskin.fabric)
    modLocalRuntime(libs.appleskin.fabric)
}

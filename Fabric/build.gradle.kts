plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    modApi(sharedLibs.fabricapi.fabric)
    modApi(sharedLibs.puzzleslib.fabric)
    modCompileOnly(sharedLibs.appleskin.fabric)
    modLocalRuntime(sharedLibs.cloth.fabric)
    modLocalRuntime(sharedLibs.appleskin.fabric)
}

multiloader {
    mixins {
        clientMixin("Gui\u0024HeartTypeFabricMixin")
    }
}

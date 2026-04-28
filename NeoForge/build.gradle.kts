plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    modApi(sharedLibs.puzzleslib.neoforge)
    modLocalRuntime(sharedLibs.appleskin.neoforge)
}

multiloader {
    modFile {
        enumExtensions.set("META-INF/enumextensions.json")
    }
}

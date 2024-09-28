package fuzs.overflowingbars.client.packs;

import com.mojang.blaze3d.platform.NativeImage;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.handler.NewHealthBarRenderer;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicallyCopiedPackResources extends AbstractModPackResources {
    private final ResourceManager resourceManager;
    private final VanillaPackResources vanillaPackResources;
    private final Map<ResourceLocation, TextureCopy> textures;

    protected DynamicallyCopiedPackResources(TextureCopy... textures) {
        Minecraft minecraft = Minecraft.getInstance();
        this.resourceManager = minecraft.getResourceManager();
        this.vanillaPackResources = minecraft.getVanillaPackResources();
        this.textures = Stream.of(textures)
                .collect(Collectors.toMap(TextureCopy::destinationLocation, Function.identity()));
    }

    public static Supplier<AbstractModPackResources> create(TextureCopy... textures) {
        return () -> {
            return new DynamicallyCopiedPackResources(textures);
        };
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
        if (this.textures.containsKey(resourceLocation)) {
            TextureCopy textureCopy = this.textures.get(resourceLocation);
            Optional<Resource> vanillaResource = this.resourceManager.getResource(textureCopy.vanillaLocation());
            if (vanillaResource.isPresent()) {
                try (NativeImage nativeImage = NativeImage.read(vanillaResource.get().open())) {
                    // check the vanilla texture aspect ratio; some mods using OptiFine change the texture file completely since they also change the model
                    // make sure to check the aspect ratio instead of absolute width / height to support higher resolution resource packs
                    // in that case fall back to the skeleton texture from the vanilla assets pack
                    if (nativeImage.getWidth() / nativeImage.getHeight() !=
                            textureCopy.vanillaImageWidth() / textureCopy.vanillaImageHeight()) {
                        return this.vanillaPackResources.getResource(packType, textureCopy.vanillaLocation());
                    }
                } catch (IOException exception) {
                    // NO-OP
                }
                return vanillaResource.get()::open;
            }
        }

        return null;
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
//        return this.textures.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
        return Collections.singleton(OverflowingBars.MOD_ID);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        String prefix = "textures/gui/sprites";
        if (path.startsWith(prefix)) {
            FileToIdConverter textureIdConverter = SpriteSource.TEXTURE_ID_CONVERTER;
            int[] dyes = new int[]{0x004D81, 0x007C77, 0x047101, 0xFF9400, 0xB51700, 0x9A1860};
//            int[] dyes = new int[]{0x41B0F6, 0x74FBEA, 0x89F94F, 0xFFFC67, 0xFE968D, 0xFF8EC6};
//            int[] dyes = new int[]{0xF06E14};
            for (int dye : dyes) {

                Stream.of(Gui.HeartType.values())
                        .flatMap(NewHealthBarRenderer::getHeartTypeTextureLocations)
                        .forEach(resourceLocation -> {
                            ResourceLocation resourceLocation1 = OverflowingBars.id(resourceLocation.getPath()).withSuffix("_" + dye);
                            ResourceLocation resourceLocation2 = resourceLocation.withPrefix(prefix + "/").withSuffix(".png");
                            Optional<Resource> resource = this.resourceManager.getResource(resourceLocation2);
                            if (resource.isPresent()) {
                                ResourceLocation t = resourceLocation1.withPrefix(prefix + "/").withSuffix(".png");
                                resourceOutput.accept(t, () -> makeImageTranslucent(resource.get()::open, dye));
                            }
                        });
            }
        }
    }

    private static InputStream makeImageTranslucent(IoSupplier<InputStream> supplier, int dye) throws IOException {
        try (InputStream inputStream = supplier.get(); NativeImage image = NativeImage.read(inputStream)) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int pixel = image.getPixelRGBA(x, y);
//                    pixel = getColorOverlayOperator(0x808080).applyAsInt(toGreyscale(pixel));
//                    pixel = getColorOverlayOperator(dye).applyAsInt(toGreyscale(pixel));
                    pixel = colorizePixel(pixel, dye);
//                    pixel = lerpColor(toGreyscale(pixel), 0x2DB928, 0.5);
//                    pixel = applyBlend(toGreyscale(pixel), dye);
                    image.setPixelRGBA(x, y, pixel);
                }
            }
            return new ByteArrayInputStream(image.asByteArray());
        }
    }

    public static int applyBlend(int greyscale, int dye) {
        int r = softLightInt(FastColor.ABGR32.red(greyscale), FastColor.ABGR32.red(dye));
        int g = softLightInt(FastColor.ABGR32.green(greyscale), FastColor.ABGR32.green(dye));
        int b = softLightInt(FastColor.ABGR32.blue(greyscale), FastColor.ABGR32.blue(dye));
        return FastColor.ABGR32.color(FastColor.ABGR32.alpha(greyscale), b, g ,r);
    }

    public static int softLightInt(int a, int b) {
        return Mth.clamp((int) (softLight(a / 255.0, b / 255.0) * 255.0), 0, 255);
    }

    public static double softLight(double a, double b) {
        if (b < 0.5) {
            return 2*a*b+a*a*(1.0-2*b);
        }else {
            return 2*a*(255-b)+Math.sqrt(a)*(2*b-1.0);
        }
    }

    public static int lerpColor(int greyscale, int dye, double range) {
        int blueBase = (int) (FastColor.ABGR32.blue(greyscale) + (FastColor.ABGR32.blue(dye) - FastColor.ABGR32.blue(greyscale)) * range);
        int greenBase = (int) (FastColor.ABGR32.green(greyscale) + (FastColor.ABGR32.green(dye) - FastColor.ABGR32.green(greyscale)) * range);
        int redBase = (int) (FastColor.ABGR32.red(greyscale) + (FastColor.ABGR32.red(dye) - FastColor.ABGR32.red(greyscale)) * range);
        return FastColor.ABGR32.color(FastColor.ABGR32.alpha(greyscale), blueBase, greenBase, redBase);
    }

    public static IntUnaryOperator getColorOverlayOperator(int color) {
        return getColorOverlayOperator(FastColor.ABGR32.red(color), FastColor.ABGR32.green(color), FastColor.ABGR32.blue(color));
    }

    public static IntUnaryOperator getColorOverlayOperator(int redColor, int greenColor, int blueColor) {
        return (pixel) -> {
            int alphaBase = FastColor.ABGR32.alpha(pixel);
            // no need to do any extra processing as nothing is going to be colored
            if (alphaBase == 0) return pixel;

            int blueBase = FastColor.ABGR32.blue(pixel);
            int greenBase = FastColor.ABGR32.green(pixel);
            int redBase = FastColor.ABGR32.red(pixel);

            final int blue, green, red;

            if (blueBase < 128) {
                blue = 2 * (blueBase * blueColor / 255);
            } else {
                blue = 255 - (2 * (255 - blueBase) * (alphaBase - blueColor) / 255);
            }

            if (greenBase < 128) {
                green = 2 * (greenBase * greenColor / 255);
            } else {
                green = 255 - (2 * (255 - greenBase) * (alphaBase - greenColor) / 255);
            }

            if (redBase < 128) {
                red = 2 * (redBase * redColor / 255);
            } else {
                red = 255 - (2 * (255 - redBase) * (alphaBase - redColor) / 255);
            }

            return FastColor.ABGR32.color(alphaBase, blue, green, red);
        };
    }

    public static int colorizePixel(int argb, int dye) {

        // https://stackoverflow.com/a/75527017
        float hue = Color.RGBtoHSB(FastColor.ABGR32.red(dye), FastColor.ABGR32.green(dye), FastColor.ABGR32.blue(dye),
                null
        )[0];

        int alpha = (argb & 0xff000000);
        int grayLevel = toGreyscale(argb) & 255;
        float brightness = grayLevel / 255f;
        int rgb = Color.HSBtoRGB(hue, 1.0F, brightness);

        argb = (rgb & 0x00ffffff) | alpha;
        return argb;
    }

    public static int toGreyscale(int color) {
        int alpha = FastColor.ABGR32.alpha(color);
        if (alpha == 0) {
            return color;
        } else {
            int red = FastColor.ABGR32.red(color);
            int green = FastColor.ABGR32.green(color);
            int blue = FastColor.ABGR32.blue(color);
            // https://www.baeldung.com/cs/convert-rgb-to-grayscale
            int avg = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
//            int avg = (red + green + blue) / 3;
            return alpha << 24 | avg << 16 | avg << 8 | avg;
        }
    }

    public record TextureCopy(ResourceLocation vanillaLocation,
                              ResourceLocation destinationLocation,
                              int vanillaImageWidth,
                              int vanillaImageHeight) {

        public TextureCopy {
            if (vanillaLocation.getNamespace().equals(destinationLocation.getNamespace())) {
                throw new IllegalStateException("%s and %s share same namespace".formatted(vanillaLocation, destinationLocation));
            }
            if (!vanillaLocation.getPath().endsWith(".png")) {
                throw new IllegalArgumentException("%s is no texture location".formatted(vanillaLocation));
            }
            if (!destinationLocation.getPath().endsWith(".png")) {
                throw new IllegalArgumentException("%s is no texture location".formatted(destinationLocation));
            }
        }
    }
}

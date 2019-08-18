package net.ilexiconn.llibrary;

import net.ilexiconn.llibrary.client.ClientProxy;
import net.ilexiconn.llibrary.client.lang.LanguageHandler;
import net.ilexiconn.llibrary.client.util.ItemTESRContext;
import net.ilexiconn.llibrary.server.ServerProxy;
import net.ilexiconn.llibrary.server.capability.IEntityDataCapability;
import net.ilexiconn.llibrary.server.config.LLibraryConfig;
import net.ilexiconn.llibrary.server.core.api.LLibraryCoreAPI;
import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.ilexiconn.llibrary.server.network.AnimationMessage;
import net.ilexiconn.llibrary.server.network.BlockEntityMessage;
import net.ilexiconn.llibrary.server.network.NetworkHandler;
import net.ilexiconn.llibrary.server.network.NetworkChannel;
import net.ilexiconn.llibrary.server.network.PropertiesMessage;
import net.ilexiconn.llibrary.server.network.SnackbarMessage;
import net.ilexiconn.llibrary.server.network.SurvivalTabMessage;
import net.ilexiconn.llibrary.server.world.TickRateHandler;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Map;

@Mod(
        "llibrary"
        /*name = "LLibrary",
        version = LLibrary.VERSION,
        acceptedMinecraftVersions = "1.12.2",
        certificateFingerprint = "${fingerprint}",
        guiFactory = "net.ilexiconn.llibrary.client.gui.LLibraryGUIFactory",
        updateJSON = "https://gist.githubusercontent.com/gegy1000/a6639456aeb8edd92cbf7cbfcf9d65d9/raw/llibrary_updates.json",
        dependencies = "required-after:forge@[14.23.3.2655,)"*/
)
public class LLibrary {
    public static final String VERSION = "1.7.15";

    public static final Logger LOGGER = LogManager.getLogger("LLibrary");
    public static ServerProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static LLibrary INSTANCE;
    @CapabilityInject(IEntityDataCapability.class)
    public static Capability<IEntityDataCapability> ENTITY_DATA_CAPABILITY;
    public static LLibraryConfig CONFIG = new LLibraryConfig();
    public static final String NETWORKING_PROTOCOL_VERSION = "1.0";
    @NetworkChannel({ AnimationMessage.class, PropertiesMessage.class, SnackbarMessage.class, BlockEntityMessage.class, SurvivalTabMessage.class })
    public static SimpleChannel NETWORK_WRAPPER;
    public static int QUBBLE_VERSION = 1;
    public static int QUBBLE_VANILLA_VERSION = 1;

    public static final File LLIBRARY_ROOT = new File(".", "llibrary");

    static {
        try {
            LLibraryPlugin.api = new CoreAPIHandler();
        } catch (Throwable e) {
            LOGGER.error("Failed to load LLibrary Core API. Is it missing?", e);
        }
    }

    private LLibrary() {
        INSTANCE = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPreInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPostInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);
    }

    public void onPreInit(FMLCommonSetupEvent event) {
        if (!LLibrary.LLIBRARY_ROOT.exists()) {
            LLibrary.LLIBRARY_ROOT.mkdirs();
        }

        // Inject network channels inside fields of each mod
        ModList.get().getAllScanData().forEach(NetworkHandler.INSTANCE::injectNetworkChannels);
        LLibrary.CONFIG.load();
        LLibrary.PROXY.onPreInit();

        onInit(event);
    }

    public void onInit(FMLCommonSetupEvent event) {
        LLibrary.PROXY.onInit();
    }

    public void onPostInit(FMLLoadCompleteEvent event) {
        LLibrary.PROXY.onPostInit();
    }

    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Detected invalid fingerprint for file {}! You will not receive support with this tampered version of llibrary!", event.getSource().getName());
    }

    static class CoreAPIHandler implements LLibraryCoreAPI {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void addRemoteLocalizations(String language, Map<String, String> properties) {
            LanguageHandler.INSTANCE.addRemoteLocalizations(language, properties);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void provideStackContext(@Nonnull ItemStack stack) {
            ItemTESRContext.INSTANCE.provideStackContext(stack);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void providePerspectiveContext(@Nonnull ItemCameraTransforms.TransformType transform) {
            ItemTESRContext.INSTANCE.providePerspectiveContext(transform);
        }

        @Override
        public long getTickRate() {
            return TickRateHandler.INSTANCE.getTickRate();
        }
    }
}

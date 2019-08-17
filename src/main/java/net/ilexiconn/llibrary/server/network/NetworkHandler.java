package net.ilexiconn.llibrary.server.network;

import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handler class for message IDs.
 *
 * @author iLexiconn
 * @since 1.2.0
 */
public enum NetworkHandler {
    INSTANCE;

    private Map<SimpleChannel, Integer> idMap = new HashMap<>();

    /**
     * Register a message to both sides.
     *
     * @param networkWrapper the network wrapper
     * @param clazz          the message class
     * @param <T>            the message type
     */
    public <T extends AbstractMessage<T>> void registerMessage(SimpleChannel networkWrapper, Class<T> clazz) {
        try {
            AbstractMessage<T> message = clazz.getDeclaredConstructor().newInstance();
            if (message.canSideReceive(Dist.CLIENT)) {
                this.registerMessage(networkWrapper, clazz, Dist.CLIENT);
            }
            if (message.canSideReceive(Dist.DEDICATED_SERVER)) {
                this.registerMessage(networkWrapper, clazz, Dist.DEDICATED_SERVER);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a message to a specific side.
     *
     * @param networkWrapper the network wrapper
     * @param clazz          the message class
     * @param side           the side
     * @param <T>            the message type
     * @deprecated use {@link NetworkHandler#registerMessage(SimpleChannel, Class)} in combination with {@link AbstractMessage#canSideReceive(Dist)} instead.
     */
    @Deprecated
    public <T extends AbstractMessage<T>> void registerMessage(SimpleChannel networkWrapper, Class<T> clazz, Dist side) {
        int id = 0;
        if (this.idMap.containsKey(networkWrapper)) {
            id = this.idMap.get(networkWrapper);
        }
        networkWrapper.registerMessage(id, clazz, T::toBytes, packetBuffer -> {
            T packet = null;
            try {
                packet = clazz.newInstance();
                packet.fromBytes(packetBuffer);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return packet;
        }, T::onMessage);
        this.idMap.put(networkWrapper, id + 1);
    }

    private void registerOnField(String modids, Field field) throws IllegalAccessException {
        NetworkChannel annotation = field.getAnnotation(NetworkChannel.class);
        SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("llibrary", "network/"+modids))
                .networkProtocolVersion(() -> LLibrary.NETWORKING_PROTOCOL_VERSION)
                .clientAcceptedVersions(clientVersion -> clientVersion.equals(LLibrary.NETWORKING_PROTOCOL_VERSION))
                .serverAcceptedVersions(serverVersion -> serverVersion.equals(LLibrary.NETWORKING_PROTOCOL_VERSION))
                .simpleChannel();
        field.set(null, channel);
        for (Class messageClass : annotation.value()) {
            this.registerMessage(channel, messageClass);
        }
    }

    public void injectNetworkChannels(ModFileScanData scanData) {
        scanData.getAnnotations().stream()
                .filter(annotationData -> annotationData.getClassType().getInternalName().equals(NetworkChannel.class.getCanonicalName().replace(".", "/")))
                .filter(annotationData -> annotationData.getTargetType() == ElementType.FIELD)
                .forEach(annotationData -> {
                    try {
                        Class<?> targetClass = FMLLoader.getLaunchClassLoader().getLoadedClass(annotationData.getClassType().getClassName());
                        String fieldName = annotationData.getMemberName();
                        Field field = targetClass.getDeclaredField(fieldName);
                        field.setAccessible(true);

                        String modids = scanData.getIModInfoData().stream()
                                .map(IModFileInfo::getMods)
                                .map(modInfos -> modInfos.stream().map(IModInfo::getModId))
                                .flatMap(Function.identity())
                                .collect(Collectors.joining("&"));
                        registerOnField(modids, field);
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                });
    }
}

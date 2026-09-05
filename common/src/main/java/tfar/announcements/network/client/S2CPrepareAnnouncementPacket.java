package tfar.announcements.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import tfar.announcements.EnumIdMapper;
import tfar.announcements.network.PacketHandler;
import tfar.announcements.platform.Services;

public enum S2CPrepareAnnouncementPacket implements S2CModPacket {
    INSTANCE;
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPrepareAnnouncementPacket> STREAM_CODEC =
            (StreamCodec<RegistryFriendlyByteBuf, S2CPrepareAnnouncementPacket>)(Object)
                   ByteBufCodecs.idMapper(new EnumIdMapper<>(S2CPrepareAnnouncementPacket.class));

    public static final Type<S2CPrepareAnnouncementPacket> TYPE = new Type<>(PacketHandler.packet(S2CPrepareAnnouncementPacket.class));

    public void handleClient() {
        Services.PLATFORM.openScreen();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

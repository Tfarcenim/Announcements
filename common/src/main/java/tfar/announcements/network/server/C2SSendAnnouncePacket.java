package tfar.announcements.network.server;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.announcements.Announcements;
import tfar.announcements.network.PacketHandler;
import tfar.announcements.network.client.S2CAnnouncementPacket;
import tfar.announcements.platform.Services;

public record C2SSendAnnouncePacket(String text, ChatFormatting color, int size, boolean shake, int time) implements C2SModPacket{

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSendAnnouncePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, C2SSendAnnouncePacket::text,
                    Announcements.CHAT_FORMATTING_STREAM_CODEC, C2SSendAnnouncePacket::color,
                    ByteBufCodecs.INT, C2SSendAnnouncePacket::size,
                    ByteBufCodecs.BOOL, C2SSendAnnouncePacket::shake,
                    ByteBufCodecs.INT, C2SSendAnnouncePacket::time,
                    C2SSendAnnouncePacket::new);

    public static final Type<C2SSendAnnouncePacket> TYPE = new Type<>(PacketHandler.packet(C2SSendAnnouncePacket.class));

    @Override
    public void handleServer(ServerPlayer player) {
        if (Announcements.canSendAnnouncements(player.createCommandSourceStack())) {
            Services.PLATFORM.sendToClients(new S2CAnnouncementPacket(text,color,size,shake,time),player.getServer().getPlayerList().getPlayers());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package tfar.announcements.network.server;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.announcements.Announcements;
import tfar.announcements.TextPosition;
import tfar.announcements.network.PacketHandler;
import tfar.announcements.network.client.S2CAnnouncementPacket;
import tfar.announcements.platform.Services;

public record C2SSendAnnouncementPacket(String text, ChatFormatting color, int size, boolean shake, int time, TextPosition textPosition) implements C2SModPacket{

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSendAnnouncementPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, C2SSendAnnouncementPacket::text,
                    Announcements.CHAT_FORMATTING_STREAM_CODEC, C2SSendAnnouncementPacket::color,
                    ByteBufCodecs.INT, C2SSendAnnouncementPacket::size,
                    ByteBufCodecs.BOOL, C2SSendAnnouncementPacket::shake,
                    ByteBufCodecs.INT, C2SSendAnnouncementPacket::time,
                    TextPosition.STREAM_CODEC, C2SSendAnnouncementPacket::textPosition,
                    C2SSendAnnouncementPacket::new);

    public static final Type<C2SSendAnnouncementPacket> TYPE = new Type<>(PacketHandler.packet(C2SSendAnnouncementPacket.class));

    @Override
    public void handleServer(ServerPlayer player) {
        if (Announcements.canSendAnnouncements(player.createCommandSourceStack())) {
            Services.PLATFORM.sendToClients(new S2CAnnouncementPacket(text,color,size,shake,time,textPosition),player.getServer().getPlayerList().getPlayers());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

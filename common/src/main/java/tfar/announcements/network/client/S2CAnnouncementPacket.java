package tfar.announcements.network.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import tfar.announcements.Announcements;
import tfar.announcements.AnnouncementsClient;
import tfar.announcements.TextPosition;
import tfar.announcements.network.PacketHandler;

public record S2CAnnouncementPacket(String text, ChatFormatting color, int size, boolean shake, int time, TextPosition textPosition) implements S2CModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CAnnouncementPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, S2CAnnouncementPacket::text,
                Announcements.CHAT_FORMATTING_STREAM_CODEC, S2CAnnouncementPacket::color,
                ByteBufCodecs.INT, S2CAnnouncementPacket::size,
                ByteBufCodecs.BOOL, S2CAnnouncementPacket::shake,
                ByteBufCodecs.INT, S2CAnnouncementPacket::time,
                TextPosition.STREAM_CODEC, S2CAnnouncementPacket::textPosition,
                S2CAnnouncementPacket::new);

    public static final Type<S2CAnnouncementPacket> TYPE = new Type<>(PacketHandler.packet(S2CAnnouncementPacket.class));

    public void handleClient() {
        AnnouncementsClient.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

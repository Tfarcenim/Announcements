package tfar.announcements.network;

import net.minecraft.resources.ResourceLocation;
import tfar.announcements.Announcements;
import tfar.announcements.network.client.S2CAnnouncementPacket;
import tfar.announcements.network.client.S2CPrepareAnnouncementPacket;
import tfar.announcements.network.server.C2SSendAnnouncePacket;
import tfar.announcements.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerClientPacket(S2CAnnouncementPacket.TYPE, S2CAnnouncementPacket.STREAM_CODEC);
        Services.PLATFORM.registerClientPacket(S2CPrepareAnnouncementPacket.TYPE, S2CPrepareAnnouncementPacket.STREAM_CODEC);
        Services.PLATFORM.registerServerPacket(C2SSendAnnouncePacket.TYPE, C2SSendAnnouncePacket.STREAM_CODEC);
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return Announcements.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}

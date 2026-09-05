package tfar.announcements;

import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import tfar.announcements.network.client.S2CAnnouncementPacket;

public class AnnouncementsClient {
    static @Nullable Component announcement;
    static int size;
    static boolean shake;
    static long timestamp;
    static long time;
    static long fadeTime = 0;
    static TextPosition textPosition;


    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        long currentTime = Util.getMillis();
        if (announcement != null && currentTime - timestamp <= time + fadeTime) {
            int x = guiGraphics.guiWidth() / 2;
            int y;

            if (textPosition == TextPosition.CENTER) {
                y = guiGraphics.guiHeight() / 2;
            } else {
                y = guiGraphics.guiHeight() * 3 / 4;
            }

            if (shake) {
                x += Math.random()*4;
                y += Math.random()*4;
            }

            int textWidth = Minecraft.getInstance().font.width(announcement);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(size, size, size);
            guiGraphics.drawString(Minecraft.getInstance().font,announcement,(x - size * textWidth/2) / size,y/size,0xffffffff,true);
            guiGraphics.pose().popPose();
        }
    }

    static void tick() {
    }

    public static void handle(S2CAnnouncementPacket packet) {
        announcement = Component.literal(packet.text()).withStyle(packet.color());
        timestamp = Util.getMillis();
        size = packet.size();
        shake = packet.shake();
        time = packet.time() * 50L;
        textPosition = packet.textPosition();
    }
}

package tfar.announcements;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@Mod(value = Announcements.MOD_ID,dist = Dist.CLIENT)
public class AnnouncementsClientNeoforge {
    public AnnouncementsClientNeoforge(IEventBus bus) {
        bus.addListener(this::overlay);
    }

    public static void openScreen() {
        Minecraft.getInstance().setScreen(new PrepareAnnouncementScreen(Component.empty()));
    }

    void overlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Announcements.id("overlay"), AnnouncementsClient::renderOverlay);
    }

}

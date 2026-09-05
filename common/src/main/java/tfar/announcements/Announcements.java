package tfar.announcements;

import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.IntFunction;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class Announcements {

    public static final String MOD_ID = "announcements";
    public static final String MOD_NAME = "Announcements";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final IntFunction<ChatFormatting> BY_ID = ByIdMap.continuous(Enum::ordinal,
            ChatFormatting.values(),
            ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, ChatFormatting> CHAT_FORMATTING_STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.

    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static boolean canSendAnnouncements(CommandSourceStack player) {
        return player.hasPermission(Commands.LEVEL_GAMEMASTERS);
    }
}
package tfar.announcements;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import tfar.announcements.network.client.S2CAnnouncementPacket;
import tfar.announcements.network.client.S2CPrepareAnnouncementPacket;
import tfar.announcements.platform.NeoForgePlatformHelper;
import tfar.announcements.platform.Services;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

@Mod(Announcements.MOD_ID)
public class AnnouncementsNeoForge {

    public AnnouncementsNeoForge(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(this::commands);
        eventBus.addListener(PacketHandlerNeoForge::register);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        Announcements.init();
    }

    //(ex: /announce test red 20 (size) shake: true)
    void commands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();



        dispatcher.register(Commands.literal("announce")
                .requires(Announcements::canSendAnnouncements)
                .then(thenCommon(AnnouncementsNeoForge::announce)
                )
        );

        dispatcher.register(Commands.literal("actionbar")
                .requires(Announcements::canSendAnnouncements)
                .then(thenCommon(AnnouncementsNeoForge::actionBar)
                )
        );

        dispatcher.register(Commands.literal("announcegui")
                .requires(Announcements::canSendAnnouncements)
                .executes(AnnouncementsNeoForge::announceGui)
        );
    }

    static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> thenCommon(Command<CommandSourceStack> executes) {
        return Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("text", StringArgumentType.string())
                        .then(Commands.argument("color", StringArgumentType.string())
                                .suggests(COLOR)
                                .then(Commands.argument("size", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("shake", BoolArgumentType.bool())
                                                .then(Commands.argument("time", IntegerArgumentType.integer(1))
                                                        .executes(executes)
                                                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                                                .executes(executes)
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    public static int announceGui(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Services.PLATFORM.sendToClient(S2CPrepareAnnouncementPacket.INSTANCE, context.getSource().getPlayerOrException());
        return 1;
    }

    public static int announce(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return commonExecute(context,TextPosition.CENTER);
    }

    public static int actionBar(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return commonExecute(context,TextPosition.ACTION_BAR);
    }

    public static int commonExecute(CommandContext<CommandSourceStack> context,TextPosition textPosition) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
        String text = StringArgumentType.getString(context, "text");
        ChatFormatting color = ChatFormatting.valueOf(StringArgumentType.getString(context, "color").toUpperCase(Locale.ROOT));
        int size = IntegerArgumentType.getInteger(context, "size");
        boolean shake = BoolArgumentType.getBool(context, "shake");
        int time = IntegerArgumentType.getInteger(context, "time");

        ResourceKey<Level> dimension = null;

        try {
            dimension = DimensionArgument.getDimension(context,"dimension").dimension();
        } catch (Exception ignored) {
        }

        for (ServerPlayer player : players) {
            if (dimension == null || player.level().dimension().equals(dimension)) {
                Services.PLATFORM.sendToClient(new S2CAnnouncementPacket(text, color, size, shake, time, textPosition), player);
            }
        }
        return 1;
    }

    public static final SuggestionProvider<CommandSourceStack> COLOR = SuggestionProviders.
            register(Announcements.id("colors"), (context, builder) ->
                    SharedSuggestionProvider.suggest(ChatFormatting.getNames(true, false), builder));

}
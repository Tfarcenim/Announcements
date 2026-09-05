package tfar.announcements;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColorButton extends Button {
    private ChatFormatting chatFormatting;

    protected ColorButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, ChatFormatting chatFormatting) {
        super(x, y, width, height, message, onPress, createNarration);
        this.chatFormatting = chatFormatting;
    }

    public ColorButton(Builder builder, ChatFormatting color) {
        this(builder.x,builder.y,builder.width,builder.height,builder.message,builder.onPress,builder.createNarration,color);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(getX(),getY(),getX()+width,getY()+height,0xff000000 | chatFormatting.getColor());
    }
}

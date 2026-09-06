package tfar.announcements;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import tfar.announcements.network.server.C2SSendAnnouncementPacket;
import tfar.announcements.platform.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PrepareAnnouncementScreen extends Screen {

    public static final ResourceLocation BACKGROUND = Announcements.id("background");

    /** The X size of the inventory window in pixels. */
    protected int imageWidth = 320;
    /** The Y size of the inventory window in pixels. */
    protected int imageHeight = 230;
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int leftPos;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int topPos;
    protected int titleLabelX;
    protected int titleLabelY;

    private EditBox textBox;
    private EditBox timeBox;
    private boolean shake;
    private int size = 1;
    private ChatFormatting color = ChatFormatting.WHITE;
    private TextPosition textPosition = TextPosition.CENTER;
    ResourceKey<Level> dimension;

    protected PrepareAnnouncementScreen(Component title) {
        super(title);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    Button dimensionButton;
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        textBox= new EditBox(this.font, leftPos + 32, topPos + 14 , imageWidth - 40, 14, Component.translatable("container.repair"));
        textBox.setTextColor(-1);
        textBox.setTextColorUneditable(-1);
        //textBox.setBordered(false);
        textBox.setMaxLength(64);
        textBox.setResponder(this::onTextInput);
        textBox.setValue("");
        this.addRenderableWidget(textBox);

        timeBox= new EditBox(this.font, leftPos + 32, topPos + 64 , 103, 14, Component.translatable("container.repair"));
        timeBox.setTextColor(-1);
        timeBox.setTextColorUneditable(-1);
        //timeBox.setBordered(false);
        timeBox.setMaxLength(9);
        timeBox.setResponder(this::onTextInput);
        timeBox.setValue("30");
        this.addRenderableWidget(timeBox);

        Button button = Button.builder(Component.literal("Send Announcement"),button1 -> sendAnnouncement())
                .bounds(this.leftPos+4, topPos +  this.imageHeight - 25, imageWidth-8, 16)
                .build();

        addRenderableWidget(button);

        Button decSizeButton = Button.builder(Component.literal("<"),b -> decrSize())
                .bounds(leftPos+32,topPos+30,14,14)
                .build();

        Button incSizeButton = Button.builder(Component.literal(">"),b -> incSize())
                .bounds(leftPos + 59,topPos+30,14,14)
                .build();

        addRenderableWidget(decSizeButton);
        addRenderableWidget(incSizeButton);

        Checkbox checkbox = Checkbox.builder(Component.empty(),font)
                .pos(this.leftPos + 45, this.topPos + 97)
                .onValueChange((checkbox1, value) -> {
                    if (value) {
                        textPosition = TextPosition.ACTION_BAR;
                    } else {
                        textPosition = TextPosition.CENTER;
                    }
                })
                .build();

        Checkbox actionBar = Checkbox.builder(Component.empty(),font)
                .pos(this.leftPos + 135, this.topPos + 97)
                .onValueChange((checkbox1, value) -> shake = value)
                .build();

        ChatFormatting[] values = ChatFormatting.values();
        for (int i = 0; i < values.length; i++) {
            ChatFormatting color = values[i];
            if (!color.isColor())continue;
            ColorButton colorButton = new ColorButton(Button.builder(Component.empty(), button1 -> {
                textBox.setBordered(color != ChatFormatting.BLACK);
                textBox.setTextColor(color.getColor());
                this.color = color;
            }).bounds(leftPos + i * 16 +36,topPos+48,14,14), color);
            addRenderableWidget(colorButton);
        }

        addRenderableWidget(checkbox);
        addRenderableWidget(actionBar);

        Checkbox limitToDimension = Checkbox.builder(Component.empty(),font)
                .pos(this.leftPos + 65, this.topPos + 120)
                .onValueChange((checkbox1, value) -> {
                    if (value) {
                        if (dimension == null) {
                            dimension = Level.OVERWORLD;
                        }
                    } else {
                        dimension = null;
                    }
                    dimensionButton.active = value;
                })
                .build();

        dimensionButton = Button.builder(Component.empty(),b -> {
            cycleDimension();
            b.setMessage(Component.literal(dimension.location().toString()));
        })
                .bounds(leftPos+92,topPos+120,140,16)
                .build();

        addRenderableWidget(limitToDimension);
        dimensionButton.active = false;
        addRenderableWidget(dimensionButton);
    }

    void cycleDimension() {
        List<ResourceKey<Level>> levels = Minecraft.getInstance().getConnection().levels().stream().sorted().toList();
        if (dimension == null) {
            dimension = Level.OVERWORLD;
        } else {
            int index = levels.indexOf(dimension);
            index++;
            if (index >= levels.size()) {
                dimension = Level.OVERWORLD;
            } else {
                dimension = levels.get(index);
            }
        }
    }

    void changeColor(ChatFormatting color) {
        this.color = color;
    }

    void decrSize() {
        if (size>1) {
            size--;
        }
    }

    void incSize() {
        if (size<9) {
            size++;
        }
    }

    private void onTextInput(String text) {

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        textBox.render(guiGraphics, mouseX, mouseY, partialTick);
        renderLabels(guiGraphics, mouseX, mouseY);
    }

    void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(font, Component.literal("Text:"), leftPos+6, topPos + 16, 0x404040, false);
        guiGraphics.drawString(font, Component.literal("Size:"), leftPos+6, topPos + 32, 0x404040, false);

        guiGraphics.drawString(font, Component.literal(size+""), leftPos+51, topPos + 33, 0x404040, false);

        guiGraphics.drawString(font, Component.literal("Color:"), leftPos+6, topPos + 50, 0x404040, false);

        guiGraphics.drawString(font, Component.literal("Time:"), leftPos+6, topPos + 66, 0x404040, false);

        guiGraphics.drawString(font, Component.literal("Shake:"), leftPos+6, topPos + 100, 0x404040, false);
        guiGraphics.drawString(font, Component.literal("Action Bar:"), leftPos+72, topPos + 100, 0x404040, false);
        guiGraphics.drawString(font, Component.literal("Dimension:"), leftPos+6, topPos + 124, 0x404040, false);

    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.blitSprite(BACKGROUND,leftPos,topPos,
                imageWidth,imageHeight);
    }

    public void sendAnnouncement() {
        int time = Integer.parseInt(timeBox.getValue());
        Services.PLATFORM.sendToServer(new C2SSendAnnouncementPacket(textBox.getValue(), color,size,shake,time,textPosition,Optional.ofNullable(dimension)));
        Minecraft.getInstance().setScreen(null);
    }
}

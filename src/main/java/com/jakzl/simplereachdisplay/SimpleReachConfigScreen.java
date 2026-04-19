package com.jakzl.simplereachdisplay;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SimpleReachConfigScreen extends Screen {

    private final Screen parent;
    private ModConfig config;

    // Layout constants
    private static final int LEFT_PANEL_WIDTH = 260;
    private static final int PREVIEW_MARGIN = 20;

    // Scroll
    private int scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 24;
    private static final int SECTION_HEIGHT = 30;

    // Drag state for HUD position preview
    private boolean draggingHud = false;
    private float dragOffsetX, dragOffsetY;

    // Category tabs
    private enum Category { GENERAL, APPEARANCE, COLORS, POSITION }
    private Category activeCategory = Category.GENERAL;

    private List<SettingEntry> entries = new ArrayList<>();

    public SimpleReachConfigScreen(Screen parent) {
        super(Text.literal("SimpleReachDisplay"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        buildEntries();
        buildTabs();
    }

    private void buildTabs() {
        int tabY = 10;
        int tabX = 10;
        int tabW = 60;
        for (Category cat : Category.values()) {
            final Category c = cat;
            addDrawableChild(ButtonWidget.builder(Text.literal(cat.name()), btn -> {
                activeCategory = c;
                scrollOffset = 0;
                buildEntries();
                clearAndInit();
            }).dimensions(tabX, tabY, tabW, 20).build());
            tabX += tabW + 4;
        }
    }

    private void buildEntries() {
        entries.clear();
        switch (activeCategory) {
            case GENERAL -> {
                entries.add(new ToggleEntry("Enabled", config.enabled, v -> config.enabled = v));
                entries.add(new EnumEntry<>("Target Filter", ModConfig.TargetFilter.values(), config.targetFilter, v -> config.targetFilter = v));
                entries.add(new SliderEntry("Display Duration", config.displayDuration, 0.5f, 5.0f, v -> config.displayDuration = v));
                entries.add(new ToggleEntry("Fade Out", config.fadeOut, v -> config.fadeOut = v));
                entries.add(new IntSliderEntry("Decimal Places", config.decimalPlaces, 0, 3, v -> config.decimalPlaces = v));
                entries.add(new ToggleEntry("Show 'blocks' suffix", config.showUnit, v -> config.showUnit = v));
            }
            case APPEARANCE -> {
                entries.add(new EnumEntry<>("Text Size", ModConfig.TextSize.values(), config.textSize, v -> config.textSize = v));
                entries.add(new ToggleEntry("Bold", config.bold, v -> config.bold = v));
                entries.add(new ToggleEntry("Italic", config.italic, v -> config.italic = v));
                entries.add(new ToggleEntry("Background Box", config.showBackground, v -> config.showBackground = v));
                entries.add(new SliderEntry("Background Opacity", config.backgroundOpacity, 0f, 1f, v -> config.backgroundOpacity = v));
                entries.add(new IntSliderEntry("Background Padding", config.backgroundPadding, 0, 10, v -> config.backgroundPadding = v));
            }
            case COLORS -> {
                entries.add(new ToggleEntry("Dynamic Color", config.dynamicColor, v -> config.dynamicColor = v));
                entries.add(new SectionLabel("--- Dynamic Colors ---"));
                entries.add(new SliderEntry("Green Min", config.greenMinDistance, 0f, 5f, v -> config.greenMinDistance = v));
                entries.add(new SliderEntry("Green Max", config.greenMaxDistance, 0f, 5f, v -> config.greenMaxDistance = v));
                entries.add(new SliderEntry("Orange Min", config.orangeMinDistance, 0f, 5f, v -> config.orangeMinDistance = v));
                entries.add(new SliderEntry("Orange Max", config.orangeMaxDistance, 0f, 5f, v -> config.orangeMaxDistance = v));
                entries.add(new ColorEntry("Green Color", config.greenColor, v -> config.greenColor = v));
                entries.add(new ColorEntry("Orange Color", config.orangeColor, v -> config.orangeColor = v));
                entries.add(new ColorEntry("Red Color", config.redColor, v -> config.redColor = v));
                entries.add(new SectionLabel("--- Static Color ---"));
                entries.add(new ColorEntry("Static Color", config.staticColor, v -> config.staticColor = v));
            }
            case POSITION -> {
                entries.add(new ToggleEntry("Default Position (under crosshair)", config.useDefaultPosition, v -> {
                    config.useDefaultPosition = v;
                    if (v) { config.hudX = -1; config.hudY = -1; }
                }));
                entries.add(new SectionLabel("Drag the preview label to reposition"));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background
        context.fill(0, 0, width, height, 0xFF1a1a2e);

        // Left panel background
        context.fill(0, 0, LEFT_PANEL_WIDTH, height, 0xFF16213e);
        context.fill(LEFT_PANEL_WIDTH, 0, LEFT_PANEL_WIDTH + 1, height, 0xFF0f3460);

        // Title
        context.drawTextWithShadow(textRenderer, Text.literal("SimpleReachDisplay"), 10, 36, 0xFFe94560);
        context.drawTextWithShadow(textRenderer, Text.literal("by JAKZL"), 10, 48, 0x99aaaaaa);

        // Divider under tabs
        context.fill(0, 32, LEFT_PANEL_WIDTH, 33, 0xFF0f3460);

        // Entries
        int entryX = 10;
        int entryY = 62 - scrollOffset;
        for (SettingEntry entry : entries) {
            if (entryY + ENTRY_HEIGHT > 40 && entryY < height - 10) {
                entry.render(context, textRenderer, entryX, entryY, mouseX, mouseY);
            }
            entryY += entry instanceof SectionLabel ? SECTION_HEIGHT : ENTRY_HEIGHT;
        }

        // Right preview panel
        renderPreviewPanel(context, mouseX, mouseY, delta);

        // Draw buttons/widgets on top
        super.render(context, mouseX, mouseY, delta);

        // Save hint
        context.drawTextWithShadow(textRenderer, Text.literal("Changes saved automatically"), LEFT_PANEL_WIDTH + 10, height - 20, 0x55ffffff);
    }

    private void renderPreviewPanel(DrawContext context, int mouseX, int mouseY, float delta) {
        int px = LEFT_PANEL_WIDTH + PREVIEW_MARGIN;
        int py = 40;
        int pw = width - LEFT_PANEL_WIDTH - PREVIEW_MARGIN * 2;
        int ph = height - py - PREVIEW_MARGIN;

        // Panel bg
        context.fill(px, py, px + pw, py + ph, 0xFF0d0d1a);
        // Border
        context.fill(px, py, px + pw, py + 1, 0xFF0f3460);
        context.fill(px, py + ph - 1, px + pw, py + ph, 0xFF0f3460);
        context.fill(px, py, px + 1, py + ph, 0xFF0f3460);
        context.fill(px + pw - 1, py, px + pw, py + ph, 0xFF0f3460);

        context.drawTextWithShadow(textRenderer, Text.literal("PREVIEW"), px + pw / 2 - textRenderer.getWidth("PREVIEW") / 2, py + 6, 0x55e94560);

        // Crosshair simulation
        int cx = px + pw / 2;
        int cy = py + ph / 2;
        context.fill(cx - 5, cy, cx + 5, cy + 1, 0xAAFFFFFF);
        context.fill(cx, cy - 5, cx + 1, cy + 5, 0xAAFFFFFF);

        // Simulated reach text
        String previewText = getPreviewText();
        int textColor = getPreviewColor();

        float scale = switch (config.textSize) {
            case SMALL -> 0.75f;
            case MEDIUM -> 1.0f;
            case LARGE -> 1.5f;
        };

        int tw = (int)(textRenderer.getWidth(previewText) * scale);
        int th = (int)(textRenderer.fontHeight * scale);

        float tx, ty;
        if (config.useDefaultPosition || config.hudX < 0) {
            // Under crosshair in preview space
            tx = cx - tw / 2f;
            ty = cy + 14f;
        } else {
            // Map from screen coords to preview coords
            float relX = config.hudX / (float)this.width;
            float relY = config.hudY / (float)this.height;
            tx = px + relX * pw;
            ty = py + relY * ph;
        }

        if (activeCategory == Category.POSITION && !config.useDefaultPosition) {
            // Draggable in position tab
            boolean hoveringLabel = mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th;
            if (draggingHud) {
                tx = mouseX - dragOffsetX;
                ty = mouseY - dragOffsetY;
                // Map back to screen coords
                config.hudX = ((tx - px) / pw) * this.width;
                config.hudY = ((ty - py) / ph) * this.height;
            }
            // Highlight when hovering
            if (hoveringLabel || draggingHud) {
                context.fill((int)tx - 2, (int)ty - 2, (int)tx + tw + 2, (int)ty + th + 2, 0x33e94560);
            }
            context.drawTextWithShadow(textRenderer, Text.literal("↖ drag me"), (int)tx, (int)ty - 10, 0x55ffffff);
        }

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);

        int sx = (int)(tx / scale);
        int sy = (int)(ty / scale);

        if (config.showBackground) {
            int pad = config.backgroundPadding;
            int bgAlpha = (int)(config.backgroundOpacity * 255);
            context.fill(sx - pad, sy - pad, sx + (int)(tw / scale) + pad, sy + (int)(th / scale) + pad, (bgAlpha << 24));
        }

        context.drawTextWithShadow(textRenderer,
                net.minecraft.text.Text.literal(previewText).styled(s -> s.withBold(config.bold).withItalic(config.italic)),
                sx, sy, textColor | 0xFF000000);

        context.getMatrices().pop();
    }

    private String getPreviewText() {
        String fmt = "%." + config.decimalPlaces + "f";
        String num = String.format(fmt, 2.94);
        if (config.showUnit) return num + " blocks";
        return num;
    }

    private int getPreviewColor() {
        if (!config.dynamicColor) return config.staticColor;
        double dist = 2.94;
        if (dist >= config.greenMinDistance && dist <= config.greenMaxDistance) return config.greenColor;
        else if (dist >= config.orangeMinDistance && dist < config.greenMinDistance) return config.orangeColor;
        else return config.redColor;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle entry clicks
        int entryX = 10;
        int entryY = 62 - scrollOffset;
        for (SettingEntry entry : entries) {
            int h = entry instanceof SectionLabel ? SECTION_HEIGHT : ENTRY_HEIGHT;
            if (mouseX >= entryX && mouseX <= LEFT_PANEL_WIDTH - 10 && mouseY >= entryY && mouseY <= entryY + h) {
                entry.onClick((int)mouseX, (int)mouseY);
                saveConfig();
                return true;
            }
            entryY += h;
        }

        // Drag handle for HUD
        if (activeCategory == Category.POSITION && !config.useDefaultPosition) {
            // Check if clicking on preview text
            int px = LEFT_PANEL_WIDTH + PREVIEW_MARGIN;
            int py = 40;
            int pw = width - LEFT_PANEL_WIDTH - PREVIEW_MARGIN * 2;
            int ph = height - py - PREVIEW_MARGIN;

            String previewText = getPreviewText();
            float scale = switch (config.textSize) { case SMALL -> 0.75f; case MEDIUM -> 1.0f; case LARGE -> 1.5f; };
            int tw = (int)(textRenderer.getWidth(previewText) * scale);
            int th = (int)(textRenderer.fontHeight * scale);

            float tx = px + (config.hudX / (float)this.width) * pw;
            float ty = py + (config.hudY / (float)this.height) * ph;

            if (mouseX >= tx && mouseX <= tx + tw && mouseY >= ty && mouseY <= ty + th) {
                draggingHud = true;
                dragOffsetX = (float)(mouseX - tx);
                dragOffsetY = (float)(mouseY - ty);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingHud) {
            draggingHud = false;
            saveConfig();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX < LEFT_PANEL_WIDTH) {
            scrollOffset -= (int)(verticalAmount * 10);
            scrollOffset = Math.max(0, scrollOffset);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        saveConfig();
        MinecraftClient.getInstance().setScreen(parent);
    }

    private void saveConfig() {
        try {
            AutoConfig.getConfigHolder(ModConfig.class).save();
        } catch (Exception e) {
            // ignore
        }
    }

    // ---- Setting Entry Types ----

    abstract static class SettingEntry {
        String label;
        SettingEntry(String label) { this.label = label; }
        abstract void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my);
        void onClick(int mx, int my) {}
    }

    static class SectionLabel extends SettingEntry {
        SectionLabel(String label) { super(label); }
        @Override
        public void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my) {
            ctx.drawTextWithShadow(tr, Text.literal(label), x, y + 10, 0x99e94560);
        }
    }

    static class ToggleEntry extends SettingEntry {
        boolean value;
        java.util.function.Consumer<Boolean> setter;
        ToggleEntry(String label, boolean value, java.util.function.Consumer<Boolean> setter) {
            super(label);
            this.value = value;
            this.setter = setter;
        }
        @Override
        public void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my) {
            ctx.drawTextWithShadow(tr, Text.literal(label), x, y + 7, 0xFFCCCCCC);
            String val = value ? "§aON" : "§cOFF";
            ctx.drawTextWithShadow(tr, Text.literal(val), x + 200, y + 7, 0xFFFFFFFF);
        }
        @Override
        public void onClick(int mx, int my) {
            value = !value;
            setter.accept(value);
        }
    }

    static class SliderEntry extends SettingEntry {
        float value, min, max;
        java.util.function.Consumer<Float> setter;
        SliderEntry(String label, float value, float min, float max, java.util.function.Consumer<Float> setter) {
            super(label);
            this.value = value;
            this.min = min;
            this.max = max;
            this.setter = setter;
        }
        @Override
        public void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my) {
            ctx.drawTextWithShadow(tr, Text.literal(label), x, y + 7, 0xFFCCCCCC);
            String val = String.format("%.2f", value);
            ctx.drawTextWithShadow(tr, Text.literal(val), x + 200, y + 7, 0xFFe94560);
        }
    }

    static class IntSliderEntry extends SettingEntry {
        int value, min, max;
        java.util.function.Consumer<Integer> setter;
        IntSliderEntry(String label, int value, int min, int max, java.util.function.Consumer<Integer> setter) {
            super(label);
            this.value = value;
            this.min = min;
            this.max = max;
            this.setter = setter;
        }
        @Override
        public void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my) {
            ctx.drawTextWithShadow(tr, Text.literal(label), x, y + 7, 0xFFCCCCCC);
            ctx.drawTextWithShadow(tr, Text.literal(String.valueOf(value)), x + 200, y + 7, 0xFFe94560);
        }
        @Override
        public void onClick(int mx, int my) {
            value = (value < max) ? value + 1 : min;
            setter.accept(value);
        }
    }

    static class EnumEntry<T extends Enum<T>> extends SettingEntry {
        T[] values;
        T current;
        java.util.function.Consumer<T> setter;
        EnumEntry(String label, T[] values, T current, java.util.function.Consumer<T> setter) {
            super(label);
            this.values = values;
            this.current = current;
            this.setter = setter;
        }
        @Override
        public void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my) {
            ctx.drawTextWithShadow(tr, Text.literal(label), x, y + 7, 0xFFCCCCCC);
            String name = current.name().replace("_", " ");
            ctx.drawTextWithShadow(tr, Text.literal("< " + name + " >"), x + 100, y + 7, 0xFFe94560);
        }
        @Override
        public void onClick(int mx, int my) {
            int idx = (current.ordinal() + 1) % values.length;
            current = values[idx];
            setter.accept(current);
        }
    }

    static class ColorEntry extends SettingEntry {
        int color;
        java.util.function.Consumer<Integer> setter;
        ColorEntry(String label, int color, java.util.function.Consumer<Integer> setter) {
            super(label);
            this.color = color;
            this.setter = setter;
        }
        @Override
        public void render(DrawContext ctx, TextRenderer tr, int x, int y, int mx, int my) {
            ctx.drawTextWithShadow(tr, Text.literal(label), x, y + 7, 0xFFCCCCCC);
            // Color swatch
            ctx.fill(x + 180, y + 4, x + 200, y + 18, 0xFF000000 | color);
            ctx.drawTextWithShadow(tr, Text.literal(String.format("#%06X", color)), x + 205, y + 7, 0xFF888888);
        }
    }
}

package com.jakzl.simplereachdisplay;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(HudRenderer::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        ModConfig config;
        try {
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception e) {
            return;
        }

        if (!ReachDisplayState.isActive(config)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        float opacity = ReachDisplayState.getOpacity(config);
        if (opacity <= 0f) return;

        String text = formatDistance(ReachDisplayState.lastHitDistance, config);
        TextRenderer textRenderer = client.textRenderer;

        int textWidth = textRenderer.getWidth(text);
        int textHeight = textRenderer.fontHeight;

        int scaledSize = switch (config.textSize) {
            case SMALL -> 1;
            case MEDIUM -> 1; // handled via scale below
            case LARGE -> 1;
        };

        float scale = switch (config.textSize) {
            case SMALL -> 0.75f;
            case MEDIUM -> 1.0f;
            case LARGE -> 1.5f;
        };

        int screenW = context.getScaledWindowWidth();
        int screenH = context.getScaledWindowHeight();

        float x, y;
        if (config.useDefaultPosition || config.hudX < 0) {
            // Default: centered just below crosshair
            x = (screenW / 2f) - (textWidth * scale / 2f);
            y = (screenH / 2f) + 12f;
        } else {
            x = config.hudX;
            y = config.hudY;
        }

        int color = getColor(ReachDisplayState.lastHitDistance, config);
        int alpha = (int)(opacity * 255);
        alpha = Math.max(0, Math.min(255, alpha));

        // Blend alpha into color
        int finalColor = (alpha << 24) | (color & 0x00FFFFFF);

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);

        int sx = (int)(x / scale);
        int sy = (int)(y / scale);

        if (config.showBackground) {
            int pad = config.backgroundPadding;
            int bgAlpha = (int)(config.backgroundOpacity * alpha);
            bgAlpha = Math.max(0, Math.min(255, bgAlpha));
            int bgColor = (bgAlpha << 24) | 0x000000;
            context.fill(sx - pad, sy - pad, sx + textWidth + pad, sy + textHeight + pad, bgColor);
        }

        int style = 0;
        if (config.bold) style |= net.minecraft.util.Formatting.BOLD.getCode();

        // Draw text (with or without italic/bold we use shadow for visibility)
        if (config.italic) {
            context.drawTextWithShadow(textRenderer,
                    net.minecraft.text.Text.literal(text).styled(s -> s.withItalic(true).withBold(config.bold)),
                    sx, sy, finalColor);
        } else {
            context.drawTextWithShadow(textRenderer,
                    net.minecraft.text.Text.literal(text).styled(s -> s.withBold(config.bold)),
                    sx, sy, finalColor);
        }

        context.getMatrices().pop();
    }

    private static String formatDistance(double distance, ModConfig config) {
        String format = "%." + config.decimalPlaces + "f";
        String num = String.format(format, distance);
        if (config.showUnit) return num + " blocks";
        return num;
    }

    private static int getColor(double distance, ModConfig config) {
        if (!config.dynamicColor) return config.staticColor;

        if (distance >= config.greenMinDistance && distance <= config.greenMaxDistance) {
            return config.greenColor;
        } else if (distance >= config.orangeMinDistance && distance < config.greenMinDistance) {
            return config.orangeColor;
        } else {
            return config.redColor;
        }
    }
}

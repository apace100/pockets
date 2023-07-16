package io.github.apace100.pockets.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.pockets.PocketUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    private void renderPocketBar(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if(!PocketUtil.hasPockets(stack)) {
            return;
        }
        int occupancy = PocketUtil.getPocketOccupancy(stack);
        if(occupancy <= 0) {
            return;
        }
        int yOffset = 0;
        if(stack.isItemBarVisible()) {
            yOffset = -2;
        }

        RenderSystem.disableDepthTest();
        int i = Math.min(1 + 12 * PocketUtil.getPocketOccupancy(stack) / 64, 13);
        int j = MathHelper.packRgb(0.4f, 0.4f, 1.0f);
        int drawX = x + 2;
        int drawY = y + 13 + yOffset;
        this.fill(RenderLayer.getGuiOverlay(), drawX, drawY, drawX + 13, drawY + 2, -16777216);
        this.fill(RenderLayer.getGuiOverlay(), drawX, drawY, drawX + i, drawY + 1, j | -16777216);
        RenderSystem.enableDepthTest();
    }
}

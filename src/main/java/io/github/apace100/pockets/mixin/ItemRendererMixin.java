package io.github.apace100.pockets.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.pockets.PocketUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    private void renderPocketBar(MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
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
        DrawableHelper.fill(matrices, drawX, drawY, drawX + 13, drawY + 2, -16777216);
        DrawableHelper.fill(matrices, drawX, drawY, drawX + i, drawY + 1, j | -16777216);
        RenderSystem.enableDepthTest();
    }
}

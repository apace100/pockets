package io.github.apace100.pockets.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.pockets.PocketUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    private void renderPocketBar(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
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
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder immediate2 = tessellator.getBuffer();
        int i = Math.min(1 + 12 * PocketUtil.getPocketOccupancy(stack) / 64, 13);
        int j = MathHelper.packRgb(0.4f, 0.4f, 1.0f);
        this.renderGuiQuad(immediate2, x + 2, y + 13 + yOffset, 13, 2, 0, 0, 0, 255);
        this.renderGuiQuad(immediate2, x + 2, y + 13 + yOffset, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }
}

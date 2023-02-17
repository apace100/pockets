package io.github.apace100.pockets.mixin;

import io.github.apace100.pockets.PocketUtil;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @Inject(method = "getItemOccupancy", at = @At("HEAD"), cancellable = true)
    private static void addPocketOccupancy(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(PocketUtil.hasPockets(stack)) {
            cir.setReturnValue((64 / stack.getMaxCount()) + PocketUtil.getPocketOccupancy(stack));
        }
    }
}

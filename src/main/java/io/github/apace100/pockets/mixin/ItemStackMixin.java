package io.github.apace100.pockets.mixin;

import io.github.apace100.pockets.PocketUtil;
import io.github.apace100.pockets.SoundUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private void dropContainedStacks(int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if(PocketUtil.hasPockets(stack) && entity instanceof PlayerEntity player) {
            PocketUtil.dropAllPocketedItems(stack, player);
            SoundUtil.playDropContentsSound(player);
        }
    }
}

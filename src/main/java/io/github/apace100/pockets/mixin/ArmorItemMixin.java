package io.github.apace100.pockets.mixin;

import io.github.apace100.pockets.PocketUtil;
import io.github.apace100.pockets.SoundUtil;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin extends Item {

    public ArmorItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        boolean pocketable = PocketUtil.hasPockets(stack) && PocketUtil.isAllowedInPockets(otherStack);
        if (otherStack.isEmpty()) {
            PocketUtil.removeFirstStack(stack).ifPresent(itemStack -> {
                SoundUtil.playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
            });
        } else if(pocketable) {
            int i = PocketUtil.addToPockets(stack, otherStack);
            if (i > 0) {
                SoundUtil.playInsertSound(player);
                otherStack.decrement(i);
            }
        }
        return pocketable;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if(!PocketUtil.hasPockets(stack)) {
            return Optional.empty();
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        PocketUtil.getPocketedStacks(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, PocketUtil.getPocketOccupancy(stack)));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)  {
        if(!PocketUtil.hasPockets(stack)) {
            return;
        }
        tooltip.add(Text.translatable("item.minecraft.bundle.fullness", PocketUtil.getPocketOccupancy(stack), 64).formatted(Formatting.GRAY));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        SoundUtil.playDropContentsSound(entity);
        ItemUsage.spawnItemContents(entity, PocketUtil.getPocketedStacks(entity.getStack()));
    }
}

package io.github.apace100.pockets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class PocketUtil {

    public static boolean isAllowedInPockets(ItemStack stack) {
        return !stack.isIn(Pockets.BLACKLIST_TAG);
    }

    public static boolean hasPockets(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && stack.isIn(Pockets.HAS_POCKET_TAG);
    }

    public static int addToPockets(ItemStack pocketItem, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canBeNested()) {
            NbtCompound nbtCompound = pocketItem.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }

            int i = getPocketOccupancy(pocketItem);
            int j = getItemOccupancy(stack);
            int k = Math.min(stack.getCount(), (64 - i) / j);
            if (k == 0) {
                return 0;
            } else {
                NbtList nbtList = nbtCompound.getList("Items", 10);
                Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
                if (optional.isPresent()) {
                    NbtCompound nbtCompound2 = optional.get();
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    itemStack.increment(k);
                    itemStack.writeNbt(nbtCompound2);
                    nbtList.remove(nbtCompound2);
                    nbtList.add(0, nbtCompound2);
                } else {
                    ItemStack nbtCompound2 = stack.copy();
                    nbtCompound2.setCount(k);
                    NbtCompound itemStack = new NbtCompound();
                    nbtCompound2.writeNbt(itemStack);
                    nbtList.add(0, itemStack);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    public static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        } else {
            Stream<NbtElement> itemStream = items.stream();
            return itemStream
                .filter(elem -> elem instanceof NbtCompound)
                .map(nbt -> (NbtCompound)nbt).filter(nbtCompound -> {
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
                return ItemStack.canCombine(itemStack, stack);
            }).findFirst();
        }
    }

    private static int getItemOccupancy(ItemStack stack) {
        if(PocketUtil.hasPockets(stack)) {
            return (64 / stack.getMaxCount()) + PocketUtil.getPocketOccupancy(stack);
        }
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + getPocketOccupancy(stack);
        } else {
            if ((stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt()) {
                NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
                if (nbtCompound != null && !nbtCompound.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / stack.getMaxCount();
        }
    }

    public static int getPocketOccupancy(ItemStack stack) {
        return getPocketedStacks(stack).mapToInt((itemStack) -> {
            return getItemOccupancy(itemStack) * itemStack.getCount();
        }).sum();
    }

    public static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                nbtList.remove(0);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }

    public static boolean dropAllPocketedItems(ItemStack stack, PlayerEntity player) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                NbtList nbtList = nbtCompound.getList("Items", 10);

                for(int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound nbtCompound2 = nbtList.getCompound(i);
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    player.dropItem(itemStack, true, true);
                }
            }

            stack.removeSubNbt("Items");
            return true;
        }
    }

    public static Stream<ItemStack> getPocketedStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            Stream<NbtElement> var10000 = nbtList.stream();
            Objects.requireNonNull(NbtCompound.class);
            return var10000.map(elem -> (NbtCompound)elem).map(ItemStack::fromNbt);
        }
    }
}

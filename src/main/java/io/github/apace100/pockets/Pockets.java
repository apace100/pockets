package io.github.apace100.pockets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Pockets implements ModInitializer {

	public static final TagKey<Item> HAS_POCKET_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier("pockets", "has_pockets"));
	public static final TagKey<Item> BLACKLIST_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier("pockets", "pocket_blacklist"));

	@Override
	public void onInitialize() {

	}
}

package io.github.apace100.pockets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Pockets implements ModInitializer {

	public static final TagKey<Item> HAS_POCKET_TAG = TagKey.of(RegistryKeys.ITEM, new Identifier("pockets", "has_pockets"));
	public static final TagKey<Item> BLACKLIST_TAG = TagKey.of(RegistryKeys.ITEM, new Identifier("pockets", "pocket_blacklist"));

	@Override
	public void onInitialize() {

	}
}

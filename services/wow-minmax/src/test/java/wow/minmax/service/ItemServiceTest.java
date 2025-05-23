package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class ItemServiceTest extends ServiceTest {
	@Autowired
	@Qualifier("itemServiceImpl")
	ItemService underTest;

	@Test
	void getItemsBySlot() {
		List<Item> itemsBySlot = underTest.getItemsBySlot(character, ItemSlot.TRINKET_1, ItemFilter.everything()).stream()
				.sorted(Comparator.comparing(Item::getName))
				.toList();

		List<String> names = itemsBySlot.stream().map(Item::getName).toList();

		assertThat(names).contains(
				"Shifting Naaru Sliver",
				"The Skull of Gul'dan"
		);
	}

	@Test
	void getHandEnchants() {
		List<Enchant> enchants = underTest.getEnchants(character, ItemType.HANDS, ArmorSubType.CLOTH).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.toList();

		List<String> names = enchants.stream().map(Enchant::getName).toList();

		assertThat(names).hasSameElementsAs(List.of(
				"Enchant Gloves - Blasting",
				"Enchant Gloves - Fire Power",
				"Enchant Gloves - Frost Power",
				"Enchant Gloves - Major Spellpower",
				"Enchant Gloves - Shadow Power",
				"Enchant Gloves - Spell Strike"
		));
	}

	@Test
	void getChestEnchants() {
		List<Enchant> enchants = underTest.getEnchants(character, ItemType.CHEST, ArmorSubType.CLOTH).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.toList();

		List<String> names = enchants.stream().map(Enchant::getName).toList();

		assertThat(names).hasSameElementsAs(List.of(
				"Enchant Chest - Exceptional Stats",
				"Enchant Chest - Greater Stats",
				"Enchant Chest - Lesser Stats",
				"Enchant Chest - Minor Stats",
				"Enchant Chest - Stats"
		));
	}

	@Test
	void getFeetEnchants() {
		List<Enchant> enchants = underTest.getEnchants(character, ItemType.FEET, ArmorSubType.CLOTH).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.toList();

		List<String> names = enchants.stream().map(Enchant::getName).toList();

		assertThat(names).hasSameElementsAs(List.of(
				"Enchant Boots - Boar's Speed",
				"Enchant Boots - Minor Speed"
		));
	}

	@Test
	void getGems() {
		List<Gem> metaGems = underTest.getGems(character, SocketType.META, false);
		List<Gem> coloredGems = underTest.getGems(character, SocketType.YELLOW, false);

		List<String> metaGemNames = metaGems.stream().map(Gem::getName).toList();

		assertThat(metaGemNames).hasSameElementsAs(List.of(
				"Destructive Skyfire Diamond",
				"Mystical Skyfire Diamond",
				"Chaotic Skyfire Diamond",
				"Ember Skyfire Diamond",
				"Imbued Unstable Diamond"
		));

		List<String> coloredGemNames = coloredGems.stream().map(Gem::getName).toList();

		assertThat(coloredGemNames).hasSameElementsAs(List.of(
				"Runed Blood Garnet",
				"Potent Flame Spessarite",
				"Radiant Deep Peridot",
				"Glowing Shadow Draenite",
				"Gleaming Golden Draenite",
				"Runed Living Ruby",
				"Gleaming Dawnstone",
				"Glowing Nightseye",
				"Potent Noble Topaz",
				"Radiant Talasite",
				"Great Golden Draenite",
				"Great Dawnstone",
				"Veiled Flame Spessarite",
				"Veiled Noble Topaz",
				"Runed Crimson Spinel",
				"Gleaming Lionseye",
				"Great Lionseye",
				"Glowing Shadowsong Amethyst",
				"Potent Pyrestone",
				"Veiled Pyrestone",
				"Radiant Seaspray Emerald",
				"Quick Dawnstone",
				"Reckless Noble Topaz",
				"Forceful Talasite",
				"Forceful Seaspray Emerald",
				"Reckless Pyrestone",
				"Quick Lionseye"
		));
	}

	@Test
	void testGetGems() {
		List<Gem> coloredGems = underTest.getGems(character, getItem("Sunfire Robe").getSocketType(1));

		List<String> coloredGemNames = coloredGems.stream().map(Gem::getName).toList();

		assertThat(coloredGemNames).hasSameElementsAs(List.of(
				"Runed Blood Garnet",
				"Potent Flame Spessarite",
				"Radiant Deep Peridot",
				"Glowing Shadow Draenite",
				"Gleaming Golden Draenite",
				"Runed Living Ruby",
				"Gleaming Dawnstone",
				"Glowing Nightseye",
				"Potent Noble Topaz",
				"Radiant Talasite",
				"Stark Blood Garnet",
				"Notched Deep Peridot",
				"Runed Ornate Ruby",
				"Gleaming Ornate Dawnstone",
				"Potent Ornate Topaz",
				"Polished Chrysoprase",
				"Infused Fire Opal",
				"Glowing Tanzanite",
				"Rune Covered Chrysoprase",
				"Shining Fire Opal",
				"Mysterious Fire Opal",
				"Potent Fire Opal",
				"Fluorescent Tanzanite",
				"Vivid Chrysoprase",
				"Lambent Chrysoprase",
				"Radiant Chrysoprase",
				"Infused Amethyst",
				"Great Golden Draenite",
				"Great Dawnstone",
				"Veiled Flame Spessarite",
				"Veiled Noble Topaz",
				"Runed Crimson Spinel",
				"Gleaming Lionseye",
				"Great Lionseye",
				"Glowing Shadowsong Amethyst",
				"Potent Pyrestone",
				"Veiled Pyrestone",
				"Radiant Seaspray Emerald",
				"Unstable Topaz",
				"Unstable Talasite",
				"Quick Dawnstone",
				"Reckless Noble Topaz",
				"Forceful Talasite",
				"Runed Crimson Spinel",
				"Forceful Seaspray Emerald",
				"Reckless Pyrestone",
				"Quick Lionseye"
		));
	}
}
package wow.character.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.GearSet;
import wow.commons.model.categorization.ItemSlot;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.*;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-04-09
 */
class GearSetRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	GearSetRepository gearSetRepository;

	@Test
	void getGearSet() {
		var character = getCharacter(PRIEST, UNDEAD, 70, TBC_P5);
		var gearSet = gearSetRepository.getGearSet("Wowhead TBC P5 BiS", character).orElseThrow();

		assertSlot(gearSet, HEAD, "Dark Conjuror's Collar", "Glyph of Power", "Mystical Skyfire Diamond", "Glowing Shadowsong Amethyst");
		assertSlot(gearSet, NECK, "Amulet of Unfettered Magics");
		assertSlot(gearSet, SHOULDER, "Shoulderpads of Absolution", "Greater Inscription of Discipline", "Runed Crimson Spinel", "Runed Crimson Spinel");
		assertSlot(gearSet, BACK, "Tattered Cape of Antonidas", "Enchant Cloak - Subtlety", "Runed Crimson Spinel");
		assertSlot(gearSet, CHEST, "Sunfire Robe", "Enchant Chest - Exceptional Stats", "Runed Crimson Spinel", "Runed Crimson Spinel", "Runed Crimson Spinel");
		assertSlot(gearSet, WRIST, "Bracers of Absolution", "Enchant Bracer - Spellpower", "Runed Crimson Spinel");
		assertSlot(gearSet, HANDS, "Handguards of Defiled Worlds", "Enchant Gloves - Major Spellpower", "Runed Crimson Spinel", "Runed Crimson Spinel");
		assertSlot(gearSet, WAIST, "Cord of Absolution", null, "Runed Crimson Spinel");
		assertSlot(gearSet, LEGS, "Pantaloons of Growing Strife", "Runic Spellthread", "Runed Crimson Spinel", "Runed Crimson Spinel", "Runed Crimson Spinel");
		assertSlot(gearSet, FEET, "Treads of Absolution", "Enchant Boots - Boar's Speed", "Runed Crimson Spinel");
		assertSlot(gearSet, FINGER_1, "Ring of Omnipotence", "Enchant Ring - Spellpower");
		assertSlot(gearSet, FINGER_2, "Ring of Ancient Knowledge", "Enchant Ring - Spellpower");
		assertSlot(gearSet, TRINKET_1, "Hex Shrunken Head");
		assertSlot(gearSet, TRINKET_2, "Shifting Naaru Sliver");
		assertSlot(gearSet, MAIN_HAND, "Sunflare", "Enchant Weapon - Soulfrost");
		assertSlot(gearSet, OFF_HAND, "Heart of the Pit");
		assertSlot(gearSet, RANGED, "Wand of the Demonsoul", null, "Runed Crimson Spinel");
	}

	private void assertSlot(GearSet gearSet, ItemSlot itemSlot, String itemName) {
		assertSlot(gearSet, itemSlot, itemName, null);
	}

	private void assertSlot(GearSet gearSet, ItemSlot itemSlot, String expectedItemName, String expectedEnchantName, String... expectedGemNames) {
		var item = gearSet.getItem(itemSlot);

		assertThat(item).isNotNull();
		assertThat(item.getName()).isEqualTo(expectedItemName);

		var enchant = item.getEnchant();
		var enchantName = enchant != null ? enchant.getName() : null;

		assertThat(enchantName).isEqualTo(expectedEnchantName);

		var gems = item.getGems();
		var gemNames = gems.stream()
				.map(x -> x != null ? x.getName() : null)
				.toList();

		assertThat(gemNames).isEqualTo(List.of(expectedGemNames));
	}
}
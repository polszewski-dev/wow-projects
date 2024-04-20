package wow.character.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.GameVersionId;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.GameVersionId.VANILLA;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class ItemServiceTest extends WowCharacterSpringTest {
	@Autowired
	@Qualifier("itemServiceImpl")
	ItemService underTest;

	@Test
	void getItem() {
		Item item = underTest.getItem(34364, character.getPhaseId());
		assertThat(item.getId()).isEqualTo(34364);
	}

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
		List<Gem> metaGems = underTest.getGems(character, SocketType.META, true);
		List<Gem> coloredGems = underTest.getGems(character, SocketType.YELLOW, true);

		List<String> metaGemNames = metaGems.stream().map(Gem::getName).toList();

		assertThat(metaGemNames).hasSameElementsAs(List.of(
				"Destructive Skyfire Diamond",
				"Mystical Skyfire Diamond",
				"Chaotic Skyfire Diamond",
				"Ember Skyfire Diamond"
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
		List<Gem> coloredGems = underTest.getGems(character, getItem("Sunfire Robe").getSocketType(1), false);

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

	@Test
	void getGemCombos() {
		List<Gem[]> gemCombos = underTest.getBestGemCombos(character, getItem("Bracers of the Malefic").getItem());

		List<String> names = gemCombos.stream().map(x -> Stream.of(x).map(Gem::getName).collect(Collectors.joining(","))).toList();

		assertThat(names).hasSameElementsAs(List.of(
				"Quick Lionseye",
				"Gleaming Lionseye",
				"Runed Crimson Spinel",
				"Great Lionseye",
				"Veiled Pyrestone",
				"Radiant Seaspray Emerald",
				"Forceful Seaspray Emerald",
				"Glowing Shadowsong Amethyst",
				"Potent Pyrestone",
				"Reckless Pyrestone"
		));
	}

	@Test
	void getGemCombos2() {
		List<Gem[]> gemCombos = underTest.getBestGemCombos(character, getItem("Dark Conjuror's Collar").getItem());

		List<String> names = gemCombos.stream().map(x -> Stream.of(x).map(Gem::getName).collect(Collectors.joining(","))).toList();

		assertThat(names).hasSize(40);
	}

	@Test
	void getGemCombos3() {
		List<Gem[]> gemCombos = underTest.getBestGemCombos(character, getItem("Sunfire Robe").getItem());

		List<String> names = gemCombos.stream().map(x -> Stream.of(x).map(Gem::getName).collect(Collectors.joining(","))).toList();

		assertThat(names).hasSize(220);
	}

	@DisplayName("getBestEnchants")
	@Nested
	class BestEnchants {
		@Nested
		class Warlock {
			@DisplayName("Vanilla")
			@ParameterizedTest(name = "{0}")
			@MethodSource("wow.character.service.ItemServiceTest#getBestEnchantsWarlockVanilla")
			void vanilla(ItemType itemType, ItemSubType itemSubType, ExclusiveFaction exclusiveFaction, List<String> expectedResult) {
				assertBestEnchants(WARLOCK, VANILLA, exclusiveFaction, itemType, itemSubType, expectedResult);
			}

			@DisplayName("TBC")
			@ParameterizedTest(name = "{0} {2}")
			@MethodSource("wow.character.service.ItemServiceTest#getBestEnchantsWarlockTBC")
			void tbc(ItemType itemType, ItemSubType itemSubType, ExclusiveFaction exclusiveFaction, List<String> expectedResult) {
				assertBestEnchants(WARLOCK, TBC, exclusiveFaction, itemType, itemSubType, expectedResult);
			}
		}

		@Nested
		class Priest {
			@DisplayName("Vanilla")
			@ParameterizedTest(name = "{0}")
			@MethodSource("wow.character.service.ItemServiceTest#getBestEnchantsPriestVanilla")
			void vanilla(ItemType itemType, ItemSubType itemSubType, ExclusiveFaction exclusiveFaction, List<String> expectedResult) {
				assertBestEnchants(PRIEST, VANILLA, exclusiveFaction, itemType, itemSubType, expectedResult);
			}

			@DisplayName("TBC")
			@ParameterizedTest(name = "{0} {2}")
			@MethodSource("wow.character.service.ItemServiceTest#getBestEnchantsPriestTBC")
			void tbc(ItemType itemType, ItemSubType itemSubType, ExclusiveFaction exclusiveFaction, List<String> expectedResult) {
				assertBestEnchants(PRIEST, TBC, exclusiveFaction, itemType, itemSubType, expectedResult);
			}
		}
	}

	static Stream<Arguments> getBestEnchantsWarlockVanilla() {
		return getBestEnchantsDataVanilla(WARLOCK);
	}

	static Stream<Arguments> getBestEnchantsWarlockTBC() {
		return getBestEnchantsDataTBC(WARLOCK);
	}

	static Stream<Arguments> getBestEnchantsPriestVanilla() {
		return getBestEnchantsDataVanilla(PRIEST);
	}

	static Stream<Arguments> getBestEnchantsPriestTBC() {
		return getBestEnchantsDataTBC(PRIEST);
	}

	static Stream<Arguments> getBestEnchantsDataVanilla(CharacterClassId characterClassId) {
		var headEnchants = switch (characterClassId) {
			case WARLOCK -> List.of(
					"Hoodoo Hex"
			);
			case PRIEST -> List.of(
					"Arcanum of Focus"
			);
			default -> throw new IllegalArgumentException();
		};
		var legEnchants = switch (characterClassId) {
			case WARLOCK -> List.of(
					"Hoodoo Hex"
			);
			case PRIEST -> List.of(
					"Arcanum of Focus"
			);
			default -> throw new IllegalArgumentException();
		};
		return Stream.of(
				Arguments.of(ItemType.HEAD, ArmorSubType.CLOTH, null, headEnchants),
				Arguments.of(ItemType.NECK, null, null, List.of(

				)),
				Arguments.of(ItemType.SHOULDER, ArmorSubType.CLOTH, null, List.of(
						"Zandalar Signet of Mojo",
						"Power of the Scourge"
				)),
				Arguments.of(ItemType.BACK, ArmorSubType.CLOTH, null, List.of(
						"Enchant Cloak - Subtlety"
				)),
				Arguments.of(ItemType.CHEST, ArmorSubType.CLOTH, null, List.of(
						"Enchant Chest - Greater Stats"
				)),
				Arguments.of(ItemType.WRIST, ArmorSubType.CLOTH, null, List.of(
						"Enchant Bracer - Greater Intellect"
				)),
				Arguments.of(ItemType.HANDS, ArmorSubType.CLOTH, null, List.of(
						"Enchant Gloves - Fire Power",
						"Enchant Gloves - Frost Power",
						"Enchant Gloves - Shadow Power"
				)),
				Arguments.of(ItemType.WAIST, ArmorSubType.CLOTH, null, List.of()),
				Arguments.of(ItemType.LEGS, ArmorSubType.CLOTH, null, legEnchants),
				Arguments.of(ItemType.FEET, ArmorSubType.CLOTH, null, List.of(
						"Enchant Boots - Minor Speed"
				)),
				Arguments.of(ItemType.FINGER, null, null, List.of()),
				Arguments.of(ItemType.TRINKET, null, null, List.of()),
				Arguments.of(ItemType.TWO_HAND, null, null, List.of(
						"Enchant Weapon - Spell Power",
						"Enchant Weapon - Winter's Might"
				)),
				Arguments.of(ItemType.MAIN_HAND, null, null, List.of(
						"Enchant Weapon - Spell Power",
						"Enchant Weapon - Winter's Might"
				)),
				Arguments.of(ItemType.ONE_HAND, null, null, List.of(
						"Enchant Weapon - Spell Power",
						"Enchant Weapon - Winter's Might"
				)),
				Arguments.of(ItemType.OFF_HAND, null, null, List.of()),
				Arguments.of(ItemType.RANGED, null, null, List.of())
		);
	}

	static Stream<Arguments> getBestEnchantsDataTBC(CharacterClassId characterClassId) {
		var headEnchants = switch (characterClassId) {
			case WARLOCK -> List.of(
					"Hoodoo Hex",
					"Glyph of Power"
			);
			case PRIEST -> List.of(
					"Glyph of Power"
			);
			default -> throw new IllegalArgumentException();
		};
		var legEnchants = switch (characterClassId) {
			case WARLOCK, PRIEST -> List.of(
					"Runic Spellthread"
			);
			default -> throw new IllegalArgumentException();
		};
		return Stream.of(
				Arguments.of(ItemType.HEAD, ArmorSubType.CLOTH, null, headEnchants),
				Arguments.of(ItemType.NECK, null, null, List.of(

				)),
				Arguments.of(ItemType.SHOULDER, ArmorSubType.CLOTH, ExclusiveFaction.SCRYERS, List.of(
						"Zandalar Signet of Mojo",
						"Power of the Scourge",
						"Greater Inscription of the Orb"
				)),
				Arguments.of(ItemType.SHOULDER, ArmorSubType.CLOTH, ExclusiveFaction.ALDOR, List.of(
						"Power of the Scourge",
						"Greater Inscription of Discipline"
				)),
				Arguments.of(ItemType.BACK, ArmorSubType.CLOTH, null, List.of(
						"Enchant Cloak - Subtlety"
				)),
				Arguments.of(ItemType.CHEST, ArmorSubType.CLOTH, null, List.of(
						"Enchant Chest - Exceptional Stats"
				)),
				Arguments.of(ItemType.WRIST, ArmorSubType.CLOTH, null, List.of(
						"Enchant Bracer - Greater Intellect",
						"Enchant Bracer - Spellpower"
				)),
				Arguments.of(ItemType.HANDS, ArmorSubType.CLOTH, null, List.of(
						"Enchant Gloves - Blasting",
						"Enchant Gloves - Spell Strike",
						"Enchant Gloves - Fire Power",
						"Enchant Gloves - Frost Power",
						"Enchant Gloves - Shadow Power",
						"Enchant Gloves - Major Spellpower"
				)),
				Arguments.of(ItemType.WAIST, ArmorSubType.CLOTH, null, List.of()),
				Arguments.of(ItemType.LEGS, ArmorSubType.CLOTH, null, legEnchants),
				Arguments.of(ItemType.FEET, ArmorSubType.CLOTH, null, List.of(
						"Enchant Boots - Boar's Speed"
				)),
				Arguments.of(ItemType.FINGER, null, null, List.of(
						"Enchant Ring - Spellpower"
				)),
				Arguments.of(ItemType.TRINKET, null, null, List.of()),
				Arguments.of(ItemType.TWO_HAND, null, null, List.of(
						"Enchant Weapon - Deathfrost",
						"Enchant Weapon - Soulfrost",
						"Enchant Weapon - Sunfire",
						"Enchant Weapon - Major Spellpower",
						"Enchant Weapon - Winter's Might"
				)),
				Arguments.of(ItemType.MAIN_HAND, null, null, List.of(
						"Enchant Weapon - Deathfrost",
						"Enchant Weapon - Soulfrost",
						"Enchant Weapon - Sunfire",
						"Enchant Weapon - Major Spellpower",
						"Enchant Weapon - Winter's Might"
				)),
				Arguments.of(ItemType.ONE_HAND, null, null, List.of(
						"Enchant Weapon - Deathfrost",
						"Enchant Weapon - Soulfrost",
						"Enchant Weapon - Sunfire",
						"Enchant Weapon - Major Spellpower",
						"Enchant Weapon - Winter's Might"
				)),
				Arguments.of(ItemType.OFF_HAND, null, null, List.of()),
				Arguments.of(ItemType.RANGED, null, null, List.of())
		);
	}

	private void assertBestEnchants(CharacterClassId characterClassId, GameVersionId gameVersionId, ExclusiveFaction exclusiveFaction, ItemType itemType, ItemSubType itemSubType, List<String> expectedResult) {
		var gameVersion = characterRepository.getGameVersion(gameVersionId).orElseThrow();
		var lastPhase = gameVersion.getLastPhase();
		var maxLevel = lastPhase.getMaxLevel();
		var character = getCharacter(characterClassId, RaceId.UNDEAD, maxLevel, lastPhase.getPhaseId());

		if (exclusiveFaction != null) {
			character.getExclusiveFactions().set(List.of(exclusiveFaction));
		}

		var enchants = underTest.getBestEnchants(character, itemType, itemSubType);

		assertThat(enchants.stream().map(Enchant::getName)).hasSameElementsAs(expectedResult);
	}

	PlayerCharacter character;

	@BeforeEach
	void setup() {
		character = getCharacter();
	}
}
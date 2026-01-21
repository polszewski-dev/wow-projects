package wow.commons.repository.item;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.constant.AttributeConditions;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.ProfessionRestriction;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.EnchantId;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.PhaseId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AttributeConditions.SPELL;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.attribute.AttributeId.STAMINA;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class EnchantRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	EnchantRepository enchantRepository;

	@ParameterizedTest
	@CsvSource({
			"31372, Runic Spellthread",
			"27924, Enchant Ring - Spellpower"
	})
	void name_is_correct(int getId, String expected) {
		var enchant = getEnchant(getId);

		var actual = enchant.getName();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Runic Spellthread, Legs",
			"Enchant Ring - Spellpower, Finger"
	})
	void item_types_is_correct(String name, String expectedStr) {
		var enchant = getEnchant(name);

		var actual = enchant.getItemTypes();
		var expected = toList(expectedStr, ItemType::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Enchant Shield - Minor Stamina, Shield",
	})
	void item_subtypes_is_correct(String name, String expectedStr) {
		var enchant = getEnchant(name);

		var actual = enchant.getItemSubTypes();
		var expected = toList(expectedStr, ItemSubType::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Enchant Weapon - Soulfrost, 35",
	})
	void required_ilvl_is_correct(String name, int expected) {
		var enchant = getEnchant(name);

		var actual = enchant.getRequiredItemLevel();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Runic Spellthread, EPIC",
			"Enchant Ring - Spellpower, UNSPECIFIED"
	})
	void rarity_is_correct(String name, ItemRarity expected) {
		var enchant = getEnchant(name);

		var actual = enchant.getRarity();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Runic Spellthread, TBC_P0",
	})
	void required_phase_is_correct(String name, PhaseId expected) {
		var enchant = getEnchant(name);

		var actual = enchant.getEarliestPhaseId();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hoodoo Hex, Warlock",
			"Presence of Sight, Mage"
	})
	void required_phase_is_correct(String name, String expectedStr) {
		var enchant = getEnchant(name);

		var actual = enchant.getRequiredCharacterClassIds();
		var expected = toList(expectedStr, CharacterClassId::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Enchant Ring - Spellpower, ENCHANTING, 360",
			"Enchant Ring - Healing Power, ENCHANTING, 360",
	})
	void required_profession_is_correct(String name, ProfessionId expectedProfessionId, int expectedProfessionLevel) {
		var enchant = getEnchant(name);

		var actual = enchant.getCharacterRestriction().professionRestriction();
		var expected = ProfessionRestriction.of(expectedProfessionId, expectedProfessionLevel);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Greater Inscription of the Orb, The Scryers",
			"Greater Inscription of Discipline, The Aldor"
	})
	void required_xfaction_is_correct(String name, String expected) {
		var enchant = getEnchant(name);

		var actual = enchant.getCharacterRestriction().exclusiveFaction();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Greater Inscription of the Orb, CASTER_DPS",
			"Greater Inscription of Discipline, CASTER_DPS"
	})
	void pve_roles_is_correct(String name, String expectedStr) {
		var enchant = getEnchant(name);

		var actual = enchant.getPveRoles();
		var expected = toList(expectedStr, PveRole::parse);

		assertThat(actual).hasSameElementsAs(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Runic Spellthread, 2748",
			"Enchant Ring - Spellpower, 2928"
	})
	void enchant_id_is_correct(String name, int expected) {
		var enchant = getEnchant(name);

		var actual = enchant.getAppliedEnchantId();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getStatsData")
	void stats_is_correct(StatsData data) {
		var enchant = getEnchant(data.name());

		var actual = new StatLine(enchant.getEffect());
		var expected = data.statLines().getFirst();

		assertThat(actual).isEqualTo(expected);
	}

	static List<StatsData> getStatsData() {
		return List.of(
				new StatsData(
						"Runic Spellthread",
						List.of(
								new StatLine(
										Attributes.of(
												Attribute.of(POWER, 35, AttributeConditions.SPELL),
												Attribute.of(STAMINA, 20)
										),
										"Use: Permanently embroiders spellthread into pants, increasing spell damage and healing by up to 35 and Stamina by 20."
								)
						)
				),
				new StatsData(
						"Enchant Ring - Spellpower",
						List.of(
								new StatLine(
										POWER, 12, SPELL, "Permanently enchant a ring to increase spell damage and healing by up to 12. Only the Enchanter's rings can be enchanted, and enchanting a ring will cause it to become soulbound."
								)
						)
				)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"Runic Spellthread",
	})
	void tooltip_is_correct(String name) {
		var enchant = getEnchant(name);

		var actual = enchant.getTooltip();

		assertThat(actual).isBlank();
	}

	@ParameterizedTest
	@CsvSource({
			"Runic Spellthread, spell_nature_astralrecalgroup",
			"Enchant Ring - Spellpower, spell_holy_greaterheal"
	})
	void icon_is_correct(String name, String expected) {
		var enchant = getEnchant(name);

		var actual = enchant.getIcon();

		assertThat(actual).isEqualTo(expected);
	}

	Enchant getEnchant(int enchantId) {
		return enchantRepository.getEnchant(EnchantId.of(enchantId), TBC_P5).orElseThrow();
	}

	Enchant getEnchant(String name) {
		return enchantRepository.getEnchant(name, TBC_P5).orElseThrow();
	}
}

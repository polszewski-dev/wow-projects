package wow.commons.repository.item;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.Duration;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.categorization.*;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.ProfessionRestriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.item.SocketType;
import wow.commons.model.item.WeaponStats;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.SpellSchool;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AttributeConditions.*;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class ItemRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	ItemRepository itemRepository;

	@ParameterizedTest
	@CsvSource({
			"24262, Spellstrike Pants",
			"31051, Hood of the Malefic"
	})
	void name_is_correct(int itemId, String expected) {
		var item = getItem(itemId);

		var actual = item.getName();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, HEAD",
			"Scryer's Bloodgem, TRINKET"
	})
	void item_type_is_correct(String name, ItemType expected) {
		var item = getItem(name);

		var actual = item.getItemType();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, CLOTH",
			"Scryer's Bloodgem, "
	})
	void item_subtype_is_correct(String name, String expectedStr) {
		var item = getItem(name);

		var actual = item.getItemSubType();
		var expected = ItemSubType.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, EPIC",
			"Scryer's Bloodgem, RARE"
	})
	void rarity_is_correct(String name, ItemRarity expected) {
		var item = getItem(name);

		var actual = item.getRarity();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, BINDS_ON_PICK_UP",
			"Spellstrike Pants, BINDS_ON_EQUIP"
	})
	void binding_is_correct(String name, Binding expected) {
		var item = getItem(name);

		var actual = item.getBinding();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Ring of Ancient Knowledge, false",
			"Loop of Forged Power, true"
	})
	void unique_is_correct(String name, boolean expected) {
		var item = getItem(name);

		var actual = item.isUnique();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, 146",
			"Spellstrike Pants, 105"
	})
	void item_level_is_correct(String name, int expected) {
		var item = getItem(name);

		var actual = item.getItemLevel();

		assertThat(actual).isEqualTo(expected);
	}

	@Disabled
	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, Traded: Helm of the Forgotten Conqueror",
			"Spellstrike Pants, Tailoring"
	})
	void source_is_correct(String name, String expected) {
		var item = getItem(name);

		var actual = item.getSources().stream()
				.map(Object::toString)
				.collect(Collectors.joining("+"));

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Sunfire Robe, TBC_P5",
			"Voidheart Robe, TBC_P1",
			"Plagueheart Robe, TBC_P0"
	})
	void required_phase_is_correct(String name, PhaseId expected) {
		var item = getItem(name);

		var actual = item.getEarliestPhaseId();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hood of the Malefic, 70",
			"Spellstrike Pants, 70"
	})
	void required_level_is_correct(String name, int expected) {
		var item = getItem(name);

		var actual = item.getRequiredLevel();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Voidheart Crown, Warlock",
			"General's Silk Cuffs, Priest+Mage+Warlock",
	})
	void required_class_is_correct(String name, String expectedStr) {
		var item = getItem(name);

		var actual = item.getRequiredCharacterClassIds();
		var expected = toList(expectedStr, CharacterClassId::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Mindfang, HORDE",
			"Sageclaw, ALLIANCE",
	})
	void required_side_is_correct(String name, String expectedStr) {
		var item = getItem(name);

		var actual = item.getCharacterRestriction().side();
		var expected = Side.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Sunfire Robe, TAILORING, 350",
			"Pendant of Sunfire, JEWELCRAFTING, 350"
	})
	void required_profession_is_correct(String name, ProfessionId expectedProfessionId, int expectedProfessionLevel) {
		var item = getItem(name);

		var actual = item.getCharacterRestriction().professionRestriction();
		var expected = ProfessionRestriction.of(expectedProfessionId, expectedProfessionLevel);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Frozen Shadoweave Boots, SHADOWEAVE_TAILORING",
	})
	void required_profession_specialization_is_correct(String name, ProfessionSpecializationId expected) {
		var item = getItem(name);

		var actual = item.getCharacterRestriction().professionSpecId();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Scryer's Bloodgem, The Scryers",
			"Medallion of the Lightbearer, The Aldor"
	})
	void required_xfaction_is_correct(String name, String expected) {
		var item = getItem(name);

		var actual = item.getCharacterRestriction().exclusiveFaction();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Pants, CASTER_DPS",
			"Hood of the Malefic, CASTER_DPS"
	})
	void pve_roles_is_correct(String name, String expectedStr) {
		var item = getItem(name);

		var actual = item.getPveRoles();
		var expected = toList(expectedStr, PveRole::parse);

		assertThat(actual).hasSameElementsAs(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Pants, BLUE+YELLOW+RED",
			"Hood of the Malefic, META+YELLOW"
	})
	void socket_types_is_correct(String name, String expectedStr) {
		var item = getItem(name);

		var actual = item.getSocketTypes();
		var expected = toList(expectedStr, SocketType::valueOf);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getSocketBonusData")
	void socket_bonus_is_correct(SocketBonusData data) {
		var item = getItem(data.name);

		var actual = new StatLine(item.getSocketBonus());
		var expected = data.socketBonus;

		assertThat(actual).isEqualTo(expected);
	}

	record SocketBonusData(String name, StatLine socketBonus) {}

	static List<SocketBonusData> getSocketBonusData() {
		return List.of(
				new SocketBonusData(
						"Hood of the Malefic",
						new StatLine(POWER, 5, SPELL, "+5 Spell Damage and Healing")
				)
		);
	}

	@ParameterizedTest
	@MethodSource("getStatsData")
	void stats_is_correct(StatsData data) {
		var item = getItem(data.name());

		var actual = item.getEffects().stream()
				.map(StatLine::new)
				.toList();
		var expected = data.statLines();

		assertThat(actual).isEqualTo(expected);
	}

	static List<StatsData> getStatsData() {
		return List.of(
				new StatsData(
						"Drakestone of Shadow Wrath",
						List.of(
								new StatLine(POWER, 7, SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 7."),
								new StatLine(POWER, 21, AttributeCondition.and(SPELL_DAMAGE, SHADOW), "+21 Shadow Spell Damage")
						)
				),
				new StatsData(
						"Sunfire Robe",
						List.of(
								new StatLine(ARMOR, 266, "266 Armor"),
								new StatLine(STAMINA, 36, "+36 Stamina"),
								new StatLine(INTELLECT, 34, "+34 Intellect"),
								new StatLine(CRIT_RATING, 40, SPELL, "Equip: Improves spell critical strike rating by 40."),
								new StatLine(HASTE_RATING, 40, SPELL, "Equip: Improves spell haste rating by 40."),
								new StatLine(POWER, 71, SPELL, "Equip: Increases damage and healing done by magical spells and effects by up to 71.")
						)
				)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"Wand of the Demonsoul, 208, 387, Shadow, 198.33, 1.5",
	})
	void weapon_stats_is_correct(
			String name, int expectedDamageMin, int expectedDamageMax, String expectedDamageTypeStr, double expectedDps, double expectedSpeed
	) {
		var item = getItem(name);

		var actual = item.getWeaponStats();
		var expected = new WeaponStats(expectedDamageMin, expectedDamageMax, SpellSchool.parse(expectedDamageTypeStr), expectedDps, Duration.seconds(expectedSpeed));

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Pants, Spellstrike Infusion",
			"Hood of the Malefic, Malefic Raiment"
	})
	void item_set_is_correct(String name, String expected) {
		var item = getItem(name);

		var actual = item.getItemSet().getName();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Scryer's Bloodgem, 129132",
	})
	void activated_ability_is_correct(String name, int expected) {
		var item = getItem(name);

		var actual = item.getActivatedAbility().getId().value();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Pants",
			"Hood of the Malefic",
	})
	void tooltip_is_correct(String name) {
		var item = getItem(name);

		var actual = item.getTooltip();

		assertThat(actual).isBlank();
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Pants, inv_pants_cloth_14",
			"Hood of the Malefic, inv_helmet_103",
	})
	void icon_is_correct(String name, String expected) {
		var item = getItem(name);

		var actual = item.getIcon();

		assertThat(actual).isEqualTo(expected);
	}

	Item getItem(int itemId) {
		return itemRepository.getItem(ItemId.of(itemId), TBC_P5).orElseThrow();
	}

	Item getItem(String name) {
		return itemRepository.getItem(name, TBC_P5).orElseThrow();
	}
}

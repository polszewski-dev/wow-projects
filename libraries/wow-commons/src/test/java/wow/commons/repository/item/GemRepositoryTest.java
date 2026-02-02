package wow.commons.repository.item;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.ProfessionRestriction;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.GemId;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AttributeConditions.SPELL;
import static wow.commons.constant.AttributeConditions.SPELL_DAMAGE;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class GemRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	GemRepository gemRepository;

	@ParameterizedTest
	@CsvSource({
			"35760, Reckless Pyrestone",
			"34220, Chaotic Skyfire Diamond"
	})
	void name_is_correct(int getId, String expected) {
		var gem = getGem(getId);

		var actual = gem.getName();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone",
			"Chaotic Skyfire Diamond"
	})
	void item_type_is_correct(String name) {
		var gem = getGem(name);

		var actual = gem.getItemType();
		var expected = ItemType.GEM;

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone",
			"Chaotic Skyfire Diamond"
	})
	void item_subtype_is_correct(String name) {
		var gem = getGem(name);

		var actual = gem.getItemSubType();

		assertThat(actual).isNull();
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, EPIC",
			"Chaotic Skyfire Diamond, RARE"
	})
	void rarity_is_correct(String name, ItemRarity expected) {
		var gem = getGem(name);

		var actual = gem.getRarity();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, NO_BINDING",
			"Infused Fire Opal, BINDS_ON_PICK_UP"
	})
	void rarity_is_correct(String name, Binding expected) {
		var gem = getGem(name);

		var actual = gem.getBinding();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, false",
			"Infused Fire Opal, true"
	})
	void unique_is_correct(String name, boolean expected) {
		var gem = getGem(name);

		var actual = gem.isUnique();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, 100",
			"Chaotic Skyfire Diamond, 70"
	})
	void rarity_is_correct(String name, int expected) {
		var gem = getGem(name);

		var actual = gem.getItemLevel();

		assertThat(actual).isEqualTo(expected);
	}

	@Disabled
	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, Jewelcrafting",
			"Infused Fire Opal, Trash: SV"
	})
	void source_is_correct(String name, String expected) {
		var gem = getGem(name);

		var actual = gem.getSources().stream()
				.map(Object::toString)
				.collect(Collectors.joining("+"));

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, TBC_P5",
			"Chaotic Skyfire Diamond, TBC_P1"
	})
	void required_phase_is_correct(String name, PhaseId expected) {
		var gem = getGem(name);

		var actual = gem.getEarliestPhaseId();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Don Julio's Heart, JEWELCRAFTING, 360",
	})
	void required_profession_is_correct(String name, ProfessionId expectedProfessionId, int expectedProfessionLevel) {
		var gem = getGem(name);

		var actual = gem.getCharacterRestriction().professionRestriction();
		var expected = ProfessionRestriction.of(expectedProfessionId, expectedProfessionLevel);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, CASTER_DPS",
			"Chaotic Skyfire Diamond, CASTER_DPS"
	})
	void pve_roles_is_correct(String name, String expectedStr) {
		var gem = getGem(name);

		var actual = gem.getPveRoles();
		var expected = toList(expectedStr, PveRole::parse);

		assertThat(actual).hasSameElementsAs(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, ORANGE",
			"Chaotic Skyfire Diamond, META"
	})
	void color_is_correct(String name, GemColor expected) {
		var gem = getGem(name);

		var actual = gem.getColor();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getStatsData")
	void stats_is_correct(StatsData data) {
		var gem = getGem(data.name());

		var actual = gem.getEffects().stream()
				.map(StatLine::new)
				.toList();
		var expected = data.statLines();

		assertThat(actual).isEqualTo(expected);
	}

	static List<StatsData> getStatsData() {
		return List.of(
				new StatsData(
						"Reckless Pyrestone",
						List.of(
								new StatLine(HASTE_RATING, 5, SPELL, "+5 Spell Haste Rating"),
								new StatLine(POWER, 6, SPELL_DAMAGE, "+6 Spell Damage")
						)
				),
				new StatsData(
						"Chaotic Skyfire Diamond",
						List.of(
								new StatLine(CRIT_RATING, 12, SPELL, "+12 Spell Critical"),
								new StatLine(CRIT_EFFECT_PCT, 3, SPELL_DAMAGE, "3% Increased Critical Damage")
						)
				)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"Chaotic Skyfire Diamond, Requires at least 2 Blue Gems"
	})
	void meta_enablers_is_correct(String name, String expectedStr) {
		var gem = getGem(name);

		var actual = gem.getMetaEnablers();
		var expected = toList(expectedStr, MetaEnabler::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone",
			"Chaotic Skyfire Diamond"
	})
	void tooltip_is_correct(String name) {
		var gem = getGem(name);

		var actual = gem.getTooltip();

		assertThat(actual).isBlank();
	}

	@ParameterizedTest
	@CsvSource({
			"Reckless Pyrestone, inv_jewelcrafting_pyrestone_02",
			"Chaotic Skyfire Diamond, inv_misc_gem_diamond_07"
	})
	void icon_is_correct(String name, String expected) {
		var gem = getGem(name);

		var actual = gem.getIcon();

		assertThat(actual).isEqualTo(expected);
	}

	Gem getGem(int gemId) {
		return gemRepository.getGem(GemId.of(gemId), TBC_P5).orElseThrow();
	}

	Gem getGem(String name) {
		return gemRepository.getGem(name, TBC_P5).orElseThrow();
	}
}

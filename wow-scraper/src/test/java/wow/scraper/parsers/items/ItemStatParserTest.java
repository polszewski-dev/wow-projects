package wow.scraper.parsers.items;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.OnUseAbility;
import wow.commons.model.attributes.complex.special.ProcAbility;
import wow.scraper.ScraperTestConfig;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.repository.StatPatternRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ScraperTestConfig.class)
class ItemStatParserTest {
	@Autowired
	StatPatternRepository statPatternRepository;

	@DisplayName("Test parsing proc strings")
	@ParameterizedTest(name = "[{index}] LINE = {0}, STAT = {1}")
	@MethodSource("getProcTestArguments")
	void testParseProc(String line, String expectedStatString) {
		StatParser parser = statPatternRepository.getItemStatParser(TBC);
		boolean success = parser.tryParse(line);
		Attributes stats = parser.getParsedStats();

		assertThat(success).isTrue();
		assertThat(stats.getPrimitiveAttributes()).isEmpty();
		assertThat(stats.getComplexAttributeMap()).hasSize(1);

		List<SpecialAbility> specialAbilities = stats.getSpecialAbilities();

		assertThat(specialAbilities).isNotNull().hasSize(1);
		assertThat(specialAbilities.get(0)).isInstanceOf(ProcAbility.class);

		ProcAbility specialAbility = (ProcAbility)specialAbilities.get(0);

		assertThat(specialAbility.getLine()).isEqualTo(line);
		assertThat(specialAbility).hasToString(expectedStatString);
	}

	@DisplayName("Test parsing on-use strings")
	@ParameterizedTest(name = "[{index}] LINE = {0}, STAT = {1}")
	@MethodSource("getOnUseTestArguments")
	void testParseOnUse(String line, String expectedStatString) {
		StatParser parser = statPatternRepository.getItemStatParser(TBC);
		boolean success = parser.tryParse(line);
		Attributes stats = parser.getParsedStats();

		assertThat(success).isTrue();
		assertThat(stats.getPrimitiveAttributes()).isEmpty();
		assertThat(stats.getComplexAttributeMap()).hasSize(1);

		List<SpecialAbility> specialAbilities = stats.getSpecialAbilities();

		assertThat(specialAbilities).isNotNull().hasSize(1);
		assertThat(specialAbilities.get(0)).isInstanceOf(OnUseAbility.class);

		OnUseAbility specialAbility = (OnUseAbility)specialAbilities.get(0);

		assertThat(specialAbility.getLine()).isEqualTo(line);
		assertThat(specialAbility).hasToString(expectedStatString);
	}

	static Stream<Arguments> getProcTestArguments() {
		return Stream.of(
			arguments(
					"Equip: Your harmful spells have a chance to increase your spell haste rating by 320 for 6 secs. (Proc chance: 10%, 45s cooldown)",
					"(event: SPELL_HIT, chance: 10%, 320 spell haste rating | 6s/45s)"
			),
			arguments(
					"Equip: 2% chance on successful spellcast to increase your spell damage by up to 120 for 15 sec. (Proc chance: 2%)",
					"(event: SPELL_HIT, chance: 2%, 120 sd | 15s/-)"
			),
			arguments(
					"Equip: Chance on spell critical hit to increase your spell damage and healing by 225 for 10 secs. (Proc chance: 20%, 45s cooldown)",
					"(event: SPELL_CRIT, chance: 20%, 225 sp | 10s/45s)"
			),
			arguments(
					"Equip: Gives a chance when your harmful spells land to increase the damage of your spells and effects by 132 for 10 sec. (Proc chance: 5%)",
					"(event: SPELL_HIT, chance: 5%, 132 sd | 10s/-)"
			),
			arguments(
					"Equip: Gives a chance when your harmful spells land to increase the damage of your spells and effects by up to 130 for 10 sec. (Proc chance: 20%, 50s cooldown)",
					"(event: SPELL_HIT, chance: 20%, 130 sd | 10s/50s)"
			),
			arguments(
					"Equip: Grants 170 increased spell damage for 10 sec when one of your spells is resisted.",
					"(event: SPELL_RESIST, chance: 100%, 170 sd | 10s/-)"
			),
			arguments(
					"Equip: Your healing and damage spells have a chance to increase your healing by up to 175 and damage by up to 59 for 10 secs. (Proc chance: 10%, 1m cooldown)",
					"(event: SPELL_HIT, chance: 10%, 59 sd | 10s/1m)"
			),
			arguments(
					"Equip: Your offensive spells have a chance on hit to increase your spell damage by 95 for 10 secs. (Proc chance: 10%, 1m cooldown)",
					"(event: SPELL_HIT, chance: 10%, 95 sd | 10s/1m)"
			),
			arguments(
					"Equip: Your spell critical strikes have a 50% chance to grant you 145 spell haste rating for 5 sec. (Proc chance: 50%)",
					"(event: SPELL_CRIT, chance: 50%, 145 spell haste rating | 5s/-)"
			),
			arguments(
					"Equip: Your spell critical strikes have a chance to increase your spell damage and healing by 190 for 15 sec. (Proc chance: 20%, 45s cooldown)",
					"(event: SPELL_CRIT, chance: 20%, 190 sp | 15s/45s)"
			)
		);
	}

	static Stream<Arguments> getOnUseTestArguments() {
		return Stream.of(
			arguments(
					"Use: Conjures a Power Circle lasting for 15 sec.  While standing in this circle, the caster gains up to 320 spell damage and healing. (1 Min, 30 Sec Cooldown)",
					"(320 sp | 15s/1m30s)"
			),
			arguments(
					"Use: Increases damage and healing done by magical spells and effects by up to 155 for 20 sec. (2 Min Cooldown)",
					"(155 sp | 20s/2m)"
			),
			arguments(
					"Use: Increases damage done by magical spells and effects by up to 50, and decreases the magical resistances of your spell targets by 100 for 30 sec. (3 Min Cooldown)",
					"(50 sd | 30s/3m)"
			),
			arguments(
					"Use: Increases healing done by magical spells and effects by up to 350 and damage done by spells by up to 117 for 15 sec. (1 Min, 30 Sec Cooldown)",
					"(117 sd | 15s/1m30s)"
			),
			arguments(
					"Use: Increases healing done by spells and effects by up to 282 and damage done by spells by up to 94 for 20 sec. (2 Min Cooldown)",
					"(94 sd | 20s/2m)"
			),
			arguments(
					"Use: Increases healing done by spells by up to 297 and damage done by spells by up to 99 for 20 sec. (2 Min Cooldown)",
					"(99 sd | 20s/2m)"
			),
			arguments(
					"Use: Increases healing done by up to 238 and damage done by up to 79 for all magical spells and effects for 15 sec. (1 Min, 30 Sec Cooldown)",
					"(79 sd | 15s/1m30s)"
			),
			arguments(
					"Use: Increases spell damage and healing by up to 250, and increases mana cost of spells by 20% for 20 sec. (5 Min Cooldown)",
					"(250 sp | 20s/5m)"
			),
			arguments(
					"Use: Increases spell damage by up to 84 and healing by up to 156 for 15 sec. (1 Min, 30 Sec Cooldown)",
					"(84 sd | 15s/1m30s)"
			),
			arguments(
					"Use: Increases spell damage by up to 125 for 15 sec. (1 Min, 30 Sec Cooldown)",
					"(125 sd | 15s/1m30s)"
			),
			arguments(
					"Use: Increases spell damage by up to 34 for all nearby party members.  Lasts 30 min. (1 Hour Cooldown)",
					"(34 sd | 30m/1h)"
			),
			arguments(
					"Use: Increases spell damage done by up to 120 and healing done by up to 220 for 15 sec. (1 Min, 30 Sec Cooldown)",
					"(120 sd | 15s/1m30s)"
			),
			arguments(
					"Use: Increases spell hit rating by 80 for 15 sec. (1 Min, 15 Sec Cooldown)",
					"(80 spell hit rating | 15s/1m15s)"
			),
			arguments(
					"Use: Increases the spell critical hit chance of nearby party members by 2% for 30 min. (1 Hour Cooldown)",
					"(2% spell crit | 30m/1h)"
			),
			arguments(
					"Use: Increases your spell damage by up to 100 and your healing by up to 190 for 15 sec. (1 Min, 15 Sec Cooldown)",
					"(100 sd | 15s/1m15s)"
			),
			arguments(
					"Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)",
					"(175 spell haste rating | 20s/2m)"
			)
		);
	}
}
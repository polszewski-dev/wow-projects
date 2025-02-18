package wow.simulator.log.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.stats.AbilityStats;
import wow.simulator.model.stats.Stats;
import wow.simulator.util.SpellInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.SpellInfos.*;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
class StatisticsGatheringHandlerTest extends WowSimulatorSpringTest {
	@ParameterizedTest
	@CsvSource({
		"SHADOW_BOLT,		3",
		"CURSE_OF_AGONY,	1.5",
		"DRAIN_LIFE,		5"
	})
	void totalCastTimeIsCorrect(AbilityId abilityId, double expectedCastTime) {
		player.cast(abilityId);

		updateUntil(30);

		var stat = getAbilityStat(abilityId);

		assertThat(stat.getTotalCastTime()).isEqualTo(Duration.seconds(expectedCastTime));
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT",
			"CURSE_OF_AGONY",
			"DRAIN_LIFE"
	})
	void numCastsIsCorrect(AbilityId abilityId) {
		player.cast(abilityId);

		updateUntil(30);

		var stat = getAbilityStat(abilityId);

		assertThat(stat.getNumCasts()).isEqualTo(1);
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT",
			"CURSE_OF_AGONY",
			"DRAIN_LIFE"
	})
	void numHitIsCorrect(AbilityId abilityId) {
		player.cast(abilityId);

		updateUntil(30);

		var stat = getAbilityStat(abilityId);

		assertThat(stat.getNumHit()).isEqualTo(1);
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT, 		1",
			"CURSE_OF_AGONY, 	0",
			"DRAIN_LIFE, 		0"
	})
	void numCritIsCorrect(AbilityId abilityId, int expectedNumCrits) {
		critsOnlyOnFollowingRolls(0);

		player.cast(abilityId);

		updateUntil(30);

		var stat = getAbilityStat(abilityId);

		assertThat(stat.getNumCrit()).isEqualTo(expectedNumCrits);
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT",
			"CURSE_OF_AGONY",
			"DRAIN_LIFE"
	})
	void totalDamageIsCorrect(AbilityId abilityId) {
		player.cast(abilityId);

		updateUntil(30);

		var stat = getAbilityStat(abilityId);
		var spellInfo = getSpellInfo(abilityId);

		assertThat(stat.getTotalDamage()).isEqualTo((int)spellInfo.damage());
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT,		3",
			"CURSE_OF_AGONY,	1.5",
			"DRAIN_LIFE,		5"
	})
	void dpsIsCorrect(AbilityId abilityId, double expectedCastTime) {
		player.cast(abilityId);

		updateUntil(30);

		var stat = getAbilityStat(abilityId);
		var spellInfo = getSpellInfo(abilityId);

		assertThat(stat.getDps()).isEqualTo((int)(spellInfo.damage() / expectedCastTime));
	}

	AbilityStats getAbilityStat(AbilityId abilityId) {
		return stats.getAbilityStats().stream()
				.filter(x -> x.getAbility().getAbilityId() == abilityId)
				.findAny()
				.orElseThrow();
	}

	SpellInfo getSpellInfo(AbilityId abilityId) {
		return switch (abilityId) {
			case SHADOW_BOLT -> SHADOW_BOLT_INFO;
			case CURSE_OF_AGONY -> CURSE_OF_AGONY_INFO;
			case DRAIN_LIFE -> DRAIN_LIFE_INFO;
			default -> throw new IllegalArgumentException();
		};
	}

	StatisticsGatheringHandler handler;
	Stats stats = new Stats();

	@BeforeEach
	void setUp() {
		setupTestObjects();

		handler = new StatisticsGatheringHandler(player, stats);

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);
	}
}
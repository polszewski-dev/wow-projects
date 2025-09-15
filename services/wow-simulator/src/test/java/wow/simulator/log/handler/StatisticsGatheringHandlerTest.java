package wow.simulator.log.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.Duration;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.stats.AbilityStats;
import wow.simulator.model.stats.Stats;
import wow.simulator.util.SpellInfo;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.SpellInfos.*;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
class StatisticsGatheringHandlerTest extends WowSimulatorSpringTest {
	@ParameterizedTest
	@CsvSource({
		"Shadow Bolt,		3",
		"Curse of Agony,	1.5",
		"Drain Life,		5"
	})
	void totalCastTimeIsCorrect(String name, double expectedCastTime) {
		player.cast(name);

		updateUntil(30);

		var stat = getAbilityStat(name);

		assertThat(stat.getTotalCastTime()).isEqualTo(Duration.seconds(expectedCastTime));
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt",
			"Curse of Agony",
			"Drain Life"
	})
	void numCastsIsCorrect(String name) {
		player.cast(name);

		updateUntil(30);

		var stat = getAbilityStat(name);

		assertThat(stat.getNumCasts()).isEqualTo(1);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt",
			"Curse of Agony",
			"Drain Life"
	})
	void numHitIsCorrect(String name) {
		player.cast(name);

		updateUntil(30);

		var stat = getAbilityStat(name);

		assertThat(stat.getNumHit()).isEqualTo(1);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt, 		1",
			"Curse of Agony, 	0",
			"Drain Life, 		0"
	})
	void numCritIsCorrect(String name, int expectedNumCrits) {
		critsOnlyOnFollowingRolls(0);

		player.cast(name);

		updateUntil(30);

		var stat = getAbilityStat(name);

		assertThat(stat.getNumCrit()).isEqualTo(expectedNumCrits);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt",
			"Curse of Agony",
			"Drain Life"
	})
	void totalDamageIsCorrect(String name) {
		player.cast(name);

		updateUntil(30);

		var stat = getAbilityStat(name);
		var spellInfo = getSpellInfo(name);

		assertThat(stat.getTotalDamage()).isEqualTo((int)spellInfo.damage());
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt,		3",
			"Curse of Agony,	1.5",
			"Drain Life,		5"
	})
	void dpsIsCorrect(String name, double expectedCastTime) {
		player.cast(name);

		updateUntil(30);

		var stat = getAbilityStat(name);
		var spellInfo = getSpellInfo(name);

		assertThat(stat.getDps()).isEqualTo((int)(spellInfo.damage() / expectedCastTime));
	}

	AbilityStats getAbilityStat(String name) {
		return stats.getAbilityStats().stream()
				.filter(x -> x.getAbility().getName().equals(name))
				.findAny()
				.orElseThrow();
	}

	SpellInfo getSpellInfo(String name) {
		var map = Map.of(
				SHADOW_BOLT, SHADOW_BOLT_INFO,
				CURSE_OF_AGONY, CURSE_OF_AGONY_INFO,
				DRAIN_LIFE, DRAIN_LIFE_INFO
		);

		return map.get(name);
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
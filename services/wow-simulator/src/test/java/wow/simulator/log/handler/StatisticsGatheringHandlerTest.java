package wow.simulator.log.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.Duration;
import wow.commons.model.spell.Spell;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.stats.AbilityStats;
import wow.simulator.model.stats.CooldownStats;
import wow.simulator.model.stats.EffectStats;
import wow.simulator.model.stats.Stats;
import wow.simulator.model.time.Time;
import wow.simulator.util.SpellInfo;

import java.util.Map;
import java.util.Optional;

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

	@Test
	void lifeTapDoesNotContributeToDamageStat() {
		setMana(player, 0);
		player.cast(LIFE_TAP);

		updateUntil(30);

		var stat = getAbilityStat(LIFE_TAP);

		assertThat(stat.getNumCasts()).isEqualTo(1);
		assertThat(stat.getTotalDamage()).isZero();
	}

	@ParameterizedTest
	@CsvSource({
			"Curse of Doom,		60",
			"Curse of Agony,	24",
			"Corruption,        18",
			"Immolate,          15",
			"Drain Life,		5"
	})
	void correctEffectUptime(String name, double expectedUptime) {
		player.cast(name);

		updateUntil(120);

		var stat = getEffectStat(name);

		assertThat(stat.getUptime().getSeconds()).isEqualTo(expectedUptime);
	}

	@ParameterizedTest
	@CsvSource({
			"Curse of Doom,		60",
			"Curse of Agony,	24",
			"Corruption,        18",
			"Immolate,          15",
			"Drain Life,		5"
	})
	void correctEffectUptimeWhenCastTwice(String name, double expectedUptime) {
		player.cast(name);
		player.idleUntil(Time.at(expectedUptime));
		player.cast(name);

		updateUntil(120);

		var stat = getEffectStat(name);

		assertThat(stat.getUptime().getSeconds()).isEqualTo(2 * expectedUptime);
	}

	@ParameterizedTest
	@CsvSource({
			"Curse of Doom,		60",
	})
	void correctCooldownUptime(String name, double expectedUptime) {
		player.cast(name);

		updateUntil(120);

		var stat = getCooldownStat(name);

		assertThat(stat.getUptime().getSeconds()).isEqualTo(expectedUptime);
	}

	AbilityStats getAbilityStat(String name) {
		return stats.getAbilityStats().stream()
				.filter(x -> x.getAbility().getName().equals(name))
				.findAny()
				.orElseThrow();
	}

	EffectStats getEffectStat(String name) {
		return stats.getEffectStats().stream()
				.filter(x -> x.getEffect().getName().equals(name))
				.findAny()
				.orElseThrow();
	}

	CooldownStats getCooldownStat(String name) {
		return stats.getCooldownStats().stream()
				.filter(x -> getSpell(x).isPresent() && getSpell(x).get().getName().equals(name))
				.findAny()
				.orElseThrow();
	}

	Optional<Spell> getSpell(CooldownStats stats) {
		var spellId = stats.getSpellId(player);
		return getSpellRepository().getSpell(spellId, player.getPhaseId());
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
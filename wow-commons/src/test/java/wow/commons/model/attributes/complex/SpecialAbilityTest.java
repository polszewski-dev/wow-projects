package wow.commons.model.attributes.complex;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.modifiers.ProcEvent;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static wow.commons.model.attributes.complex.modifiers.ProcEvent.*;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_POWER;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class SpecialAbilityTest {
	@DisplayName("Proc with internal cooldown")
	@ParameterizedTest(name = "[{index}] (event: {0}, chance: 50%, 100 sp | 15s/60s) == {1}, hit: {2}, crit: {3}, castTime: {4}")
	@MethodSource
	void procWithICD(ProcEvent event, double expected, double hitChance, double critChance, double castTime) {
		SpecialAbility proc = SpecialAbility.proc(
				event,
				Percent.of(50),
				Attributes.of(SPELL_POWER, 100),
				Duration.seconds(15),
				Duration.seconds(60),
				"test"
		);

		Attributes statEquivalent = proc.getStatEquivalent(StatProvider.fixedValues(hitChance, critChance, Duration.seconds(castTime)));

		assertThat(statEquivalent.getSpellPower()).isEqualTo(expected, PRECISION);
	}

	static Stream<Arguments> procWithICD() {
		return Stream.of(
				arguments(SPELL_HIT, 25, 0.9, 0.2, 2),
				arguments(SPELL_HIT, 25, 0.9, 0.2, 20),
				arguments(SPELL_CRIT, 25, 0.9, 0.2, 2),
				arguments(SPELL_CRIT, 7.5, 0.9, 0.2, 20),
				arguments(SPELL_RESIST, 25, 0.9, 0.2, 2),
				arguments(SPELL_RESIST, 3.75, 0.9, 0.2, 20),
				arguments(SPELL_DAMAGE, 25, 0.9, 0.2, 2),
				arguments(SPELL_DAMAGE, 25, 0.9, 0.2, 20)
		);
	}

	@DisplayName("Proc without internal cooldown")
	@ParameterizedTest(name = "[{index}] (event: {0}, chance: 50%, 100 sp | 15s/60s) == {1}, hit: {2}, crit: {3}, castTime: {4}")
	@MethodSource
	void procWithoutICD(ProcEvent event, double expected, double hitChance, double critChance, double castTime) {
		SpecialAbility proc = SpecialAbility.proc(
				event,
				Percent.of(50),
				Attributes.of(SPELL_POWER, 100),
				Duration.seconds(15),
				null,
				"test"
		);

		Attributes statEquivalent = proc.getStatEquivalent(StatProvider.fixedValues(hitChance, critChance, Duration.seconds(castTime)));

		assertThat(statEquivalent.getSpellPower()).isEqualTo(expected, PRECISION);
	}

	static Stream<Arguments> procWithoutICD() {
		return Stream.of(
				arguments(SPELL_HIT, 100, 0.9, 0.2, 2),
				arguments(SPELL_HIT, 33.75, 0.9, 0.2, 20),
				arguments(SPELL_CRIT, 75, 0.9, 0.2, 2),
				arguments(SPELL_CRIT, 7.5, 0.9, 0.2, 20),
				arguments(SPELL_RESIST, 37.5, 0.9, 0.2, 2),
				arguments(SPELL_RESIST, 3.75, 0.9, 0.2, 20),
				arguments(SPELL_DAMAGE, 100, 0.9, 0.2, 2),
				arguments(SPELL_DAMAGE, 37.5, 0.9, 0.2, 20)
		);
	}

	@Test
	@DisplayName("OnUse")
	void onUse() {
		SpecialAbility onUse = SpecialAbility.onUse(
				Attributes.of(SPELL_POWER, 100),
				Duration.seconds(15),
				Duration.seconds(60),
				"test"
		);

		Attributes statEquivalent = onUse.getStatEquivalent(StatProvider.fixedValues(0.9, 0.2, Duration.seconds(2)));

		assertThat(statEquivalent.getSpellPower()).isEqualTo(25);
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
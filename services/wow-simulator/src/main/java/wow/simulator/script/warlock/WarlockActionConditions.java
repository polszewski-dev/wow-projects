package wow.simulator.script.warlock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spell.AbilityId;
import wow.simulator.script.ConditionalSpellCast;

import java.util.Optional;
import java.util.stream.Stream;

import static wow.simulator.script.CommonAbilityIds.BLOOD_FURY;
import static wow.simulator.script.ConditionalSpellCast.of;
import static wow.simulator.script.warlock.WarlockAbilityIds.*;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
@AllArgsConstructor
@Getter
public enum WarlockActionConditions {
	CURSE_OF_DOOM_COND(of(CURSE_OF_DOOM).dotCondition()),
	CURSE_OF_AGONY_COND(of(CURSE_OF_AGONY).dotCondition()),
	CORRUPTION_COND(of(CORRUPTION).dotCondition()),
	UNSTABLE_AFFLICTION_COND(of(UNSTABLE_AFFLICTION).dotCondition()),
	IMMOLATE_COND(of(IMMOLATE).dotCondition()),
	SIPHON_LIFE_COND(of(SIPHON_LIFE).dotCondition()),
	SHADOWBURN_COND(of(SHADOWBURN).notOnCooldown()),
	AMPLIFY_CURSE_COND(of(AMPLIFY_CURSE).notOnCooldown()),
	BLOOD_FURY_COND(of(BLOOD_FURY).notOnCooldown()),
	SHADOW_BOLT_COND(of(SHADOW_BOLT)),
	DRAIN_LIFE_COND(of(DRAIN_LIFE));

	private final ConditionalSpellCast cast;

	public static Optional<ConditionalSpellCast> forAbility(AbilityId abilityId) {
		return Stream.of(values())
				.map(x -> x.cast)
				.filter(x -> x.abilityId().equals(abilityId))
				.findAny();
	}
}

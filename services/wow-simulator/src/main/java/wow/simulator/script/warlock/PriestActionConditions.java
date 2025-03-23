package wow.simulator.script.warlock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spell.AbilityId;
import wow.simulator.script.ConditionalSpellCast;

import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.spell.AbilityId.*;
import static wow.simulator.script.ConditionalSpellCast.of;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
@AllArgsConstructor
@Getter
public enum PriestActionConditions {
	SHADOW_WORD_PAIN_COND(of(SHADOW_WORD_PAIN).dotCondition()),
	VAMPIRIC_TOUCH_COND(of(VAMPIRIC_TOUCH).dotCondition()),
	DEVOURING_PLAGUE_COND(of(DEVOURING_PLAGUE).dotCondition()),
	MIND_BLAST_COND(of(MIND_BLAST).notOnCooldown()),
	SHADOW_WORD_DEATH_COND(of(SHADOW_WORD_DEATH).notOnCooldown()),
	MIND_FLAY_COND(of(MIND_FLAY)),
	HOLY_FIRE_COND(of(HOLY_FIRE).dotCondition()),
	SMITE_COND(of(SMITE)),
	SHOOT_COND(of(SHOOT));

	private final ConditionalSpellCast cast;

	public static Optional<ConditionalSpellCast> forAbility(AbilityId abilityId) {
		return Stream.of(values())
				.map(x -> x.cast)
				.filter(x -> x.abilityId() == abilityId)
				.findAny();
	}
}

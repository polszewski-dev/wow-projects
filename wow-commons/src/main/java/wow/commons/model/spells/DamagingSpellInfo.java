package wow.commons.model.spells;

import wow.commons.model.Percent;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record DamagingSpellInfo(
		Percent coeffDirect,
		Percent coeffDot,
		boolean bolt,
		EffectId requiredSpellEffect,
		EffectId spellEffectRemovedOnHit,
		EffectId bonusDamageIfUnderSpellEffect
) {
}

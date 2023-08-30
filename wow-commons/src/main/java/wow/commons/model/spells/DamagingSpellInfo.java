package wow.commons.model.spells;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record DamagingSpellInfo(
		boolean bolt,
		EffectId requiredSpellEffect,
		EffectId spellEffectRemovedOnHit,
		EffectId bonusDamageIfUnderSpellEffect
) {
}

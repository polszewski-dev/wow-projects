package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Percent;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@AllArgsConstructor
@Getter
public class DamagingSpellInfo {
	private final Percent coeffDirect;
	private final Percent coeffDot;
	private final boolean bolt;
	private final EffectId requiredSpellEffect;
	private final EffectId spellEffectRemovedOnHit;
	private final EffectId bonusDamageIfUnderSpellEffect;
	private final List<Integer> dotScheme;
}

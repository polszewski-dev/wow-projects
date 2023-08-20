package wow.simulator.scripts.warlock;

import wow.simulator.scripts.ConditionalSpellCast;

import static wow.commons.model.spells.SpellId.*;
import static wow.simulator.scripts.ConditionalSpellCast.of;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
final class WarlockActionConditions {
	static final ConditionalSpellCast CURSE_OF_DOOM_COND = of(CURSE_OF_DOOM).dotCondition();

	static final ConditionalSpellCast CURSE_OF_AGONY_COND = of(CURSE_OF_AGONY).dotCondition();

	static final ConditionalSpellCast CORRUPTION_COND = of(CORRUPTION).dotCondition();

	static final ConditionalSpellCast UNSTABLE_AFFLICTION_COND = of(UNSTABLE_AFFLICTION).dotCondition();

	static final ConditionalSpellCast IMMOLATE_COND = of(IMMOLATE).dotCondition();

	static final ConditionalSpellCast SIPHON_LIFE_COND = of(SIPHON_LIFE).dotCondition();

	static final ConditionalSpellCast SHADOWBURN_COND = of(SHADOWBURN).notOnCooldown();

	static final ConditionalSpellCast AMPLIFY_CURSE_COND = of(AMPLIFY_CURSE).notOnCooldown();

	static final ConditionalSpellCast BLOOD_FURY_COND = of(BLOOD_FURY).notOnCooldown();

	static final ConditionalSpellCast SHADOW_BOLT_COND = of(SHADOW_BOLT);

	static final ConditionalSpellCast DRAIN_LIFE_COND = of(DRAIN_LIFE);

	private WarlockActionConditions() {
	}
}

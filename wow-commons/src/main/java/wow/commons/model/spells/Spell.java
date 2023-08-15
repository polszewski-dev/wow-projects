package wow.commons.model.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.model.config.ConfigurationElement;
import wow.commons.model.talents.TalentTree;

import java.util.HashSet;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-09-19
 */
public interface Spell extends ConfigurationElement<SpellIdAndRank> {
	SpellInfo getSpellInfo();

	CastInfo getCastInfo();

	DirectDamageInfo getDirectDamageInfo();

	DotDamageInfo getDotDamageInfo();

	default SpellId getSpellId() {
		return getId().spellId();
	}

	default Integer getRank() {
		return getId().rank();
	}

	default TalentTree getTalentTree() {
		return getSpellInfo().getTalentTree();
	}

	default SpellSchool getSpellSchool() {
		return getSpellInfo().getSpellSchool();
	}

	default Percent getCoeffDirect() {
		return getSpellInfo().getDamagingSpellInfo().coeffDirect();
	}

	default Percent getCoeffDot() {
		return getSpellInfo().getDamagingSpellInfo().coeffDot();
	}

	default Cost getCost() {
		return getCastInfo().cost();
	}

	default Duration getCastTime() {
		return getCastInfo().castTime();
	}

	default boolean isChanneled() {
		return getSpellInfo().isChanneled();
	}

	default boolean isBolt() {
		return getSpellInfo().getDamagingSpellInfo().bolt();
	}

	default SpellTarget getTarget() {
		return getSpellInfo().getTarget();
	}

	default boolean isFriendly() {
		return getTarget().isFriendly();
	}

	default boolean isHostile() {
		return getTarget().isHostile();
	}

	default AppliedEffect getAppliedEffect() {
		return getCastInfo().appliedEffect();
	}

	default boolean hasDirectComponent() {
		return getDirectDamageInfo() != null;
	}

	default boolean hasDotComponent() {
		return getDotDamageInfo() != null;
	}

	default boolean hasDamageComponent() {
		return hasDirectComponent() || hasDotComponent();
	}

	default int getMinDmg() {
		return getDirectDamageInfo().minDmg();
	}

	default int getMaxDmg() {
		return getDirectDamageInfo().maxDmg();
	}

	default int getMinDmg2() {
		return getDirectDamageInfo().minDmg2();
	}

	default int getMaxDmg2() {
		return getDirectDamageInfo().maxDmg2();
	}

	default int getDotDmg() {
		return getDotDamageInfo().dotDmg();
	}

	default int getNumTicks() {
		return getDotDamageInfo().numTicks();
	}

	default Duration getTickInterval() {
		if (isChanneled()) {
			return getCastTime().divideBy(getNumTicks());
		}
		return getDotDamageInfo().tickInterval();
	}

	default Duration getDotDuration() {
		return getTickInterval().multiplyBy(getNumTicks());
	}

	default Set<AttributeCondition> getConditions() {
		var result = new HashSet<AttributeCondition>();
		result.add(AttributeCondition.of(getTalentTree()));
		result.add(AttributeCondition.of(getSpellSchool()));
		result.add(AttributeCondition.of(getSpellId()));
		return result;
	}

	default Duration getCooldown() {
		return getSpellInfo().getCooldown();
	}

	default Duration getEffectiveCooldown() {
		if (hasDotComponent()) {
			return getCooldown().max(getDotDuration());
		}
		return getCooldown();
	}
}

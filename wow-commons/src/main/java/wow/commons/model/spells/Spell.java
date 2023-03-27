package wow.commons.model.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
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
		return getId().getSpellId();
	}

	default Integer getRank() {
		return getId().getRank();
	}

	default TalentTree getTalentTree() {
		return getSpellInfo().getTalentTree();
	}

	default SpellSchool getSpellSchool() {
		return getSpellInfo().getSpellSchool();
	}

	default Percent getCoeffDirect() {
		return getSpellInfo().getDamagingSpellInfo().getCoeffDirect();
	}

	default Percent getCoeffDot() {
		return getSpellInfo().getDamagingSpellInfo().getCoeffDot();
	}

	default int getManaCost() {
		return getCastInfo().getManaCost();
	}

	default Duration getCastTime() {
		return getCastInfo().getCastTime();
	}

	default boolean isChanneled() {
		return getCastInfo().isChanneled();
	}

	default AdditionalCost getAdditionalCost() {
		return getCastInfo().getAdditionalCost();
	}

	default AppliedEffect getAppliedEffect() {
		return getCastInfo().getAppliedEffect();
	}

	default boolean hasDirectComponent() {
		return getDirectDamageInfo() != null;
	}

	default boolean hasDotComponent() {
		return getDotDamageInfo() != null;
	}

	default int getMinDmg() {
		return getDirectDamageInfo().getMinDmg();
	}

	default int getMaxDmg() {
		return getDirectDamageInfo().getMaxDmg();
	}

	default int getMinDmg2() {
		return getDirectDamageInfo().getMinDmg2();
	}

	default int getMaxDmg2() {
		return getDirectDamageInfo().getMaxDmg2();
	}

	default int getDotDmg() {
		return getDotDamageInfo().getDotDmg();
	}

	default int getNumTicks() {
		return getDotDamageInfo().getNumTicks();
	}

	default Duration getTickInterval() {
		if (isChanneled()) {
			return getCastTime().divideBy(getNumTicks());
		}
		return getDotDamageInfo().getTickInterval();
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
}

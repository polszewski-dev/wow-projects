package wow.commons.model.spells;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.ConfigurationElement;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.talents.TalentTree;

import java.util.HashSet;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-09-19
 */
@Getter
public class Spell extends ConfigurationElement<SpellIdAndRank> {
	@NonNull
	private final SpellInfo spellInfo;
	@NonNull
	private final CastInfo castInfo;
	private final DirectDamageInfo directDamageInfo;
	private final DotDamageInfo dotDamageInfo;

	public Spell(
			SpellIdAndRank id,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Description description,
			SpellInfo spellInfo,
			CastInfo castInfo,
			DirectDamageInfo directDamageInfo,
			DotDamageInfo dotDamageInfo
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.spellInfo = spellInfo;
		this.castInfo = castInfo;
		this.directDamageInfo = directDamageInfo;
		this.dotDamageInfo = dotDamageInfo;
	}

	public SpellId getSpellId() {
		return getId().getSpellId();
	}

	public Integer getRank() {
		return getId().getRank();
	}

	public TalentTree getTalentTree() {
		return spellInfo.getTalentTree();
	}

	public SpellSchool getSpellSchool() {
		return spellInfo.getSpellSchool();
	}

	public Percent getCoeffDirect() {
		return spellInfo.getDamagingSpellInfo().getCoeffDirect();
	}

	public Percent getCoeffDot() {
		return spellInfo.getDamagingSpellInfo().getCoeffDot();
	}

	public int getManaCost() {
		return castInfo.getManaCost();
	}

	public Duration getCastTime() {
		return castInfo.getCastTime();
	}

	public boolean isChanneled() {
		return castInfo.isChanneled();
	}

	public AdditionalCost getAdditionalCost() {
		return castInfo.getAdditionalCost();
	}

	public AppliedEffect getAppliedEffect() {
		return castInfo.getAppliedEffect();
	}

	public boolean hasDirectComponent() {
		return directDamageInfo != null;
	}

	public boolean hasDotComponent() {
		return dotDamageInfo != null;
	}

	public int getMinDmg() {
		return directDamageInfo.getMinDmg();
	}

	public int getMaxDmg() {
		return directDamageInfo.getMaxDmg();
	}

	public int getMinDmg2() {
		return directDamageInfo.getMinDmg2();
	}

	public int getMaxDmg2() {
		return directDamageInfo.getMaxDmg2();
	}

	public int getDotDmg() {
		return dotDamageInfo.getDotDmg();
	}

	public int getNumTicks() {
		return dotDamageInfo.getNumTicks();
	}

	public Duration getTickInterval() {
		if (isChanneled()) {
			return getCastTime().divideBy(getNumTicks());
		}
		return dotDamageInfo.getTickInterval();
	}

	public Duration getDotDuration() {
		return getTickInterval().multiplyBy(getNumTicks());
	}

	public Set<AttributeCondition> getConditions() {
		var result = new HashSet<AttributeCondition>();
		result.add(AttributeCondition.of(getTalentTree()));
		result.add(AttributeCondition.of(getSpellSchool()));
		result.add(AttributeCondition.of(getSpellId()));
		return result;
	}

	@Override
	public String toString() {
		return getId().toString();
	}
}

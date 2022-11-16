package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2021-09-19
 */
@AllArgsConstructor
@Getter
public class Spell {
	private final SpellInfo spellInfo;
	private final SpellRankInfo spellRankInfo;

	public SpellId getSpellId() {
		return spellInfo.getSpellId();
	}

	public String getName() {
		return spellInfo.getSpellId().getName();
	}

	public TalentTree getTalentTree() {
		return getSpellInfo().getTalentTree();
	}

	public SpellSchool getSpellSchool() {
		return getSpellInfo().getSpellSchool();
	}

	public int getMinDmg() {
		return spellRankInfo.getMinDmg();
	}

	public int getMaxDmg() {
		return spellRankInfo.getMaxDmg();
	}

	public int getDoTDamage() {
		return spellRankInfo.getDotDmg();
	}

	public boolean hasDirectComponent() {
		return getMinDmg() != 0 && getMaxDmg() != 0;
	}

	public boolean hasDoTComponent() {
		return getDoTDamage() != 0;
	}

	public Duration getCastTime() {
		return spellRankInfo.getCastTime();
	}
}

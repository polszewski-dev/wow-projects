package wow.minmax.model;

import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellRankInfo;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2021-09-19
 */
public class Spell {
	private final SpellInfo spellInfo;

	public Spell(SpellInfo spellInfo) {
		this.spellInfo = spellInfo;
	}

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

	public int getMinDmg(int level) {
		return getSpellRankInfo(level).getMinDmg();
	}

	public int getMaxDmg(int level) {
		return getSpellRankInfo(level).getMaxDmg();
	}

	public int getDoTDamage(int level) {
		return getSpellRankInfo(level).getDotDmg();
	}

	public SpellInfo getSpellInfo() {
		return spellInfo;
	}

	public SpellRankInfo getSpellRankInfo(int level) {
		return getSpellInfo().getHighestRank(level);
	}

	public boolean hasDirectComponent(int level) {
		return getMinDmg(level) != 0 && getMaxDmg(level) != 0;
	}

	public boolean hasDoTComponent(int level) {
		return getDoTDamage(level) != 0;
	}
}
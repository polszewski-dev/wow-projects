package wow.commons.repository;

import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface SpellDataRepository {
	Optional<Spell> getSpell(SpellId spellId, Integer rank);
	Optional<Spell> getSpellHighestRank(SpellId spellId, int level);
	List<Spell> getAllSpellRanks(SpellId spellId);
	Optional<Talent> getTalent(TalentId talentId, int rank);
	Optional<Talent> getTalent(int talentCalculatorPosition, int rank);
	Optional<Buff> getBuff(int buffId);
	Optional<Buff> getHighestRankBuff(String name, int level);
	List<Buff> getAvailableBuffs();
	List<Buff> getBuffs(Collection<String> buffNames);
	List<Buff> getBuffs(BuffExclusionGroup exclusionGroup);
}

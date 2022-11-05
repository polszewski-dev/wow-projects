package wow.commons.repository;

import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.effects.EffectId;
import wow.commons.model.effects.EffectInfo;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface SpellDataRepository {
	Optional<SpellInfo> getSpellInfo(SpellId spellId);
	Optional<TalentInfo> getTalentInfo(TalentId talentId, Integer rank);
	Optional<EffectInfo> getEffectInfo(EffectId effectId);
	Optional<Buff> getBuff(int buffId);
	Optional<Buff> getHighestRankBuff(String name, int level);
	List<Buff> getAvailableBuffs();
	List<Buff> getBuffs(Collection<String> buffNames);
	List<Buff> getBuffs(BuffExclusionGroup exclusionGroup);
}

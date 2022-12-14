package wow.minmax.service;

import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
public interface SpellService {
	Spell getSpellHighestRank(SpellId spellId, CharacterInfo characterInfo);

	List<Spell> getSpellHighestRanks(List<SpellId> spellIds, CharacterInfo characterInfo);

	Buff getBuff(int buffId, Phase phase);

	List<Buff> getBuffs(List<String> buffNames, CharacterInfo characterInfo);

	List<Buff> getBuffs(CharacterInfo characterInfo);

	Map<TalentId, Talent> getTalentsFromTalentLink(String link, CharacterInfo characterInfo);
}

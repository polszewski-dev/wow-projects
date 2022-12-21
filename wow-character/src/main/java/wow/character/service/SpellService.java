package wow.character.service;

import wow.character.model.character.Character;
import wow.commons.model.buffs.Buff;
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
	Spell getSpellHighestRank(SpellId spellId, Character character);

	List<Spell> getSpellHighestRanks(List<SpellId> spellIds, Character character);

	Buff getBuff(int buffId, Phase phase);

	List<Buff> getBuffs(List<String> buffNames, Character character);

	List<Buff> getBuffs(Character character);

	Map<TalentId, Talent> getTalentsFromTalentLink(String link, Character character);
}

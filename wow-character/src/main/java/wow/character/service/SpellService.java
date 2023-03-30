package wow.character.service;

import wow.character.model.character.Character;
import wow.commons.model.buffs.Buff;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
public interface SpellService {
	Spell getSpellHighestRank(SpellId spellId, Character character);

	List<Spell> getSpellHighestRanks(List<SpellId> spellIds, Character character);

	Buff getBuff(int buffId, PhaseId phaseId);

	List<Buff> getBuffs(List<String> buffNames, Character character);

	List<Buff> getBuffs(Character character);

	Talent getTalent(int position, int talentRank, Character character);
}

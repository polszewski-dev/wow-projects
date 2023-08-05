package wow.character.service;

import wow.character.model.character.BuffListType;
import wow.character.model.character.Character;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
public interface SpellService {
	List<Spell> getAvailableSpells(Character character);

	List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId);

	List<Buff> getAvailableBuffs(Character character, BuffListType buffListType);
}

package wow.commons.repository;

import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface SpellRepository {
	List<Spell> getAvailableSpells(CharacterClassId characterClassId, int level, PhaseId phaseId);

	Optional<Spell> getSpellHighestRank(SpellId spellId, int level, PhaseId phaseId);

	Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId);

	Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId);

	Optional<Buff> getBuff(int buffId, PhaseId phaseId);

	Optional<Buff> getBuff(String buffName, PhaseId phaseId);

	List<Buff> getBuffs(PhaseId phaseId);
}

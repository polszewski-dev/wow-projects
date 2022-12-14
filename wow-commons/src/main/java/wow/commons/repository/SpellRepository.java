package wow.commons.repository;

import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface SpellRepository {
	Optional<Spell> getSpellHighestRank(SpellId spellId, int level, Phase phase);

	Optional<Talent> getTalent(CharacterClass characterClass, int talentCalculatorPosition, int rank, Phase phase);

	Optional<Buff> getBuff(int buffId, Phase phase);

	Optional<Buff> getBuff(String buffName, Phase phase);

	List<Buff> getBuffs(Phase phase);
}

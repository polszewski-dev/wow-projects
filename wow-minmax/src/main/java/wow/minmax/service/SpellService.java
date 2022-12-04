package wow.minmax.service;

import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.minmax.model.PlayerProfile;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
public interface SpellService {
	Spell getSpell(SpellId spellId, int level);

	Optional<Spell> getAvailableSpellHighestRank(SpellId spellId, CharacterInfo characterInfo);

	List<Spell> getAvailableSpellsHighestRanks(List<SpellId> spellIds, CharacterInfo characterInfo);

	List<Buff> getAvailableBuffs(List<String> buffNames, CharacterInfo characterInfo);

	List<Buff> getAvailableBuffs(PlayerProfile playerProfile);
}

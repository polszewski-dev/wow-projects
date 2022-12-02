package wow.minmax.service;

import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.minmax.model.PlayerProfile;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
public interface SpellService {
	Spell getSpell(SpellId spellId, int level);

	List<Buff> getAvailableBuffs(PlayerProfile playerProfile);
}

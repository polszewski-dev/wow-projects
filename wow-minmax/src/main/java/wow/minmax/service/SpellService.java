package wow.minmax.service;

import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.SpellId;
import wow.minmax.model.Spell;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
public interface SpellService {
	Spell getSpell(SpellId spellId);

	List<Buff> getAvailableBuffs();
}

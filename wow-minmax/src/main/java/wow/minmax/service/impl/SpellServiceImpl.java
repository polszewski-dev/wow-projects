package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.model.Spell;
import wow.minmax.service.SpellService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
@Service
@AllArgsConstructor
public class SpellServiceImpl implements SpellService {
	private final SpellDataRepository spellDataRepository;

	@Override
	public Spell getSpell(SpellId spellId) {
		return new Spell(spellDataRepository.getSpellInfo(spellId));
	}

	@Override
	public List<Buff> getAvailableBuffs() {
		return spellDataRepository.getAvailableBuffs();
	}
}

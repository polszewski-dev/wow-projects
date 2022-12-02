package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.SpellService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
@Service
@AllArgsConstructor
public class SpellServiceImpl implements SpellService {
	private final SpellDataRepository spellDataRepository;

	@Override
	public Spell getSpell(SpellId spellId, int level) {
		return spellDataRepository.getSpellHighestRank(spellId, level).orElseThrow();
	}

	@Override
	public List<Buff> getAvailableBuffs(PlayerProfile playerProfile) {
		return spellDataRepository.getAvailableBuffs().stream()
				.filter(buff -> buff.getRestriction().isMetBy(playerProfile.getCharacterInfo()))
				.collect(Collectors.toList());
	}
}

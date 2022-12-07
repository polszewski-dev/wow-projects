package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.SpellService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
	public Optional<Spell> getAvailableSpellHighestRank(SpellId spellId, CharacterInfo characterInfo) {
		return spellDataRepository.getAllSpellRanks(spellId).stream()
				.filter(spell -> spell.isAvailableDuring(characterInfo.getPhase()))
				.filter(spell -> spell.isAvailableTo(characterInfo))
				.max(Comparator.comparing(Spell::getRank));
	}

	@Override
	public List<Spell> getAvailableSpellsHighestRanks(List<SpellId> spellIds, CharacterInfo characterInfo) {
		return spellIds.stream()
				.map(spellId -> getAvailableSpellHighestRank(spellId, characterInfo))
				.map(optionalSpell -> optionalSpell.orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public List<Buff> getAvailableBuffs(List<String> buffNames, CharacterInfo characterInfo) {
		return spellDataRepository.getBuffs(buffNames).stream()
				.filter(buff -> buff.isAvailableDuring(characterInfo.getPhase()))
				.filter(buff -> buff.isAvailableTo(characterInfo))
				.collect(Collectors.toList());
	}

	@Override
	public List<Buff> getAvailableBuffs(PlayerProfile playerProfile) {
		return spellDataRepository.getAvailableBuffs().stream()
				.filter(buff -> buff.isAvailableDuring(playerProfile.getPhase()))
				.filter(buff -> buff.isAvailableTo(playerProfile.getCharacterInfo()))
				.collect(Collectors.toList());
	}
}

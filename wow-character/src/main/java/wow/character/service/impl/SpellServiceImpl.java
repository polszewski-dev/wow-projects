package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Character;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.repository.SpellRepository;

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
	private final SpellRepository spellRepository;

	@Override
	public Spell getSpellHighestRank(SpellId spellId, Character character) {
		return getSpellHighestRankFilteredByCharacter(spellId, character).orElseThrow();
	}

	@Override
	public List<Spell> getSpellHighestRanks(List<SpellId> spellIds, Character character) {
		return spellIds.stream()
				.map(spellId -> getSpellHighestRankFilteredByCharacter(spellId, character).orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public Buff getBuff(int buffId, Phase phase) {
		return spellRepository.getBuff(buffId, phase).orElseThrow();
	}

	private Optional<Spell> getSpellHighestRankFilteredByCharacter(SpellId spellId, Character character) {
		return spellRepository.getSpellHighestRank(spellId, character.getLevel(), character.getPhase())
				.filter(spell -> spell.isAvailableTo(character));
	}

	@Override
	public List<Buff> getBuffs(List<String> buffNames, Character character) {
		return buffNames.stream()
				.map(buffName -> spellRepository.getBuff(buffName, character.getPhase()).orElse(null))
				.filter(Objects::nonNull)
				.filter(buff -> buff.isAvailableTo(character))
				.collect(Collectors.toList());
	}

	@Override
	public List<Buff> getBuffs(Character character) {
		return spellRepository.getBuffs(character.getPhase()).stream()
				.filter(buff -> buff.isAvailableTo(character))
				.collect(Collectors.toList());
	}

	@Override
	public Talent getTalent(int position, int talentRank, Character character) {
		return spellRepository.getTalent(character.getCharacterClass(), position, talentRank, character.getPhase()).orElseThrow();
	}
}

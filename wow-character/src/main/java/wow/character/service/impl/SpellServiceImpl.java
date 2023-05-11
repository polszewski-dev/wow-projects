package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Character;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;
import wow.commons.repository.SpellRepository;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
@Service
@AllArgsConstructor
public class SpellServiceImpl implements SpellService {
	private final SpellRepository spellRepository;

	@Override
	public List<Spell> getAvailableSpells(Character character) {
		return spellRepository.getAvailableSpells(character.getCharacterClassId(), character.getLevel(), character.getPhaseId()).stream()
				.filter(spell -> spell.isAvailableTo(character))
				.toList();
	}

	@Override
	public Buff getBuff(int buffId, PhaseId phaseId) {
		return spellRepository.getBuff(buffId, phaseId).orElseThrow();
	}

	@Override
	public List<Buff> getBuffs(List<String> buffNames, Character character) {
		return buffNames.stream()
				.map(buffName -> spellRepository.getBuff(buffName, character.getPhaseId()).orElse(null))
				.filter(Objects::nonNull)
				.filter(buff -> buff.isAvailableTo(character))
				.toList();
	}

	@Override
	public List<Buff> getBuffs(Character character) {
		return spellRepository.getBuffs(character.getPhaseId()).stream()
				.filter(buff -> buff.isAvailableTo(character))
				.toList();
	}

	@Override
	public Talent getTalent(int position, int talentRank, Character character) {
		return spellRepository.getTalent(character.getCharacterClassId(), position, talentRank, character.getPhaseId()).orElseThrow();
	}
}

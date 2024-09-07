package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.character.model.character.Character;
import wow.character.service.SpellService;
import wow.commons.model.buff.Buff;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.Talent;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.repository.spell.TalentRepository;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
@Service
@AllArgsConstructor
public class SpellServiceImpl implements SpellService {
	private final SpellRepository spellRepository;
	private final TalentRepository talentRepository;
	private final BuffRepository buffRepository;

	@Override
	public List<Ability> getAvailableAbilities(Character character) {
		return spellRepository.getAvailableAbilities(character.getCharacterClassId(), character.getLevel(), character.getPhaseId()).stream()
				.filter(spell -> spell.isAvailableTo(character))
				.toList();
	}

	@Override
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentRepository.getAvailableTalents(characterClassId, phaseId);
	}

	@Override
	public List<Buff> getAvailableBuffs(Character character, BuffListType buffListType) {
		return buffRepository.getAvailableBuffs(character.getPhaseId()).stream()
				.filter(buff -> buff.isAvailableTo(character))
				.filter(buffListType.getFilter())
				.toList();
	}
}

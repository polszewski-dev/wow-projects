package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
@Service
@AllArgsConstructor
public class CharacterServiceImpl implements CharacterService {
	private final CharacterRepository characterRepository;

	@Override
	public BaseStatInfo getBaseStats(Character character) {
		return characterRepository.getBaseStats(
				character.getCharacterClass(), character.getRace(), character.getLevel(), character.getPhase()
		).orElseThrow();
	}

	@Override
	public CombatRatingInfo getCombatRatings(Character character) {
		return characterRepository.getCombatRatings(character.getLevel(), character.getPhase()).orElseThrow();
	}

	@Override
	public BuildTemplate getBuildTemplate(BuildId buildId, Character character) {
		return characterRepository.getBuildTemplate(
				buildId, character.getCharacterClass(), character.getLevel(), character.getPhase()
		).orElseThrow();
	}
}

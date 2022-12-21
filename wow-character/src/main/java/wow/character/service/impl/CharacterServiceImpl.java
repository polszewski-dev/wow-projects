package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CharacterInfo;
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
	public BaseStatInfo getBaseStats(CharacterInfo characterInfo) {
		return characterRepository.getBaseStats(
				characterInfo.getCharacterClass(), characterInfo.getRace(), characterInfo.getLevel(), characterInfo.getPhase()
		).orElseThrow();
	}

	@Override
	public CombatRatingInfo getCombatRatings(CharacterInfo characterInfo) {
		return characterRepository.getCombatRatings(characterInfo.getLevel(), characterInfo.getPhase()).orElseThrow();
	}

	@Override
	public BuildTemplate getBuildTemplate(BuildId buildId, CharacterInfo characterInfo) {
		return characterRepository.getBuildTemplate(
				buildId, characterInfo.getCharacterClass(), characterInfo.getLevel(), characterInfo.getPhase()
		).orElseThrow();
	}
}

package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.PhaseConverter;
import wow.commons.client.dto.EnemyTypeDTO;
import wow.commons.client.dto.LevelDifferenceDTO;
import wow.commons.model.character.CreatureType;
import wow.minmax.client.dto.CharacterSelectionOptionsDTO;
import wow.minmax.model.CharacterSelectionOptions;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
@Component
@AllArgsConstructor

public class CharacterSelectionOptionsConverter implements Converter<CharacterSelectionOptions, CharacterSelectionOptionsDTO> {
	private final PhaseConverter phaseConverter;

	@Override
	public CharacterSelectionOptionsDTO doConvert(CharacterSelectionOptions source) {
		return new CharacterSelectionOptionsDTO(
				phaseConverter.convertList(source.phases()),
				getEnemyTypes(source),
				getEnemyLevelDiffs(source)
		);
	}

	private List<EnemyTypeDTO> getEnemyTypes(CharacterSelectionOptions source) {
		return source.enemyTypes().stream()
				.map(this::getEnemyTypeDTO)
				.toList();
	}

	private EnemyTypeDTO getEnemyTypeDTO(CreatureType creatureType) {
		return new EnemyTypeDTO(
				creatureType.toString(),
				StringUtils.capitalize(creatureType.toString())
		);
	}

	private List<LevelDifferenceDTO> getEnemyLevelDiffs(CharacterSelectionOptions source) {
		return source.enemyLevelDiffs().stream()
				.map(this::getLevelDifferenceDTO)
				.toList();
	}

	private LevelDifferenceDTO getLevelDifferenceDTO(int levelDiff) {
		return new LevelDifferenceDTO(levelDiff, getLevelDifferenceName(levelDiff));
	}

	private String getLevelDifferenceName(int levelDiff) {
		if (levelDiff == 3) {
			return "+3 (Boss)";
		}
		return "%+d".formatted(levelDiff);
	}
}

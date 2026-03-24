package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.dto.NonPlayerDTO;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@AllArgsConstructor
public abstract class AbstractNonPlayerConverter<N extends NonPlayerCharacter> implements Converter<N, NonPlayerDTO>, BackConverter<N, NonPlayerDTO> {
	private final CharacterService characterService;

	@Override
	public NonPlayerDTO doConvert(N source) {
		return new NonPlayerDTO(
				source.getName(),
				source.getCreatureType(),
				source.getLevel(),
				source.getPhaseId()
		);
	}

	@Override
	public N doConvertBack(NonPlayerDTO source) {
		return characterService.createNonPlayerCharacter(
				source.name(),
				source.enemyType(),
				source.enemyLevel(),
				source.phaseId(),
				getFactory(source.name())
		);
	}

	protected abstract NonPlayerCharacterFactory<N> getFactory(String name);
}

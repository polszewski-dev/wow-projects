package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.model.pve.PhaseId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@AllArgsConstructor
public abstract class AbstractNonPlayerConverter<N extends NonPlayerCharacter> implements Converter<N, NonPlayerDTO>, ParametrizedBackConverter<N, NonPlayerDTO, PhaseId> {
	private final CharacterService characterService;

	private final BuffConverter buffConverter;

	@Override
	public NonPlayerDTO doConvert(N source) {
		return new NonPlayerDTO(
				source.getName(),
				source.getCreatureType(),
				source.getLevel(),
				buffConverter.convertList(source.getBuffs().getList()),
				List.of()
		);
	}

	@Override
	public N doConvertBack(NonPlayerDTO source, PhaseId phaseId) {
		return characterService.createNonPlayerCharacter(
				source.name(),
				source.enemyType(),
				source.enemyLevel(),
				phaseId,
				getFactory(source.name())
		);
	}

	protected abstract NonPlayerCharacterFactory<N> getFactory(String name);
}

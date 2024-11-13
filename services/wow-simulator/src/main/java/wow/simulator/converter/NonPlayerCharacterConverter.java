package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.service.CharacterService;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.simulator.client.dto.EnemyDTO;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
@AllArgsConstructor
public class NonPlayerCharacterConverter implements ParametrizedConverter<EnemyDTO, NonPlayerCharacter, PhaseId> {
	private final CharacterService characterService;

	@Override
	public NonPlayerCharacter doConvert(EnemyDTO source, PhaseId phaseId) {
		return characterService.createNonPlayerCharacter(
				source.enemyType(),
				source.enemyLevel(),
				phaseId
		);
	}
}

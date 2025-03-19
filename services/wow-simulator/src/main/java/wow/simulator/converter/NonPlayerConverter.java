package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.model.pve.PhaseId;
import wow.simulator.model.unit.NonPlayer;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
@AllArgsConstructor
public class NonPlayerConverter implements ParametrizedConverter<NonPlayerDTO, NonPlayer, PhaseId> {
	private final CharacterService characterService;

	@Override
	public NonPlayer doConvert(NonPlayerDTO source, PhaseId phaseId) {
		return characterService.createNonPlayerCharacter(
				source.enemyType(),
				source.enemyLevel(),
				phaseId,
				NonPlayer.getFactory(source.name())
		);
	}
}

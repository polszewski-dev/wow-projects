package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Phase;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.PhaseDTO;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class PhaseConverter implements Converter<Phase, PhaseDTO> {
	@Override
	public PhaseDTO doConvert(Phase source) {
		return new PhaseDTO(source.getPhaseId(), source.getName(), source.getMaxLevel());
	}
}

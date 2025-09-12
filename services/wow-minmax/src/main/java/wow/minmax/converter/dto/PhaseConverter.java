package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.pve.Phase;
import wow.minmax.client.dto.PhaseDTO;

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

package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.RotationStatsDTO;
import wow.minmax.model.RotationStats;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@Component
@AllArgsConstructor
public class RotationStatsConverter implements Converter<RotationStats, RotationStatsDTO> {
	private final RotationSpellStatsConverter rotationSpellStatsConverter;

	@Override
	public RotationStatsDTO doConvert(RotationStats source) {
		return new RotationStatsDTO(
				rotationSpellStatsConverter.convertList(source.getStatList()),
				source.getDps(),
				source.getTotalDamage()
		);
	}
}
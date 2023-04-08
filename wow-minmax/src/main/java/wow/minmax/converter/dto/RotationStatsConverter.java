package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.model.RotationStats;
import wow.minmax.model.dto.RotationStatsDTO;

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

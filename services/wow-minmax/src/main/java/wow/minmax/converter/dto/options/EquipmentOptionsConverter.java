package wow.minmax.converter.dto.options;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.options.EquipmentOptionsDTO;
import wow.minmax.model.options.EquipmentOptions;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class EquipmentOptionsConverter implements Converter<EquipmentOptions, EquipmentOptionsDTO> {
	@Override
	public EquipmentOptionsDTO doConvert(EquipmentOptions source) {
		return new EquipmentOptionsDTO(
				source.editGems(),
				source.heroics()
		);
	}
}

package wow.minmax.converter.dto.options;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.equipment.GemConverter;
import wow.minmax.client.dto.options.GemOptionsDTO;
import wow.minmax.model.options.GemOptions;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class GemOptionsConverter implements Converter<GemOptions, GemOptionsDTO> {
	private final GemConverter gemConverter;

	@Override
	public GemOptionsDTO doConvert(GemOptions source) {
		return new GemOptionsDTO(
				source.socketType(),
				gemConverter.convertList(source.gems())
		);
	}
}

package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.BuffConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.BuffStatusDTO;
import wow.minmax.model.BuffStatus;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
@Component
@AllArgsConstructor
public class BuffStatusConverter implements Converter<BuffStatus, BuffStatusDTO> {
	private final BuffConverter buffConverter;

	@Override
	public BuffStatusDTO doConvert(BuffStatus source) {
		return new BuffStatusDTO(
			buffConverter.convert(source.buff()),
			source.enabled()
		);
	}
}

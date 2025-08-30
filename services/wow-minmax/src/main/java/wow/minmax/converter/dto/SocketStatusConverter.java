package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.SocketStatusDTO;
import wow.minmax.model.SocketStatus;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class SocketStatusConverter implements Converter<SocketStatus, SocketStatusDTO> {
	@Override
	public SocketStatusDTO doConvert(SocketStatus source) {
		return new SocketStatusDTO(
				source.socketNo(),
				source.socketType(),
				source.matching()
		);
	}
}

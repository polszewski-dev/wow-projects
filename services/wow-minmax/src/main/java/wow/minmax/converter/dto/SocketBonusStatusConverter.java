package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.util.AttributeFormatter;
import wow.commons.model.effect.Effect;
import wow.minmax.client.dto.SocketBonusStatusDTO;
import wow.minmax.model.SocketBonusStatus;

import static java.util.stream.Collectors.joining;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class SocketBonusStatusConverter implements Converter<SocketBonusStatus, SocketBonusStatusDTO> {
	@Override
	public SocketBonusStatusDTO doConvert(SocketBonusStatus source) {
		return new SocketBonusStatusDTO(
				getSocketBonusString(source.bonus()),
				source.enabled()
		);
	}

	private String getSocketBonusString(Effect socketBonus) {
		return socketBonus.getModifierAttributeList().stream()
				.map(AttributeFormatter::format)
				.collect(joining(", "));
	}
}

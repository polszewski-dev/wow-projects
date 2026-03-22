package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.minmax.client.dto.BuffDTO;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
@Component
public class BuffStatusConverter extends OptionStatusConverter<Buff, BuffDTO> {
	public BuffStatusConverter(BuffConverter converter) {
		super(converter);
	}

	@Override
	protected String getGroupKey(Buff source) {
		return source.getExclusionGroup() != null
				? source.getExclusionGroup().name()
				: source.getName();
	}
}

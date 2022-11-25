package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.buffs.Buff;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.BuffDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffConverter extends Converter<Buff, BuffDTO> {
	@Override
	protected BuffDTO doConvert(Buff buff) {
		return new BuffDTO(buff.getId(), buff.getName(), buff.statString(), buff.getIcon(), buff.getTooltip());
	}
}

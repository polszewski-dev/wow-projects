package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.GemDTO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemConverter extends Converter<Gem, GemDTO> {
	@Override
	protected GemDTO doConvert(Gem gem) {
		return new GemDTO(gem.getId(), gem.getName(), gem.getColor(), gem.getRarity(), gem.getAttributes().statString(), gem.getShorterName());
	}
}
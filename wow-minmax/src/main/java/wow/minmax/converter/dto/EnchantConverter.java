package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EnchantDTO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantConverter extends Converter<Enchant, EnchantDTO> {
	@Override
	protected EnchantDTO doConvert(Enchant enchant) {
		return new EnchantDTO(enchant.getId(), enchant.getName(), enchant.getAttributes().statString());
	}
}

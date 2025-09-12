package wow.minmax.converter.dto.options;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.equipment.EnchantConverter;
import wow.minmax.client.dto.options.EnchantOptionsDTO;
import wow.minmax.model.options.EnchantOptions;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class EnchantOptionsConverter implements Converter<EnchantOptions, EnchantOptionsDTO> {
	private final EnchantConverter enchantConverter;

	@Override
	public EnchantOptionsDTO doConvert(EnchantOptions source) {
		return new EnchantOptionsDTO(
				source.itemType(),
				source.itemSubType(),
				enchantConverter.convertList(source.enchants())
		);
	}
}

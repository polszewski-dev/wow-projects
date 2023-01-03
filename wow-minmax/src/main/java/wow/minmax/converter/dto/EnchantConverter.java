package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.EnchantDTO;

import java.util.Map;

import static wow.minmax.converter.dto.DtoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantConverter implements Converter<Enchant, EnchantDTO>, ParametrizedBackConverter<Enchant, EnchantDTO> {
	private final ItemRepository itemRepository;

	@Override
	public EnchantDTO doConvert(Enchant enchant) {
		return new EnchantDTO(
				enchant.getId(),
				enchant.getName(),
				enchant.getRarity(),
				enchant.getAttributes().statString(),
				enchant.getIcon(),
				enchant.getTooltip()
		);
	}

	@Override
	public Enchant doConvertBack(EnchantDTO value, Map<String, Object> params) {
		return itemRepository.getEnchant(value.getId(), getPhase(params)).orElseThrow();
	}
}

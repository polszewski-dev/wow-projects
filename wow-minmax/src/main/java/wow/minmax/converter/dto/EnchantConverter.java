package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.dto.EnchantDTO;

import java.util.Map;

import static wow.minmax.converter.dto.DtoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantConverter extends ParametrizedConverter<Enchant, EnchantDTO> {
	private final ItemRepository itemRepository;

	@Override
	protected EnchantDTO doConvert(Enchant enchant, Map<String, Object> params) {
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
	protected Enchant doConvertBack(EnchantDTO value, Map<String, Object> params) {
		return itemRepository.getEnchant(value.getId(), getPhase(params)).orElseThrow();
	}
}

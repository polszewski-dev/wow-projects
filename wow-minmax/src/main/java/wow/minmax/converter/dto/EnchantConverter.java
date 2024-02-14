package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.EnchantDTO;

import java.util.Map;

import static wow.minmax.converter.dto.DtoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantConverter implements Converter<Enchant, EnchantDTO>, ParametrizedBackConverter<Enchant, EnchantDTO> {
	private final ItemRepository itemRepository;

	@Override
	public EnchantDTO doConvert(Enchant source) {
		return new EnchantDTO(
				source.getId(),
				source.getName(),
				source.getRarity(),
				source.getIcon(),
				getTooltip(source)
		);
	}

	@Override
	public Enchant doConvertBack(EnchantDTO source, Map<String, Object> params) {
		return itemRepository.getEnchant(source.getId(), getPhaseId(params)).orElseThrow();
	}

	private String getTooltip(Enchant source) {
		return source.getEffect().getTooltip();
	}
}

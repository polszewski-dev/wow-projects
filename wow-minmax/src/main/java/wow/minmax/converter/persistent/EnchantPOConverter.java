package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.EnchantPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantPOConverter implements Converter<Enchant, EnchantPO>, ParametrizedBackConverter<Enchant, EnchantPO> {
	private final ItemRepository itemRepository;

	@Override
	public EnchantPO doConvert(Enchant enchant) {
		return new EnchantPO(enchant.getId(), enchant.getName());
	}

	@Override
	public Enchant doConvertBack(EnchantPO value, Map<String, Object> params) {
		return itemRepository.getEnchant(value.getId(), getPhase(params)).orElseThrow();
	}
}

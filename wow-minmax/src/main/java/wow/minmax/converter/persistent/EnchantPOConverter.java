package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.EnchantPO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantPOConverter extends Converter<Enchant, EnchantPO> {
	private final ItemDataRepository itemDataRepository;

	@Override
	protected EnchantPO doConvert(Enchant enchant) {
		return new EnchantPO(enchant.getId(), enchant.getName());
	}

	@Override
	protected Enchant doConvertBack(EnchantPO value) {
		return itemDataRepository.getEnchant(value.getId()).orElseThrow();
	}
}

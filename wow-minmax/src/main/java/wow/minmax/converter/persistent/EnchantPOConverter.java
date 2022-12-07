package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.persistent.EnchantPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantPOConverter extends ParametrizedConverter<Enchant, EnchantPO> {
	private final ItemDataRepository itemDataRepository;

	@Override
	protected EnchantPO doConvert(Enchant enchant, Map<String, Object> params) {
		return new EnchantPO(enchant.getId(), enchant.getName());
	}

	@Override
	protected Enchant doConvertBack(EnchantPO value, Map<String, Object> params) {
		return itemDataRepository.getEnchant(value.getId()).orElseThrow();
	}
}

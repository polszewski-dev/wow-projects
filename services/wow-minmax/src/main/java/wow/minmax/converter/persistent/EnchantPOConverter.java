package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Enchant;
import wow.commons.repository.item.EnchantRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.EnchantPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantPOConverter implements Converter<Enchant, EnchantPO>, ParametrizedBackConverter<Enchant, EnchantPO> {
	private final EnchantRepository enchantRepository;

	@Override
	public EnchantPO doConvert(Enchant source) {
		return new EnchantPO(source.getId(), source.getName());
	}

	@Override
	public Enchant doConvertBack(EnchantPO source, Map<String, Object> params) {
		return enchantRepository.getEnchant(source.getId(), getPhaseId(params)).orElseThrow();
	}
}

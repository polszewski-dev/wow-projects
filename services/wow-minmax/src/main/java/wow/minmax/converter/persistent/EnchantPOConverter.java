package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Enchant;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.EnchantRepository;
import wow.minmax.model.persistent.EnchantPO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantPOConverter implements Converter<Enchant, EnchantPO>, ParametrizedBackConverter<Enchant, EnchantPO, PhaseId> {
	private final EnchantRepository enchantRepository;

	@Override
	public EnchantPO doConvert(Enchant source) {
		return new EnchantPO(source.getId(), source.getName());
	}

	@Override
	public Enchant doConvertBack(EnchantPO source, PhaseId phaseId) {
		return enchantRepository.getEnchant(source.getId(), phaseId).orElseThrow();
	}
}

package wow.minmax.converter.model.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.EnchantId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.EnchantRepository;
import wow.minmax.model.equipment.EnchantConfig;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantConfigConverter implements Converter<Enchant, EnchantConfig>, ParametrizedBackConverter<Enchant, EnchantConfig, PhaseId> {
	private final EnchantRepository enchantRepository;

	@Override
	public EnchantConfig doConvert(Enchant source) {
		return new EnchantConfig(source.getId().value(), source.getName());
	}

	@Override
	public Enchant doConvertBack(EnchantConfig source, PhaseId phaseId) {
		var enchantId = EnchantId.of(source.getId());

		return enchantRepository.getEnchant(enchantId, phaseId).orElseThrow();
	}
}

package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Enchant;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.EnchantRepository;
import wow.minmax.client.dto.equipment.EnchantDTO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EnchantConverter implements Converter<Enchant, EnchantDTO>, ParametrizedBackConverter<Enchant, EnchantDTO, PhaseId> {
	private final EnchantRepository enchantRepository;

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
	public Enchant doConvertBack(EnchantDTO source, PhaseId phaseId) {
		return enchantRepository.getEnchant(source.id(), phaseId).orElseThrow();
	}

	private String getTooltip(Enchant source) {
		return source.getEffect().getTooltip();
	}
}

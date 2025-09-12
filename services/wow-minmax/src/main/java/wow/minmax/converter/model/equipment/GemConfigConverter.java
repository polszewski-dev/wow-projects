package wow.minmax.converter.model.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.GemRepository;
import wow.minmax.model.equipment.GemConfig;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemConfigConverter implements Converter<Gem, GemConfig>, ParametrizedBackConverter<Gem, GemConfig, PhaseId> {
	private final GemRepository gemRepository;

	@Override
	public GemConfig doConvert(Gem source) {
		return new GemConfig(source.getId(), source.getName());
	}

	@Override
	public Gem doConvertBack(GemConfig source, PhaseId phaseId) {
		return gemRepository.getGem(source.getId(), phaseId).orElseThrow();
	}
}

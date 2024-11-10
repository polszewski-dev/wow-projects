package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.GemRepository;
import wow.minmax.model.persistent.GemPO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemPOConverter implements Converter<Gem, GemPO>, ParametrizedBackConverter<Gem, GemPO, PhaseId> {
	private final GemRepository gemRepository;

	@Override
	public GemPO doConvert(Gem source) {
		return new GemPO(source.getId(), source.getName());
	}

	@Override
	public Gem doConvertBack(GemPO source, PhaseId phaseId) {
		return gemRepository.getGem(source.getId(), phaseId).orElseThrow();
	}
}

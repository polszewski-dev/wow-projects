package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.commons.repository.item.GemRepository;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.GemPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemPOConverter implements Converter<Gem, GemPO>, ParametrizedBackConverter<Gem, GemPO> {
	private final GemRepository gemRepository;

	@Override
	public GemPO doConvert(Gem source) {
		return new GemPO(source.getId(), source.getName());
	}

	@Override
	public Gem doConvertBack(GemPO source, Map<String, Object> params) {
		return gemRepository.getGem(source.getId(), getPhaseId(params)).orElseThrow();
	}
}

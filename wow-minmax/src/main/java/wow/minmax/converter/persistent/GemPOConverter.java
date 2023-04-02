package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
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
	private final ItemRepository itemRepository;

	@Override
	public GemPO doConvert(Gem source) {
		return new GemPO(source.getId(), source.getName());
	}

	@Override
	public Gem doConvertBack(GemPO source, Map<String, Object> params) {
		return itemRepository.getGem(source.getId(), getPhaseId(params)).orElseThrow();
	}
}

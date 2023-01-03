package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.GemPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemPOConverter implements Converter<Gem, GemPO>, ParametrizedBackConverter<Gem, GemPO> {
	private final ItemRepository itemRepository;

	@Override
	public GemPO doConvert(Gem gem) {
		return new GemPO(gem.getId(), gem.getName());
	}

	@Override
	public Gem doConvertBack(GemPO value, Map<String, Object> params) {
		return itemRepository.getGem(value.getId(), getPhase(params)).orElseThrow();
	}
}

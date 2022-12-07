package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.persistent.GemPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemPOConverter extends ParametrizedConverter<Gem, GemPO> {
	private final ItemDataRepository itemDataRepository;

	@Override
	protected GemPO doConvert(Gem gem, Map<String, Object> params) {
		return new GemPO(gem.getId(), gem.getName());
	}

	@Override
	protected Gem doConvertBack(GemPO value, Map<String, Object> params) {
		return itemDataRepository.getGem(value.getId()).orElseThrow();
	}
}

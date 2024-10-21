package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.BuffPO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffPOConverter implements Converter<Buff, BuffPO> {
	@Override
	public BuffPO doConvert(Buff source) {
		return new BuffPO(source.getBuffId(), source.getRank(), source.getName());
	}
}

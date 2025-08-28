package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.buff.Buff;
import wow.minmax.model.BuffConfig;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffConfigConverter implements Converter<Buff, BuffConfig> {
	@Override
	public BuffConfig doConvert(Buff source) {
		return new BuffConfig(source.getBuffId(), source.getRank(), source.getName());
	}
}

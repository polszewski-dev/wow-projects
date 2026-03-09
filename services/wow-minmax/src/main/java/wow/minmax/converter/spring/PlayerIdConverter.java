package wow.minmax.converter.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import wow.minmax.model.PlayerId;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
public class PlayerIdConverter implements Converter<String, PlayerId> {
	@Override
	public PlayerId convert(String source) {
		return PlayerId.parse(source);
	}
}

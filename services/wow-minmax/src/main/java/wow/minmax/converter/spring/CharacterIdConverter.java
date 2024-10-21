package wow.minmax.converter.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import wow.minmax.model.CharacterId;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
public class CharacterIdConverter implements Converter<String, CharacterId> {
	@Override
	public CharacterId convert(String source) {
		return CharacterId.parse(source);
	}
}

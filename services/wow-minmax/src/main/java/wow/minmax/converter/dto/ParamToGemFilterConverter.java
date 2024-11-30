package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.GemFilter;
import wow.commons.client.converter.Converter;

import java.util.Map;

import static java.lang.Boolean.parseBoolean;

/**
 * User: POlszewski
 * Date: 2024-11-28
 */
@Component
@AllArgsConstructor
public class ParamToGemFilterConverter implements Converter<Map<String, String>, GemFilter> {
	@Override
	public GemFilter doConvert(Map<String, String> source) {
		var result = GemFilter.empty();

		source.forEach((key, value) -> {
			if (key.equals("uniqueGems")) {
				result.setUnique(parseBoolean(value));
			}
		});

		return result;
	}
}

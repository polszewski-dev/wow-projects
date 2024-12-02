package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.ItemFilter;
import wow.commons.client.converter.Converter;

import java.util.Map;

import static java.lang.Boolean.parseBoolean;

/**
 * User: POlszewski
 * Date: 2024-11-28
 */
@Component
@AllArgsConstructor
public class ParamToItemFilterConverter implements Converter<Map<String, String>, ItemFilter> {
	@Override
	public ItemFilter doConvert(Map<String, String> source) {
		var result = ItemFilter.empty();

		source.forEach((key, value) -> {
			switch (key) {
				case "heroics" -> result.setHeroics(parseBoolean(value));
				case "raids" -> result.setRaids(parseBoolean(value));
				case "worldBosses" -> result.setWorldBosses(parseBoolean(value));
				case "pvpItems" -> result.setPvpItems(parseBoolean(value));
				case "greens" -> result.setGreens(parseBoolean(value));
				case "legendaries" -> result.setLegendaries(parseBoolean(value));
				default -> {
					// ignore
				}
			}
		});

		return result;
	}
}

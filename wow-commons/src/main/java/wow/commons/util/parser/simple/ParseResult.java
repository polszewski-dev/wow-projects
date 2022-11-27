package wow.commons.util.parser.simple;

import lombok.Data;
import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.util.Map;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Data
public class ParseResult {
	private String type;
	private Map<String, String> map;

	public String getString(String key) {
		assertHasKey(key);
		return map.get(key);
	}

	public String getString(String key, String defaultValue) {
		String str = map.get(key);
		return str != null ? str : defaultValue;
	}

	public Integer getInteger(String key) {
		assertHasKey(key);
		return Integer.valueOf(map.get(key));
	}

	public Integer getInteger(String key, Integer defaultValue) {
		String str = map.get(key);
		return str != null ? Integer.valueOf(str) : defaultValue;
	}

	public Double getDouble(String key) {
		assertHasKey(key);
		return Double.valueOf(map.get(key));
	}

	public Double getDouble(String key, Double defaultValue) {
		String str = map.get(key);
		return str != null ? Double.valueOf(str) : defaultValue;
	}

	public Percent getPercent(String key) {
		assertHasKey(key);
		return Percent.parse(map.get(key));
	}

	public Percent getPercent(String key, Percent defaultValue) {
		Percent result = Percent.parse(map.get(key));
		return result != null ? result : defaultValue;
	}

	public Duration getDuration(String key) {
		assertHasKey(key);
		return Duration.parse(map.get(key));
	}

	public Duration getDuration(String key, Duration defaultValue) {
		Duration result = Duration.parse(map.get(key));
		return result != null ? result : defaultValue;
	}

	public <T> T getEnum(String key, Function<String, T> producer) {
		assertHasKey(key);
		return producer.apply(map.get(key));
	}

	public <T> T getEnum(String key, Function<String, T> producer, T defaultValue) {
		T result = producer.apply(map.get(key));
		return result != null ? result : defaultValue;
	}

	private void assertHasKey(String key) {
		if (!map.containsKey(key)) {
			throw new IllegalArgumentException("Missing key: " + key);
		}
	}
}

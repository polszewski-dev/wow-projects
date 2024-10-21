package wow.commons.util.parser.simple;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
class SimpleRecordMapperTest {
	@Test
	void toString1() {
		String result = SimpleRecordMapper.toString("Test", Map.of());
		assertThat(result).isEqualTo("Test -> ");
	}

	@Test
	void toString2() {
		String result = SimpleRecordMapper.toString("Test", Map.of("key", "value"));
		assertThat(result).isEqualTo("Test -> key=value");
	}

	@Test
	void toString3() {
		String result = SimpleRecordMapper.toString("Test", Map.of("key", 123));
		assertThat(result).isEqualTo("Test -> key=123");
	}

	@Test
	void toString4() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key1", "value");
		map.put("key2", 123);
		String result = SimpleRecordMapper.toString("Test", map);
		assertThat(result).isEqualTo("Test -> key1=value; key2=123");
	}

	@Test
	void toString5() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key1", null);
		map.put("key2", 123);
		String result = SimpleRecordMapper.toString("Test", map);
		assertThat(result).isEqualTo("Test -> key2=123");
	}

	@Test
	void toString6() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key1%", null);
		map.put("key2%%", 123);
		String result = SimpleRecordMapper.toString("Test", map);
		assertThat(result).isEqualTo("Test -> key2%%=123");
	}

	@Test
	void toString7() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key1", null);
		map.put("key2", null);
		String result = SimpleRecordMapper.toString("Test", map);
		assertThat(result).isEqualTo("Test -> ");
	}


	@Test
	void fromString1() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> ");
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEmpty();
	}

	@Test
	void fromString2() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> key=value");
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEqualTo(Map.of("key", "value"));
	}

	@Test
	void fromString3() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> key=123");
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEqualTo(Map.of("key", "123"));
	}

	@Test
	void fromString4() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> key1=value; key2=123");
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEqualTo(Map.of("key1", "value", "key2", "123"));
	}

	@Test
	void fromString5() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> key1=; key2=123");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("key1", null);
		map.put("key2", "123");
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEqualTo(map);
	}

	@Test
	void fromString6() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> key1%=; key2%%=123");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("key1%", null);
		map.put("key2%%", "123");
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEqualTo(map);
	}

	@Test
	void fromString7() {
		ParseResult result = SimpleRecordMapper.fromString("Test -> key1=; key2=");
		Map<String, String> map = new LinkedHashMap<>();
		map.put("key1", null);
		map.put("key2", null);
		assertThat(result.getType()).isEqualTo("Test");
		assertThat(result.getMap()).isEqualTo(map);
	}
}
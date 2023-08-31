package wow.character.service.impl.enumerator;

import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.config.Described;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-10-26
 */
class FilterOutWorseGemChoicesTest extends WowCharacterSpringTest {
	@Test
	void oneType() {
		var gemNames = new String[] {
				"Veiled Flame Spessarite",
				"Veiled Noble Topaz",
				"Veiled Pyrestone"
		};

		var result = getResult(gemNames);

		assertThat(result).isEqualTo(List.of("Veiled Pyrestone"));
	}

	@Test
	void twoTypes() {
		var gemNames = new String[] {
				"Potent Flame Spessarite",
				"Potent Noble Topaz",
				"Potent Pyrestone",
				"Veiled Flame Spessarite",
				"Veiled Noble Topaz",
				"Veiled Pyrestone"
		};

		var result = getResult(gemNames);

		assertThat(result).hasSameElementsAs(List.of(
				"Veiled Pyrestone",
				"Potent Pyrestone"
		));
	}

	private List<String> getResult(String[] gemNames) {
		var gems = Stream.of(gemNames)
				.map(x -> itemRepository.getGem(x, PhaseId.TBC_P5).orElseThrow())
				.toList();

		return new FilterOutWorseGemChoices(gems)
				.getResult().stream()
				.map(Described::getName)
				.toList();
	}
}
package wow.minmax.service.impl.enumerator;

import org.junit.jupiter.api.Test;
import wow.commons.model.config.Described;
import wow.commons.model.pve.PhaseId;
import wow.minmax.WowMinMaxSpringTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-10-26
 */
class FilterOutWorseGemChoicesTest extends WowMinMaxSpringTest {
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
				.map(x -> gemRepository.getGem(x, PhaseId.TBC_P5).orElseThrow())
				.toList();

		return new FilterOutWorseGemChoices(gems)
				.getResult().stream()
				.map(Described::getName)
				.toList();
	}
}
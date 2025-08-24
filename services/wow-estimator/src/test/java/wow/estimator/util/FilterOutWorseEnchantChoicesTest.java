package wow.estimator.util;

import org.junit.jupiter.api.Test;
import wow.commons.model.config.Described;
import wow.commons.model.pve.PhaseId;
import wow.estimator.WowEstimatorSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-10-26
 */
class FilterOutWorseEnchantChoicesTest extends WowEstimatorSpringTest {
	@Test
	void singleAttribute() {
		var enchantNames = List.of(
				"Enchant Chest - Minor Stats",
				"Enchant Chest - Lesser Stats",
				"Enchant Chest - Stats",
				"Enchant Chest - Greater Stats"
		);

		var result = getResult(enchantNames);

		assertThat(result).hasSameElementsAs(List.of(
				"Enchant Chest - Greater Stats"
		));
	}

	@Test
	void multipleAttributes() {
		var enchantNames = List.of(
				"Zandalar Signet of Mojo",
				"Power of the Scourge",
				"Inscription of Discipline",
				"Greater Inscription of Discipline"
		);

		var result = getResult(enchantNames);

		assertThat(result).hasSameElementsAs(List.of(
				"Greater Inscription of Discipline",
				"Power of the Scourge"
		));
	}

	private List<String> getResult(List<String> enchantNames) {
		var enchants = enchantNames.stream()
				.map(x -> enchantRepository.getEnchant(x, PhaseId.TBC_P5).orElseThrow())
				.toList();

		return new FilterOutWorseEnchantChoices(enchants)
				.getResult().stream()
				.map(Described::getName)
				.toList();
	}
}
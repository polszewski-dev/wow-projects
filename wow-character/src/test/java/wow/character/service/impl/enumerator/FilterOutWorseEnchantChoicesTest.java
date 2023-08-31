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
class FilterOutWorseEnchantChoicesTest extends WowCharacterSpringTest {
	@Test
	void singleAttribute() {
		var enchantNames = new String[] {
				"Enchant Chest - Minor Stats",
				"Enchant Chest - Lesser Stats",
				"Enchant Chest - Stats",
				"Enchant Chest - Greater Stats"
		};

		var result = getResult(enchantNames);

		assertThat(result).isEqualTo(List.of("Enchant Chest - Greater Stats"));
	}

	private List<String> getResult(String[] enchantNames) {
		var enchants = Stream.of(enchantNames)
				.map(x -> itemRepository.getEnchant(x, PhaseId.TBC_P5).orElseThrow())
				.toList();

		return new FilterOutWorseEnchantChoices(enchants)
				.getResult().stream()
				.map(Described::getName)
				.toList();
	}
}
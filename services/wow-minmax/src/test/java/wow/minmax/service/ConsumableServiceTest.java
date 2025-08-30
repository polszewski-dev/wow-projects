package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
class ConsumableServiceTest extends ServiceTest {
	@Test
	void getConsumableStatuses() {
		var consumableStatuses = underTest.getConsumableStatuses(CHARACTER_KEY);

		var statusStrings = consumableStatuses.stream()
				.map(x -> x.consumable().getName() + "#" + x.enabled())
				.sorted()
				.toList();

		assertThat(statusStrings).hasSameElementsAs(List.of(
				"Destruction Potion#false"
		));
	}

	@Test
	void enableAndDisableConsumable() {
		assertConsumableStatus("Destruction Potion", false);

		underTest.changeConsumableStatus(CHARACTER_KEY, "Destruction Potion", true);

		assertConsumableStatus("Destruction Potion", true);

		underTest.changeConsumableStatus(CHARACTER_KEY, "Destruction Potion", false);

		assertConsumableStatus("Destruction Potion", false);
	}

	@Autowired
	ConsumableService underTest;

	private void assertConsumableStatus(String consumableName, boolean enabled) {
		assertThat(savedCharacter.getConsumables().stream().anyMatch(consumable -> consumable.getName().equals(consumableName))).isEqualTo(enabled);
	}
}

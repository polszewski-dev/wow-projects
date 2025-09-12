package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.repository.item.ConsumableRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

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

	@Autowired
	ConsumableRepository consumableRepository;

	private void assertConsumableStatus(String consumableName, boolean enabled) {
		var consumable = consumableRepository.getConsumable(consumableName, TBC_P5).orElseThrow();

		var actual = savedCharacter.getConsumableIds().stream()
				.anyMatch(consumableId -> consumableId == consumable.getId().value());

		assertThat(actual).isEqualTo(enabled);
	}
}

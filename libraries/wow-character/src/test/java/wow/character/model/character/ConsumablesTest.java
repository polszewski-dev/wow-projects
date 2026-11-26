package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.character.model.character.TestEvent.ConsumableDisabled;
import static wow.character.model.character.TestEvent.ConsumableEnabled;
import static wow.test.commons.ConsumableNames.*;

/**
 * User: POlszewski
 * Date: 2026-11-27
 */
class ConsumablesTest extends WowCharacterSpringTest {
	@Test
	void getList() {
		assertNoConsumables();

		consumables.enable(SUPER_MANA_POTION);

		assertConsumables(SUPER_MANA_POTION);

		consumables.disable(SUPER_MANA_POTION);

		assertNoConsumables();
	}

	@Test
	void getAvailableHighestRanks() {
		var names = consumables.getAvailableStream()
				.map(Consumable::getName)
				.toList();

		assertThat(names).hasSameElementsAs(List.of(
				DESTRUCTION_POTION,
				MAJOR_HEALING_POTION,
				SUPER_HEALING_POTION,
				MAJOR_MANA_POTION,
				SUPER_MANA_POTION,
				DARK_RUNE,
				MAJOR_HEALTHSTONE,
				MASTER_HEALTHSTONE
		));
	}

	@Test
	void reset() {
		assertConsumable(SUPER_MANA_POTION, false);

		consumables.enable(SUPER_MANA_POTION);

		assertConsumable(SUPER_MANA_POTION, true);

		consumables.reset();

		assertConsumable(SUPER_MANA_POTION, false);

		consumables.enable(SUPER_MANA_POTION);

		assertConsumable(SUPER_MANA_POTION, true);
	}

	@Test
	void setNames() {
		assertConsumable(SUPER_HEALING_POTION, false);
		assertConsumable(SUPER_MANA_POTION, false);

		consumables.setNames(List.of(MASTER_HEALTHSTONE, SUPER_MANA_POTION));

		assertConsumable(MASTER_HEALTHSTONE, true);
		assertConsumable(SUPER_MANA_POTION, true);
		assertConsumables(MASTER_HEALTHSTONE, SUPER_MANA_POTION);

		consumables.setNames(List.of(SUPER_HEALING_POTION));

		assertConsumables(SUPER_HEALING_POTION);
	}

	@Test
	void set() {
		assertConsumable(SUPER_MANA_POTION, false);

		consumables.setNames(List.of(SUPER_MANA_POTION));

		assertConsumable(SUPER_MANA_POTION, true);
		assertConsumables(SUPER_MANA_POTION);

		var invalidIds = List.of(ConsumableId.of(-1));

		assertThatThrownBy(() -> consumables.setIds(invalidIds)).isInstanceOf(NoSuchElementException.class);
		assertNoConsumables();
	}

	@Test
	void enable() {
		assertConsumable(SUPER_MANA_POTION, false);

		consumables.enable(SUPER_MANA_POTION);

		assertConsumables(SUPER_MANA_POTION);

		assertThatThrownBy(() -> consumables.enable(ConsumableId.of(-1))).isInstanceOf(NoSuchElementException.class);

		assertThat(consumables.getList()).hasSize(1);
		assertConsumables(SUPER_MANA_POTION);
	}

	@Test
	void exclusionGroups() {
		assertConsumable(SUPER_MANA_POTION, false);
		assertConsumable(SUPER_HEALING_POTION, false);

		consumables.enable(SUPER_MANA_POTION);

		assertConsumable(SUPER_MANA_POTION, true);
		assertConsumable(SUPER_HEALING_POTION, false);

		consumables.enable(SUPER_HEALING_POTION);

		assertConsumable(SUPER_MANA_POTION, false);
		assertConsumable(SUPER_HEALING_POTION, true);
	}

	@Test
	void has() {
		assertConsumable(SUPER_MANA_POTION, false);

		consumables.enable(SUPER_MANA_POTION);

		assertConsumable(SUPER_MANA_POTION, true);

		consumables.disable(SUPER_MANA_POTION);

		assertConsumable(SUPER_MANA_POTION, false);
	}

	@Test
	void enable_talent() {
		consumables.enable(SUPER_MANA_POTION);

		assertEvents(
				new ConsumableEnabled(superManaPotion)
		);
	}

	@Test
	void disable_talent() {
		consumables.enable(SUPER_MANA_POTION);
		consumables.disable(SUPER_MANA_POTION);

		assertEvents(
				new ConsumableEnabled(superManaPotion),
				new ConsumableDisabled(superManaPotion)
		);
	}

	@Test
	void disable_not_enabled_talent() {
		consumables.disable(SUPER_MANA_POTION);

		assertEvents();
	}

	@Test
	void reset_talent() {
		consumables.enable(SUPER_MANA_POTION);
		consumables.reset();

		assertEvents(
				new ConsumableEnabled(superManaPotion),
				new ConsumableDisabled(superManaPotion)
		);
	}

	@Test
	void enable_from_the_same_exclusion_group() {
		consumables.enable(SUPER_MANA_POTION);
		consumables.enable(SUPER_HEALING_POTION);

		assertEvents(
				new ConsumableEnabled(superManaPotion),
				new ConsumableDisabled(superManaPotion),
				new ConsumableEnabled(superHealingPotion)
		);
	}

	TestPlayerCharacter player;
	Consumables consumables;

	Consumable superManaPotion;
	Consumable superHealingPotion;

	@BeforeEach
	void setup() {
		player = getPlayer();
		consumables = player.getConsumables();
		consumables.reset();

		resetEvents();

		superManaPotion = getConsumable(SUPER_MANA_POTION);
		superHealingPotion = getConsumable(SUPER_HEALING_POTION);
	}

	Consumable getConsumable(String name) {
		return consumableRepository.getConsumable(name, player.getPhaseId()).orElseThrow();
	}

	void assertEvents(TestEvent... expected) {
		assertThat(player.events).isEqualTo(List.of(expected));
	}

	void resetEvents() {
		player.events.clear();
	}
	
	void assertNoConsumables() {
		assertConsumables(consumables);
	}

	void assertConsumable(String name, boolean enabled) {
		assertThat(consumables.has(name)).isEqualTo(enabled);
	}

	void assertConsumables(String... consumableNames) {
		assertConsumables(consumables, consumableNames);
	}
	
	void assertConsumables(Consumables consumables, String... consumableNames) {
		assertThat(consumables.getList().stream().map(Consumable::getName)).hasSameElementsAs(List.of(consumableNames));
	}
}
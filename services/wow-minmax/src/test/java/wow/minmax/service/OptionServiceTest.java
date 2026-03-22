package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.ConsumableId;
import wow.commons.repository.item.ConsumableRepository;
import wow.commons.repository.spell.BuffRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.test.commons.BuffNames.FLASK_OF_PURE_DEATH;
import static wow.test.commons.BuffNames.FLASK_OF_SUPREME_POWER;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
class OptionServiceTest extends ServiceTest {
	@Test
	void getBuffStatuses() {
		var buffStatuses = underTest.getBuffStatuses(PLAYER_ID);

		var statusStrings = buffStatuses.stream()
				.map(x -> x.option().getName() + "#" + x.enabled())
				.sorted()
				.toList();

		assertThat(statusStrings).hasSameElementsAs(List.of(
				"Arcane Brilliance#true",
				"Brilliant Wizard Oil#true",
				"Burning Wish#false",
				"Demon Armor#false",
				"Drums of Battle#false",
				"Fel Armor#true",
				"Flask of Pure Death#true",
				"Flask of Supreme Power#false",
				"Gift of the Wild#true",
				"Greater Blessing of Kings#true",
				"Moonkin Aura#false",
				"Prayer of Fortitude#true",
				"Prayer of Spirit#true",
				"Superior Wizard Oil#false",
				"Totem of Wrath#true",
				"Touch of Shadow#true",
				"Well Fed (sp)#true",
				"Wrath of Air Totem#true"
		));
	}

	@Test
	void enableBuff() {
		assertBuffStatus(FLASK_OF_SUPREME_POWER, false);
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.changeBuffStatus(PLAYER_ID, BuffId.of(17628), true);// FLASK_OF_SUPREME_POWER

		assertBuffStatus(FLASK_OF_SUPREME_POWER, true);
		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Test
	void disableBuff() {
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.changeBuffStatus(PLAYER_ID, BuffId.of(28540), false);// FLASK_OF_PURE_DEATH

		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Test
	void getConsumableStatuses() {
		var consumableStatuses = underTest.getConsumableStatuses(PLAYER_ID);

		var statusStrings = consumableStatuses.stream()
				.map(x -> x.option().getName() + "#" + x.enabled())
				.sorted()
				.toList();

		assertThat(statusStrings).hasSameElementsAs(List.of(
				"Destruction Potion#false",
				"Major Healing Potion#false",
				"Super Healing Potion#false",
				"Major Mana Potion#false",
				"Super Mana Potion#false",
				"Major Healthstone#false",
				"Master Healthstone#false",
				"Dark Rune#false"
		));
	}

	@Test
	void enableAndDisableConsumable() {
		assertConsumableStatus("Destruction Potion", false);

		underTest.changeConsumableStatus(PLAYER_ID, ConsumableId.of(22839), true);//DESTRUCTION_POTION

		assertConsumableStatus("Destruction Potion", true);

		underTest.changeConsumableStatus(PLAYER_ID, ConsumableId.of(22839), false);//DESTRUCTION_POTION

		assertConsumableStatus("Destruction Potion", false);
	}

	@Autowired
	OptionService underTest;

	@Autowired
	BuffRepository buffRepository;

	@Autowired
	ConsumableRepository consumableRepository;

	private void assertBuffStatus(String name, boolean enabled) {
		assertThat(savedCharacter.getBuffIds().stream()
						   .map(BuffId::of)
						   .map(buffId -> buffRepository.getBuff(buffId, TBC_P5))
						   .map(Optional::orElseThrow)
						   .anyMatch(buff -> buff.getName().equals(name))
		).isEqualTo(enabled);
	}

	private void assertConsumableStatus(String consumableName, boolean enabled) {
		var consumable = consumableRepository.getConsumable(consumableName, TBC_P5).orElseThrow();

		var actual = savedCharacter.getConsumableIds().stream()
				.anyMatch(consumableId -> consumableId == consumable.getId().value());

		assertThat(actual).isEqualTo(enabled);
	}
}

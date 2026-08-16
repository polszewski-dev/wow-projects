package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.SpellSchool;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.TestEventCollectingHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.spell.SpellSchool.HOLY;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2025-02-22
 */
class AuraTest extends WowSimulatorSpringTest {
	@Test
	void auras_from_different_items_stack_correctly() {
		player = getNakedPlayer(WARLOCK, UNDEAD, "Player");
		player2 = getNakedPlayer(MAGE, UNDEAD, "Player2");
		player3 = getNakedPlayer(PRIEST, UNDEAD, "Player3");
		player4 = getNakedPlayer(DRUID, TAUREN, "Player4");

		player.getParty().add(player2, player3, player4);

		addToSimulation(player, player2, player3, player4);

		equip(player, 22630, ItemSlot.MAIN_HAND);
		equip(player2, 22589, ItemSlot.MAIN_HAND);
		equip(player3, 22631, ItemSlot.MAIN_HAND);
		equip(player4, 22632, ItemSlot.MAIN_HAND);

		assertSpellDamage(player, 150 + 33);
		assertSpellCritRating(player, 28 + 28);
		assertSpellHealing(player, 150 + 33 + 62);
		assertMp5(player, 11);

		assertSpellDamage(player2, 150 + 33);
		assertSpellCritRating(player2, 28);
		assertSpellHealing(player2, 150 + 33 + 62);
		assertMp5(player2, 11);

		assertSpellDamage(player3, 120 + 33);
		assertSpellCritRating(player3, 28);
		assertSpellHealing(player3, 300 + 33 + 62);
		assertMp5(player3, 11);

		assertSpellDamage(player4, 100 + 33);
		assertSpellCritRating(player4, 28);
		assertSpellHealing(player4, 300 + 33 + 62);
		assertMp5(player4, 11);
	}

	@Test
	void auras_from_the_same_items_stack_correctly() {
		player = getNakedPlayer(WARLOCK, UNDEAD, "Player");
		player2 = getNakedPlayer(MAGE, UNDEAD, "Player2");
		player3 = getNakedPlayer(WARLOCK, UNDEAD, "Player3");
		player4 = getNakedPlayer(MAGE, UNDEAD, "Player4");

		player.getParty().add(player2, player3, player4);

		addToSimulation(player, player2, player3, player4);

		equip(player, 22630, ItemSlot.MAIN_HAND);
		equip(player2, 22589, ItemSlot.MAIN_HAND);
		equip(player3, 22630, ItemSlot.MAIN_HAND);
		equip(player4, 22589, ItemSlot.MAIN_HAND);

		assertSpellDamage(player, 150 + 33);
		assertSpellCritRating(player, 28 + 28);

		assertSpellDamage(player2, 150 + 33);
		assertSpellCritRating(player2, 28);

		assertSpellDamage(player3, 150 + 33);
		assertSpellCritRating(player3, 28 + 28);

		assertSpellDamage(player4, 150 + 33);
		assertSpellCritRating(player4, 28);
	}

	@Test
	void racial_auras_stack_correctly() {
		player = getNakedPlayer(WARLOCK, HUMAN, "Player");
		player2 = getNakedPlayer(MAGE, DRANEI, "Player2");
		player3 = getNakedPlayer(PRIEST, DRANEI, "Player3");

		player.getParty().add(player2, player3);

		addToSimulation(player, player2, player3);

		assertSpellHitPct(player, 83 + 1);
		assertSpellHitPct(player2, 83 + 1);
		assertSpellHitPct(player3, 83 + 1);
	}

	@Test
	void spell_auras_stack_correctly() {
		player = getNakedPlayer(WARLOCK, UNDEAD, "Player");
		player2 = getNakedPlayer(DRUID, TAUREN, "Player2");
		player3 = getNakedPlayer(DRUID, TAUREN, "Player3");
		player4 = getNakedPlayer(WARLOCK, UNDEAD, "Player4");

		enableTalent(player2, MOONKIN_FORM, 1);
		enableTalent(player3, MOONKIN_FORM, 1);

		player.getParty().add(player2, player3);

		addToSimulation(player, player2, player3, player4);

		player2.cast(MOONKIN_FORM);
		player3.cast(MOONKIN_FORM);

		updateUntil(30);

		assertSpellCritPctIncreasedBy(player, player4, 5);
	}

	@Test
	void augmented_auras_stack_correctly() {
		player = getNakedPlayer(PRIEST, UNDEAD, "Player");
		player2 = getNakedPlayer(PALADIN, BLOOD_ELF, "Player2");
		player3 = getNakedPlayer(PALADIN, BLOOD_ELF, "Player3");
		player4 = getNakedPlayer(PALADIN, BLOOD_ELF, "Player4");

		enableTalent(player2, SANCTITY_AURA, 1);
		enableTalent(player3, SANCTITY_AURA, 1);
		enableTalent(player4, SANCTITY_AURA, 1);

		enableTalent(player2, IMPROVED_SANCTITY_AURA, 2);
		enableTalent(player3, IMPROVED_SANCTITY_AURA, 1);

		player.getParty().add(player2, player3, player4);

		addToSimulation(player, player2, player3, player4);

		player2.cast(SANCTITY_AURA);
		player3.cast(SANCTITY_AURA);
		player4.cast(SANCTITY_AURA);

		updateUntil(30);

		assertSpellDamagePct(player, HOLY, 12);
	}

	private void assertSpellDamagePct(Unit unit, SpellSchool school, int percent) {
		assertThat(unit.getStats().getSpellDamagePct(school)).isEqualTo(percent);
	}

	@Test
	void pet_auras_stack_correctly() {
		player = getNakedPlayer(WARLOCK, UNDEAD, "Player");
		player2 = getNakedPlayer(WARLOCK, UNDEAD, "Player2");
		player4 = getNakedPlayer(WARLOCK, UNDEAD, "Player4");

		player.getParty().add(player2);

		addToSimulation(player, player2, player4);

		player.cast(SUMMON_IMP);
		player2.cast(SUMMON_IMP);
		player4.cast(SUMMON_IMP);

		summonedPetCasts(player, BLOOD_PACT);
		summonedPetCasts(player2, BLOOD_PACT);

		updateUntil(30);

		assertStaminaIncreasedBy(player, player4, 70);
		assertStaminaIncreasedBy(player.getActivePet(), player4.getActivePet(), 70);
	}

	private void assertSpellDamage(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellDamage()).isEqualTo(expected);
	}

	private void assertSpellHealing(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellHealing()).isEqualTo(expected);
	}

	private void assertSpellHitPct(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellHitPct()).isEqualTo(expected);
	}

	private void assertSpellCritRating(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellCritRating()).isEqualTo(expected);
	}

	private void assertSpellCritPctIncreasedBy(Player unit, Player base, int expected) {
		assertThat(unit.getStats().getSpellCritPct()).isEqualTo(base.getStats().getSpellCritPct() + expected, PRECISION);
	}

	private void assertMp5(Unit unit, int expected) {
		assertThat(unit.getStats().getInterruptedManaRegen()).isEqualTo(expected);
	}

	private void assertStaminaIncreasedBy(Unit unit, Unit base, int expected) {
		assertThat(unit.getStats().getStamina()).isEqualTo(base.getStats().getStamina() + expected);
	}

	Player player2;
	Player player3;
	Player player4;

	@BeforeEach
	void setUp() {
		simulationContext = getSimulationContext();
		simulation = new Simulation(simulationContext);
		handler = new TestEventCollectingHandler();
		simulation.addHandler(handler);
	}
}

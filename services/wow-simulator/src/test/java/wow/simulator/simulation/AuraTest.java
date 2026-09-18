package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.MAIN_HAND;
import static wow.commons.model.categorization.ItemSlot.NECK;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.spell.SpellSchool.HOLY;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2025-02-22
 */
class AuraTest extends SpellSimulationTest {
	@Test
	void auras_from_different_items_stack_correctly() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(WARLOCK, UNDEAD);
		setPlayer3Config(MAGE, UNDEAD);
		setPlayer4Config(PRIEST, UNDEAD);
		setPlayer5Config(DRUID, TAUREN);

		createDefaultUnits();

		equip(player2, 22630, MAIN_HAND);
		equip(player3, 22589, MAIN_HAND);
		equip(player4, 22631, MAIN_HAND);
		equip(player5, 22632, MAIN_HAND);

		assertSpellDamageIsIncreasedBy(player, 33);
		assertSpellCritRatingIsIncreasedBy(player, 28);
		assertSpellHealingIsIncreasedBy(player, 33 + 62);
		assertMp5IsIncreasedBy(player, 11);
	}

	@Test
	void auras_from_the_same_items_stack_correctly() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(WARLOCK, UNDEAD);
		setPlayer3Config(MAGE, UNDEAD);
		setPlayer4Config(WARLOCK, UNDEAD);
		setPlayer5Config(MAGE, UNDEAD);

		createDefaultUnits();

		equip(player2, 22630, MAIN_HAND);
		equip(player3, 22589, MAIN_HAND);
		equip(player4, 22630, MAIN_HAND);
		equip(player5, 22589, MAIN_HAND);

		assertSpellDamageIsIncreasedBy(player, 33);
		assertSpellCritRatingIsIncreasedBy(player, 28);
	}

	@Test
	void racial_auras_stack_correctly() {
		setPlayerConfig(WARLOCK, HUMAN);
		setPlayer2Config(MAGE, DRANEI);
		setPlayer3Config(PRIEST, DRANEI);

		createDefaultUnits();

		assertSpellHitPctIncreasedBy(player, 1);
		assertSpellHitPctIncreasedBy(player2, 1);
		assertSpellHitPctIncreasedBy(player3, 1);
	}

	@Test
	void spell_auras_stack_correctly() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(DRUID, TAUREN);
		setPlayer3Config(DRUID, TAUREN);

		createDefaultUnits();

		enableTalent(player2, MOONKIN_FORM);
		enableTalent(player3, MOONKIN_FORM);

		player2.cast(MOONKIN_FORM);
		player3.cast(MOONKIN_FORM);

		updateUntil(30);

		assertSpellCritPctIsIncreasedBy(player, 5);
	}

	@Test
	void augmented_auras_stack_correctly() {
		setPlayerConfig(PRIEST, UNDEAD);
		setPlayer2Config(PALADIN, BLOOD_ELF);
		setPlayer3Config(PALADIN, BLOOD_ELF);
		setPlayer4Config(PALADIN, BLOOD_ELF);

		createDefaultUnits();

		enableTalent(player2, SANCTITY_AURA);
		enableTalent(player3, SANCTITY_AURA);
		enableTalent(player4, SANCTITY_AURA);

		enableTalent(player2, IMPROVED_SANCTITY_AURA, 2);
		enableTalent(player3, IMPROVED_SANCTITY_AURA, 1);

		player2.cast(SANCTITY_AURA);
		player3.cast(SANCTITY_AURA);
		player4.cast(SANCTITY_AURA);

		updateUntil(30);

		assertSpellDamagePctIncreasedBy(player, HOLY, 12);
	}

	@Test
	void pet_auras_stack_correctly() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(WARLOCK, UNDEAD);

		createDefaultUnits();

		player.cast(SUMMON_IMP);
		player2.cast(SUMMON_IMP);
		baseline.cast(SUMMON_IMP);

		player.petCast(BLOOD_PACT);
		player2.petCast(BLOOD_PACT);

		updateUntil(30);

		assertStaminaIsIncreasedBy(player, 70);
		assertStaminaIsIncreasedBy(player.getActivePet(), baseline.getActivePet(), 70 + 21);//todo incorrectly added 30% from player's "Blood Pact"
	}

	@Test
	void aura_disappears_after_pet_is_unsummoned() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(WARLOCK, UNDEAD);

		createDefaultUnits();

		player2.cast(SUMMON_IMP);
		player2.petCast(BLOOD_PACT);
		player2.idleUntil(Time.at(20));
		player2.immediateAction(Unit::dismissPet);

		updateUntil(30);

		assertStaminaIsIncreasedBy(player, 0);
	}

	@Test
	void aura_disappears_after_pet_is_sacrificed() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(WARLOCK, UNDEAD);

		createDefaultUnits();

		enableTalent(player2, DEMONIC_SACRIFICE);

		player2.cast(SUMMON_IMP);
		player2.petCast(BLOOD_PACT);
		player2.idleUntil(Time.at(20));
		player2.cast(DEMONIC_SACRIFICE);

		updateUntil(30);

		assertStaminaIsIncreasedBy(player, 0);
	}

	@Test
	void aura_disappears_after_masters_death() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(WARLOCK, UNDEAD);

		createDefaultUnits();

		player2.cast(SUMMON_IMP);
		player2.petCast(BLOOD_PACT);
		player2.idleUntil(Time.at(20));
		player2.immediateAction(self -> instaKill(self, self));

		updateUntil(30);

		assertStaminaIsIncreasedBy(player, 0);
	}

	@Test
	void auras_from_necklaces_stack_correctly() {
		setPlayerConfig(WARLOCK, UNDEAD);

		createDefaultUnits();

		equip(player2, EYE_OF_THE_NIGHT, NECK);
		equip(player3, CHAIN_OF_THE_TWILIGHT_OWL, NECK);
		equip(player4, THICK_FELSTEEL_NECKLACE, NECK);
		equip(player5, EMBRACE_OF_THE_DAWN, NECK);

		player2.cast(EYE_OF_THE_NIGHT);
		player3.cast(CHAIN_OF_THE_TWILIGHT_OWL);
		player4.cast(THICK_FELSTEEL_NECKLACE);
		player5.cast(EMBRACE_OF_THE_DAWN);

		updateUntil(30);

		assertSpellDamageIsIncreasedBy(player, 34);
		assertSpellCritPctIsIncreasedBy(player, 2 + 0.125);// 2% + crit from +10 int
		assertStaminaIsIncreasedBy(player, 30);
		assertIntellectIsIncreasedBy(player, 10);
		assertSpiritIsIncreasedBy(player, 10);
	}

	@Override
	protected void beforeSetUp() {
		createUnitsOnSetUp = false;
	}
}

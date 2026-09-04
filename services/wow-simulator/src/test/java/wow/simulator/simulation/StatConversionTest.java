package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.model.unit.Player;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.MOONCLOTH_TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.SPELLFIRE_TAILORING;
import static wow.commons.model.spell.SpellSchool.SHADOW;
import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.*;

/**
 * User: POlszewski
 * Date: 2026-09-03
 */
class StatConversionTest extends SpellSimulationTest {
	@Test
	void intellect_to_spell_power_conversion_from_lunar_guidance() {
		enableTalent(druid, LUNAR_GUIDANCE);

		int totalIntellect = 115;
		int totalSpellPower = getPercentOf(25, totalIntellect);

		assertIntellect(druid, totalIntellect);
		assertSpellPower(druid, totalSpellPower);
		assertSpellDamage(druid, totalSpellPower);
		assertSpellHealing(druid, totalSpellPower);
	}

	@Test
	void intellect_to_spell_power_conversion_from_natures_blessing() {
		enableTalent(shaman, NATURES_BLESSING);

		int totalIntellect = 103;
		int totalSpellPower = getPercentOf(30, totalIntellect);

		assertIntellect(shaman, totalIntellect);
		assertSpellPower(shaman, totalSpellPower);
		assertSpellDamage(shaman, totalSpellPower);
		assertSpellHealing(shaman, totalSpellPower);
	}

	@Test
	void intellect_to_spell_power_conversion_from_holy_guidance() {
		enableTalent(paladin, HOLY_GUIDANCE);

		int totalIntellect = 86;
		int totalSpellPower = getPercentOf(35, totalIntellect);

		assertIntellect(paladin, totalIntellect);
		assertSpellPower(paladin, totalSpellPower);
		assertSpellDamage(paladin, totalSpellPower);
		assertSpellHealing(paladin, totalSpellPower);
	}

	@Test
	void intellect_to_spell_damage_conversion_from_mind_mastery() {
		enableTalent(mage, MIND_MASTERY);

		int totalIntellect = 149;
		int totalSpellDamage = getPercentOf(25, totalIntellect);

		assertIntellect(mage, totalIntellect);
		assertSpellPower(mage, 0);
		assertSpellDamage(mage, totalSpellDamage);
		assertSpellHealing(mage, 0);
	}

	@Test
	void intellect_to_spell_damage_conversion_from_wrath_of_spellfire_p3_bonus() {
		mage.addProfession(TAILORING, SPELLFIRE_TAILORING, 375);

		equip(mage, "Spellfire Robe");
		equip(mage, "Spellfire Gloves");
		equip(mage, "Spellfire Belt");

		int totalIntellect = 149 + 17 + 10 + 18;
		int totalSpellDamage = getPercentOf(7, totalIntellect);

		assertIntellect(mage, totalIntellect);
		assertSpellPower(mage, 0);
		assertSpellDamage(mage, totalSpellDamage);
		assertSpellHealing(mage, 0);
	}

	@Test
	void intellect_to_spell_healing_conversion_from_whitemend_wisdom_p2_bonus() {
		priest.addProfession(TAILORING, MOONCLOTH_TAILORING, 375);

		equip(priest, "Whitemend Hood");
		equip(priest, "Whitemend Pants");

		int totalIntellect = 143 + 15 + 21;
		int totalSpellDamage = 27 + 21;
		int totalSpellHealing = 79 + 62 + getPercentOf(10, totalIntellect);

		assertIntellect(priest, totalIntellect);
		assertSpellPower(priest, 0);
		assertSpellDamage(priest, totalSpellDamage);
		assertSpellHealing(priest, totalSpellHealing);
	}

	@Test
	void spirit_to_spell_power_conversion_from_spiritual_guidance() {
		enableTalent(priest, SPIRITUAL_GUIDANCE);

		int totalSpirit = 156;
		int totalSpellPower = getPercentOf(25, totalSpirit);

		assertSpirit(priest, totalSpirit);
		assertSpellPower(priest, totalSpellPower);
		assertSpellDamage(priest, totalSpellPower);
		assertSpellHealing(priest, totalSpellPower);
	}

	@Test
	void spirit_to_spell_power_conversion_from_improved_divine_spirit() {
		enableTalent(priest, DIVINE_SPIRIT);
		enableTalent(priest, IMPROVED_DIVINE_SPIRIT);

		priest.cast(DIVINE_SPIRIT);

		updateUntil(30);

		int totalSpirit = 156 + 50;
		int totalSpellPower = getPercentOf(10, totalSpirit);

		assertSpirit(priest, totalSpirit);
		assertSpellPower(priest, totalSpellPower);
		assertSpellDamage(priest, totalSpellPower);
		assertSpellHealing(priest, totalSpellPower);
	}

	@Test
	void spirit_to_spell_power_conversion_from_spiritual_guidance_and_improved_divine_spirit() {
		enableTalent(priest, SPIRITUAL_GUIDANCE);
		enableTalent(priest, DIVINE_SPIRIT);
		enableTalent(priest, IMPROVED_DIVINE_SPIRIT);

		priest.cast(DIVINE_SPIRIT);

		updateUntil(30);

		int totalSpirit = 156 + 50;
		int totalSpellPower = getPercentOf(25 + 10, totalSpirit);

		assertSpirit(priest, totalSpirit);
		assertSpellPower(priest, totalSpellPower);
		assertSpellDamage(priest, totalSpellPower);
		assertSpellHealing(priest, totalSpellPower);
	}

	@Test
	void intellect_to_mp5_conversion_from_dreamstate() {
		enableTalent(druid, DREAMSTATE);

		int totalIntellect = 115;
		int totalMp5 = getPercentOf(10, totalIntellect);

		assertIntellect(druid, totalIntellect);
		assertMp5(druid, totalMp5);
	}

	@Test
	void intellect_to_mp5_conversion_from_unrelenting_storm() {
		enableTalent(shaman, UNRELENTING_STORM);

		int totalIntellect = 103;
		int totalMp5 = getPercentOf(10, totalIntellect);

		assertIntellect(shaman, totalIntellect);
		assertMp5(shaman, totalMp5);
	}

	@Test
	void master_stamina_to_pet_stamina_conversion() {
		warlock.cast(SUMMON_IMP);

		updateUntil(30);

		int totalMastersStamina = 77;
		int totalPetsStamina = 77 + getPercentOf(30, totalMastersStamina);

		assertStamina(warlock, totalMastersStamina);
		assertStamina(pet, totalPetsStamina);
	}

	@Test
	void master_intellect_to_pet_intellect_conversion() {
		warlock.cast(SUMMON_IMP);

		updateUntil(30);

		int totalMastersIntellect = 131;
		int totalPetsIntellect = 131 + getPercentOf(30, totalMastersIntellect);

		assertIntellect(warlock, totalMastersIntellect);
		assertIntellect(pet, totalPetsIntellect);
	}

	@Test
	void master_spell_damage_to_pet_spell_damage_conversion() {
		equip(warlock, "Zhar'doom, Greatstaff of the Devourer", "Enchant Weapon - Soulfrost");

		warlock.cast(SUMMON_IMP);

		updateUntil(30);

		int totalMastersSpellDamage = 259;
		int totalMastersShadowDamage = 259 + 54;
		int totalPetsSpellDamage = getPercentOf(15, totalMastersSpellDamage);
		int totalPetsShadowDamage = getPercentOf(15, totalMastersShadowDamage);

		assertSpellDamage(warlock, totalMastersSpellDamage);
		assertSpellDamage(pet, totalPetsSpellDamage);
		assertSpellDamage(warlock, SHADOW, totalMastersShadowDamage);
		assertSpellDamage(pet, SHADOW, totalPetsShadowDamage);
		assertSpellPower(pet, 0);
		assertSpellHealing(pet, 0);
	}

	@Test
	void master_hit_rating_to_pet_hit_rating_conversion() {
		equip(warlock, "Robes of Rhonin");

		warlock.cast(SUMMON_IMP);

		updateUntil(30);

		var totalMastersSpellHitRating = 27;
		var mastersHitPct = warlock.getStats().getSpellHitPct();

		assertSpellHitRating(warlock, totalMastersSpellHitRating);
		assertSpellHitRating(pet, totalMastersSpellHitRating);
		assertSpellHitPct(pet, mastersHitPct);
	}

	@Test
	void pet_sta_plus_int_to_master_spell_damage_conversion_from_demonic_knowledge() {
		enableTalent(warlock, DEMONIC_KNOWLEDGE);

		warlock.cast(SUMMON_IMP);

		updateUntil(30);

		assertDemonicKnowledgeConversions(0, 0, 0, 0);
	}

	@Test
	void pet_sta_plus_int_to_master_spell_damage_conversion_from_demonic_knowledge_with_item_equipped() {
		equip(warlock, "Robes of Rhonin");

		enableTalent(warlock, DEMONIC_KNOWLEDGE);

		warlock.cast(SUMMON_IMP);

		updateUntil(30);

		assertDemonicKnowledgeConversions(55, 38, 81, 0);
	}

	@Test
	void pet_sta_plus_int_to_master_spell_damage_conversion_from_demonic_knowledge_with_item_equipped_and_improved_divine_spirit() {
		equip(warlock, "Robes of Rhonin");

		enableTalent(warlock, DEMONIC_KNOWLEDGE);
		enableTalent(priest, DIVINE_SPIRIT);
		enableTalent(priest, IMPROVED_DIVINE_SPIRIT);

		warlock.cast(SUMMON_IMP);
		warlock.immediateAction(() -> priest.cast(DIVINE_SPIRIT, pet));
		priest.cast(DIVINE_SPIRIT, warlock);

		updateUntil(30);

		var mastersSpirit = 194;
		var petsSpirit = 194;

		assertSpirit(warlock, mastersSpirit);
		assertSpirit(pet, petsSpirit);

		assertDemonicKnowledgeConversions(55, 38, 81 + getPercentOf(10.0, mastersSpirit), getPercentOf(10.0, petsSpirit));
	}

	private void assertDemonicKnowledgeConversions(int staminaBonus, int intellectBonus, double mastersSpBonus, double petsSpBonus) {
		var totalMastersStamina = 77 + staminaBonus;
		var totalMastersIntellect = 131 + intellectBonus;

		var totalPetsStamina = 77 + getPercentOf(30.0, totalMastersStamina);
		var totalPetsIntellect = 131 + getPercentOf(30.0, totalMastersIntellect);

		var spellDamageBonus = getPercentOf(12.0, totalPetsStamina + totalPetsIntellect);

		var totalMastersSpellPower = mastersSpBonus;
		var totalMastersSpellDamage = totalMastersSpellPower + spellDamageBonus;
		var totalPetsSpellDamage = petsSpBonus + getPercentOf(15.0, totalMastersSpellDamage);

		assertStamina(warlock, totalMastersStamina);
		assertStamina(pet, (int) totalPetsStamina);

		assertIntellect(warlock, totalMastersIntellect);
		assertIntellect(pet, (int) totalPetsIntellect);

		assertSpellPower(warlock, (int) totalMastersSpellPower);
		assertSpellDamage(warlock, (int) totalMastersSpellDamage);
		assertSpellHealing(warlock, (int) totalMastersSpellPower);

		assertSpellPower(pet, (int) petsSpBonus);
		assertSpellDamage(pet, (int) totalPetsSpellDamage);
		assertSpellHealing(pet, (int) petsSpBonus);
	}

	@Override
	protected void beforeSetUp() {
		setPlayerConfig(WARLOCK, UNDEAD);
		setPlayer2Config(MAGE, UNDEAD);
		setPlayer3Config(PRIEST, UNDEAD);
		setPlayer4Config(DRUID, TAUREN);
		setPlayer5Config(SHAMAN, TAUREN);
	}

	@Override
	protected void afterSetUp() {
		warlock = player;
		mage = player2;
		priest = player3;
		druid = player4;
		shaman = player5;

		paladin = getNakedPlayer(PALADIN, BLOOD_ELF, "Paladin");
		simulation.add(paladin);
	}

	Player warlock;
	Player mage;
	Player priest;
	Player druid;
	Player shaman;
	Player paladin;
}

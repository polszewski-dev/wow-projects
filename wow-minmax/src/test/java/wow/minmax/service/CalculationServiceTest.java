package wow.minmax.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.build.BuffSetId;
import wow.character.model.equipment.Equipment;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.spells.Spell;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.RotationStats;
import wow.minmax.model.SpecialAbilityStats;
import wow.minmax.model.SpellStats;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.spells.SpellId.CURSE_OF_DOOM;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.spells.SpellSchool.FIRE;
import static wow.commons.model.spells.SpellSchool.SHADOW;
import static wow.minmax.service.CalculationService.EquivalentMode.ADDITIONAL;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class CalculationServiceTest extends ServiceTest {
	@Autowired
	CalculationService underTest;

	@Test
	@DisplayName("Has talents, no buffs, no items")
	void hasTalentsNoBuffsNoItems() {
		character.setEquipment(new Equipment());
		character.resetBuffs();
		character.getTargetEnemy().resetDebuffs();

		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(88);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(131);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(136);

		assertThat(snapshot.getTotalCrit()).isEqualTo(11.29, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(0, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(0);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.83, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.11, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getCastTime()).isEqualTo(2.5, PRECISION);
		assertThat(snapshot.getGcd()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, no items")
	void hasTalentsHasBuffsNoItems() {
		character.setEquipment(new Equipment());

		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(212);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(203);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(239);

		assertThat(snapshot.getTotalCrit()).isEqualTo(15.83, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(3, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(370);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.86, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.15, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.72, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);

		assertThat(snapshot.getCastTime()).isEqualTo(2.5, PRECISION);
		assertThat(snapshot.getGcd()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, has items")
	void hasTalentsHasBuffsHasItems() {
		character.setEquipment(getEquipment());

		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(797);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(620);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(245);

		assertThat(snapshot.getTotalCrit()).isEqualTo(35.31, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(28.88, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.99, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.36, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.72, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0.2888, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);

		assertThat(snapshot.getCastTime()).isEqualTo(1.94, PRECISION);
		assertThat(snapshot.getGcd()).isEqualTo(1.16, PRECISION);
		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(1.94, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Correct stat quivalent")
	void correctStatEquivalent() {
		character.setEquipment(getEquipment());

		Attributes hitEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_HIT_RATING, 10), SPELL_POWER, ADDITIONAL, character);
		Attributes critEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_CRIT_RATING, 10), SPELL_POWER, ADDITIONAL, character);
		Attributes hasteEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_HASTE_RATING, 10), SPELL_POWER, ADDITIONAL, character);

		assertThat(hitEqv.getSpellPower()).isEqualTo(0.11, PRECISION);
		assertThat(critEqv.getSpellPower()).isEqualTo(9.79, PRECISION);
		assertThat(hasteEqv.getSpellPower()).isEqualTo(11.37, PRECISION);
	}

	@Test
	@DisplayName("Correct stat quivalent")
	void correctAbilityEquivalent() {
		character.setEquipment(getEquipment());

		String line = "Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)";
		SpecialAbility specialAbility = character.getEquipment().getStats().getSpecialAbilities().stream()
				.filter(x -> line.equals(x.getLine()))
				.findFirst()
				.orElseThrow();
		Attributes equivalent = underTest.getAbilityEquivalent(specialAbility, character);

		assertThat(equivalent.getSpellHasteRating()).isEqualTo(29.17, PRECISION);
	}

	@Test
	@DisplayName("Correct rotation dps")
	void correctRotationDps() {
		character.setEquipment(getEquipment());

		double dps = underTest.getRotationDps(character, character.getRotation());

		assertThat(dps).usingComparator(ROUNDED_DOWN).isEqualTo(2777);
	}

	@Test
	@DisplayName("Correct rotation stats")
	void correctRotationStats() {
		character.setEquipment(getEquipment());

		RotationStats stats = underTest.getRotationStats(character, character.getRotation());

		assertThat(stats.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2777);
		assertThat(stats.getStatList()).hasSize(2);
		assertThat(stats.getStatList().get(0).getSpell().getSpellId()).isEqualTo(CURSE_OF_DOOM);
		assertThat(stats.getStatList().get(1).getSpell().getSpellId()).isEqualTo(SHADOW_BOLT);
	}

	@Test
	@DisplayName("Correct spell stats")
	void correctSpellStats() {
		character.setEquipment(getEquipment());

		SpellStats spellStats = underTest.getSpellStats(character, character.getRotation().getFiller());

		assertThat(spellStats.getCharacter()).isSameAs(character);
		assertThat(spellStats.getSpellStatistics().getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5175);
		assertThat(spellStats.getSpellStatistics().getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2669);
		assertThat(spellStats.getSpellStatistics().getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(spellStats.getSpellStatistics().getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(spellStats.getSpellStatistics().getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(12);
		assertThat(spellStats.getStatEquivalents().getHitSpEqv()).isEqualTo(0.11, PRECISION);
		assertThat(spellStats.getStatEquivalents().getCritSpEqv()).isEqualTo(10.15, PRECISION);
		assertThat(spellStats.getStatEquivalents().getHasteSpEqv()).isEqualTo(11.55, PRECISION);
	}

	@Test
	@DisplayName("Correct current stats")
	void correctCurrentStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getCurrentStats(character);

		assertThat(stats.getSp()).isEqualTo(1616);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1750.0);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1696.0);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(16.00, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(331);
		assertThat(stats.getCritPct()).isEqualTo(30.31, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(797);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(620);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(245);
	}

	@Test
	@DisplayName("Correct stats")
	void correctStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getStats(character, BuffSetId.SELF_BUFFS);

		assertThat(stats.getSp()).isEqualTo(1456);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1510.0);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1456.0);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(12.99, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(317);
		assertThat(stats.getCritPct()).isEqualTo(25.30, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(626);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(510);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(142);
	}

	@Test
	@DisplayName("Correct equipment stats")
	void correctEquipmentStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getEquipmentStats(character);

		assertThat(stats.getSp()).isEqualTo(1326);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1380.0);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1326.0);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(13.00, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(317);
		assertThat(stats.getCritPct()).isEqualTo(22.30, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(545);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(510);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(150);
	}

	@Test
	@DisplayName("Correct special ability stats")
	void correctSpecialAbilityStats() {
		character.setEquipment(getEquipment());

		SpecialAbility specialAbility = character.getStats().getSpecialAbilities().stream()
				.filter(x -> "Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)".equals(x.getLine()))
				.findFirst()
				.orElseThrow();

		SpecialAbilityStats stats = underTest.getSpecialAbilityStats(character, specialAbility);

		assertThat(stats.getAbility()).isSameAs(specialAbility);
		assertThat(stats.getStatEquivalent().statString()).isEqualTo("29.17 spell haste rating");
		assertThat(stats.getSpEquivalent()).isEqualTo(33.62, PRECISION);
	}
}
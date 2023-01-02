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
import wow.minmax.model.SpecialAbilityStats;
import wow.minmax.model.SpellStats;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
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

		Spell spell = character.getDamagingSpell();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(89);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(130);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(108);

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

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(2.5, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, no items")
	void hasTalentsHasBuffsNoItems() {
		character.setEquipment(new Equipment());

		Spell spell = character.getDamagingSpell();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(115);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(202);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(155);

		assertThat(snapshot.getTotalCrit()).isEqualTo(15.82, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(3, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(370);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.86, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.15, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.74, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(2.5, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, has items")
	void hasTalentsHasBuffsHasItems() {
		character.setEquipment(getEquipment());

		Spell spell = character.getDamagingSpell();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(700);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(619);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(161);

		assertThat(snapshot.getTotalCrit()).isEqualTo(35.30, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(28.88, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.99, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.36, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0.2888, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.16, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(1.94, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

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
		assertThat(critEqv.getSpellPower()).isEqualTo(10.30, PRECISION);
		assertThat(hasteEqv.getSpellPower()).isEqualTo(10.94, PRECISION);
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
		Attributes equivalent = underTest.getAbilityEquivalent(specialAbility, character, null, null);

		assertThat(equivalent.getSpellHasteRating()).isEqualTo(29.17, PRECISION);
	}

	@Test
	@DisplayName("Correct spell dps")
	void correctSpellDps() {
		character.setEquipment(getEquipment());

		double dps = underTest.getSpellDps(character, null);

		assertThat(dps).usingComparator(ROUNDED_DOWN).isEqualTo(2691);
	}

	@Test
	@DisplayName("Correct spell stats")
	void correctSpellStats() {
		character.setEquipment(getEquipment());

		SpellStats spellStats = underTest.getSpellStats(character, null);

		assertThat(spellStats.getCharacter()).isSameAs(character);
		assertThat(spellStats.getSpellStatistics().getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5219);
		assertThat(spellStats.getSpellStatistics().getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2691);
		assertThat(spellStats.getSpellStatistics().getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(spellStats.getSpellStatistics().getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(spellStats.getSpellStatistics().getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(13);
		assertThat(spellStats.getHitSpEqv()).isEqualTo(0.11, PRECISION);
		assertThat(spellStats.getCritSpEqv()).isEqualTo(10.30, PRECISION);
		assertThat(spellStats.getHasteSpEqv()).isEqualTo(10.94, PRECISION);
	}

	@Test
	@DisplayName("Correct current stats")
	void correctCurrentStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getCurrentStats(character);

		assertThat(stats.getSp()).isEqualTo(1616);
		assertThat(stats.getSpShadow()).isEqualTo(1750);
		assertThat(stats.getSpFire()).isEqualTo(1696);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(16.00, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(331);
		assertThat(stats.getCritPct()).isEqualTo(35.30, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(28.88, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(700);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(619);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(161);
	}

	@Test
	@DisplayName("Correct stats")
	void correctStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getStats(character, BuffSetId.SELF_BUFFS);

		assertThat(stats.getSp()).isEqualTo(1456);
		assertThat(stats.getSpShadow()).isEqualTo(1510);
		assertThat(stats.getSpFire()).isEqualTo(1456);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(12.99, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(317);
		assertThat(stats.getCritPct()).isEqualTo(30.29, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(28.88, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(627);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(509);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(114);
	}

	@Test
	@DisplayName("Correct equipment stats")
	void correctEquipmentStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getEquipmentStats(character);

		assertThat(stats.getSp()).isEqualTo(1326);
		assertThat(stats.getSpShadow()).isEqualTo(1380);
		assertThat(stats.getSpFire()).isEqualTo(1326);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(13.00, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(317);
		assertThat(stats.getCritPct()).isEqualTo(22.29, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(28.88, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(546);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(509);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(120);
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

		assertThat(stats.getDescription()).isEqualTo("Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)");
		assertThat(stats.getAbility()).isEqualTo("(175 haste | 20s/2m)");
		assertThat(stats.getStatEquivalent().statString()).isEqualTo("29.17 haste");
		assertThat(stats.getSpEquivalent()).isEqualTo(35.10, PRECISION);
	}
}
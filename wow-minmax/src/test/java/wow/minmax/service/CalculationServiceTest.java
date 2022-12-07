package wow.minmax.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.spells.Snapshot;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellStatistics;
import wow.minmax.model.PlayerSpellStats;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.character.BuffSetId.*;
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
		playerProfile.setEquipment(new Equipment());
		playerProfile.setBuffs(List.of());

		Spell spell = playerProfile.getDamagingSpell();
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, playerProfile.getStats());

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
		playerProfile.setEquipment(new Equipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		Spell spell = playerProfile.getDamagingSpell();
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, playerProfile.getStats());

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
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		Spell spell = playerProfile.getDamagingSpell();
		Snapshot snapshot = underTest.getSnapshot(playerProfile, spell, playerProfile.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(700);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(619);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(161);

		assertThat(snapshot.getTotalCrit()).isEqualTo(35.75, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(31.10, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1749);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.99, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.36, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0.3110, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(1.91, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.14, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(1.91, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Correct stat quivalent")
	void correctStatEquivalent() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		Attributes hitEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_HIT_RATING, 10), SPELL_POWER, ADDITIONAL, playerProfile);
		Attributes critEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_CRIT_RATING, 10), SPELL_POWER, ADDITIONAL, playerProfile);
		Attributes hasteEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_HASTE_RATING, 10), SPELL_POWER, ADDITIONAL, playerProfile);

		assertThat(hitEqv.getSpellPower()).isEqualTo(0.11, PRECISION);
		assertThat(critEqv.getSpellPower()).isEqualTo(9.98, PRECISION);
		assertThat(hasteEqv.getSpellPower()).isEqualTo(10.87, PRECISION);
	}

	@Test
	@DisplayName("Correct stat quivalent")
	void correctAbilityEquivalent() {
		playerProfile.setEquipment(getEquipment());

		String line = "Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)";
		SpecialAbility specialAbility = playerProfile.getEquipment().getStats().getSpecialAbilities().stream()
				.filter(x -> line.equals(x.getLine()))
				.findFirst()
				.orElseThrow();
		Attributes equivalent = underTest.getAbilityEquivalent(specialAbility, playerProfile, null, null);

		assertThat(equivalent.getSpellHasteRating()).isEqualTo(29.17, PRECISION);
	}

	@Test
	@DisplayName("Correct spell stats")
	void correctSpellStats() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		SpellStatistics spellStatistics = underTest.getSpellStatistics(playerProfile, null);

		assertThat(spellStatistics.getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5114);
		assertThat(spellStatistics.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2683);
		assertThat(spellStatistics.getCastTime().getSeconds()).isEqualTo(1.91, PRECISION);
		assertThat(spellStatistics.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(spellStatistics.getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(12);
	}

	@Test
	@DisplayName("Correct player spell stats")
	void correctPlayerSpellStats() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		PlayerSpellStats playerSpellStats = underTest.getPlayerSpellStats(playerProfile, null);

		assertThat(playerSpellStats.getHitSpEqv()).isEqualTo(0.11, PRECISION);
		assertThat(playerSpellStats.getCritSpEqv()).isEqualTo(9.98, PRECISION);
		assertThat(playerSpellStats.getHasteSpEqv()).isEqualTo(10.87, PRECISION);
	}
}
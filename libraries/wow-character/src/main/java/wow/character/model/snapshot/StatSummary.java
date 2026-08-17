package wow.character.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.spell.SpellSchool;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-10-29
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatSummary {
	private BaseStatsSnapshot baseStatsSnapshot;
	private int spellPower;
	private int spellDamage;
	private Map<SpellSchool, Integer> spellDamageBySchool;
	private Map<SpellSchool, Double> spellDamagePctBySchool;
	private int spellHealing;
	private double spellHitPctBonus;
	private double spellHitPct;
	private double spellCritPct;
	private double spellHastePct;
	private int spellHitRating;
	private int spellCritRating;
	private int spellHasteRating;
	private int outOfCombatHealthRegen;
	private int inCombatHealthRegen;
	private int uninterruptedManaRegen;
	private int interruptedManaRegen;

	public int getStrength() {
		return baseStatsSnapshot.getStrength();
	}

	public int getAgility() {
		return baseStatsSnapshot.getAgility();
	}

	public int getStamina() {
		return baseStatsSnapshot.getStamina();
	}

	public int getIntellect() {
		return baseStatsSnapshot.getIntellect();
	}

	public int getSpirit() {
		return baseStatsSnapshot.getSpirit();
	}

	public int getMaxHealth() {
		return baseStatsSnapshot.getMaxHealth();
	}

	public int getMaxMana() {
		return baseStatsSnapshot.getMaxMana();
	}

	public int getSpellDamage(SpellSchool school) {
		return spellDamageBySchool.getOrDefault(school, 0);
	}

	public Double getSpellDamagePct(SpellSchool school) {
		return spellDamagePctBySchool.getOrDefault(school, 0.0);
	}

	public StatSummary difference(StatSummary other) {
		return new StatSummary(
				baseStatsSnapshot.difference(other.baseStatsSnapshot),
				spellPower - other.spellPower,
				spellDamage - other.spellDamage,
				getDamageBySchoolDifference(other),
				getDamagePctBySchoolDifference(other),
				spellHealing - other.spellHealing,
				spellHitPctBonus - other.spellHitPctBonus,
				spellHitPct - other.spellHitPct,
				spellCritPct - other.spellCritPct,
				spellHastePct - other.spellHastePct,
				spellHitRating - other.spellHitRating,
				spellCritRating - other.spellCritRating,
				spellHasteRating - other.spellHasteRating,
				outOfCombatHealthRegen - other.outOfCombatHealthRegen,
				inCombatHealthRegen - other.inCombatHealthRegen,
				uninterruptedManaRegen - other.uninterruptedManaRegen,
				interruptedManaRegen - other.interruptedManaRegen
		);
	}

	private Map<SpellSchool, Integer> getDamageBySchoolDifference(StatSummary other) {
		return getDiff(other, StatSummary::getSpellDamageBySchool, (x, y) -> x - y);
	}

	private Map<SpellSchool, Double> getDamagePctBySchoolDifference(StatSummary other) {
		return getDiff(other, StatSummary::getSpellDamagePctBySchool, (x, y) -> x - y);
	}

	private <T> Map<SpellSchool, T> getDiff(StatSummary other, Function<StatSummary, Map<SpellSchool, T>> getter, BinaryOperator<T> operator) {
		var result = new EnumMap<SpellSchool, T>(SpellSchool.class);

		for (var entry : getter.apply(this).entrySet()) {
			var school = entry.getKey();
			var value = entry.getValue();
			var otherValue = getter.apply(other).get(school);

			result.put(school, operator.apply(value, otherValue));
		}

		return result;
	}
}

package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.SpellSchool;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static wow.character.service.impl.calc.BaseStatsCalculations.getBaseStatsSnapshot;
import static wow.character.service.impl.calc.CastCalculations.getHastePct;
import static wow.character.service.impl.calc.ComponentCalculations.getSpellCritPct;
import static wow.character.service.impl.calc.HitCalculations.getSpellHitPct;
import static wow.character.service.impl.calc.HitCalculations.getSpellHitPctBonus;
import static wow.character.service.impl.calc.RegenCalculations.getRegenSnapshot;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.SpellSchool.magicSpellSchoolsStream;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class StatsSummaryCalculations extends Calculations {
	public static StatSummary getStatSummary(Character character) {
		var snapshot = new StatSummary();

		var baseStatsSnapshot = getBaseStatsSnapshot(character);

		var conditionArgs = AttributeConditionArgs.forAnySpell(character);
		var levelDifference = 3;

		var castStats = new AccumulatedCastStats(conditionArgs);
		var hitStats = new AccumulatedHitStats(conditionArgs);
		var spellStats = new AccumulatedSpellStats(conditionArgs);
		var targetStats = new AccumulatedTargetStats(conditionArgs);

		accumulateEffects(character, castStats);
		accumulateEffects(character, hitStats);
		accumulateEffects(character, spellStats);

		var regenSnapshot = getRegenSnapshot(character);

		snapshot.setBaseStatsSnapshot(baseStatsSnapshot);
		snapshot.setSpellPower((int) spellStats.getPower());
		snapshot.setSpellDamage(getSpellDamage(character));
		snapshot.setSpellDamageBySchool(getSpellDamageBySchool(character));
		snapshot.setSpellDamagePctBySchool(getSpellDamagePctBySchool(character));
		snapshot.setSpellHealing(getSpellHealing(character));
		snapshot.setSpellHitPctBonus(getSpellHitPctBonus(character, hitStats));
		snapshot.setSpellHitPct(getSpellHitPct(character, hitStats, levelDifference));
		snapshot.setSpellCritPct(getSpellCritPct(character, spellStats, targetStats));
		snapshot.setSpellHastePct(getHastePct(character, castStats));
		snapshot.setSpellHitRating((int) hitStats.getHitRating());
		snapshot.setSpellCritRating((int) spellStats.getCritRating());
		snapshot.setSpellHasteRating((int) castStats.getHasteRating());
		snapshot.setOutOfCombatHealthRegen(regenSnapshot.getOutOfCombatHealthRegen());
		snapshot.setInCombatHealthRegen(regenSnapshot.getInCombatHealthRegen());
		snapshot.setUninterruptedManaRegen(regenSnapshot.getUninterruptedManaRegen());
		snapshot.setInterruptedManaRegen(regenSnapshot.getInterruptedManaRegen());

		return snapshot;
	}

	private static Map<SpellSchool, Integer> getSpellDamageBySchool(Character caster) {
		return magicSpellSchoolsStream().collect(Collectors.toMap(
				Function.identity(),
				school -> getSpellDamage(caster, school)
		));
	}

	private static Map<SpellSchool, Double> getSpellDamagePctBySchool(Character caster) {
		return magicSpellSchoolsStream().collect(Collectors.toMap(
				Function.identity(),
				school -> getSpellDamagePct(caster, school)
		));
	}

	private static int getSpellDamage(Character caster, SpellSchool school) {
		return getSpellAmount(caster, SPELL_DAMAGE, school);
	}

	private static int getSpellDamage(Character caster) {
		return getSpellDamage(caster, null);
	}

	private static double getSpellDamagePct(Character caster, SpellSchool school) {
		return getSpellPctAmount(caster, SPELL_DAMAGE, school);
	}

	private static int getSpellHealing(Character caster) {
		return getSpellAmount(caster, HEALING, null);
	}

	private static int getSpellAmount(Character caster, PowerType powerType, SpellSchool school) {
		var conditionArgs = AttributeConditionArgs.forAnySpell(caster, powerType, school);
		var spellStats = new AccumulatedSpellStats(conditionArgs);

		accumulateEffects(caster, spellStats);
		return (int) spellStats.getPower();
	}

	private static double getSpellPctAmount(Character caster, PowerType powerType, SpellSchool school) {
		var conditionArgs = AttributeConditionArgs.forAnySpell(caster, powerType, school);
		var spellStats = new AccumulatedSpellStats(conditionArgs);

		accumulateEffects(caster, spellStats);
		return spellStats.getAmountPct();
	}
}

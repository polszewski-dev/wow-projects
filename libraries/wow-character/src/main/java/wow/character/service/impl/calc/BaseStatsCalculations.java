package wow.character.service.impl.calc;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedBaseStats;
import wow.character.model.snapshot.BaseStatsSnapshot;
import wow.character.util.AttributeConditionArgs;

import static wow.commons.constant.SpellConstants.HEALTH_PER_STAMINA;
import static wow.commons.constant.SpellConstants.MANA_PER_INTELLECT;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class BaseStatsCalculations extends Calculations {
	public static AccumulatedBaseStats newAccumulatedBaseStats(Character character) {
		var conditionArgs = AttributeConditionArgs.forBaseStats(character);

		return new AccumulatedBaseStats(conditionArgs);
	}

	private static AccumulatedBaseStats getAccumulatedBaseStats(Character character) {
		var stats = newAccumulatedBaseStats(character);

		accumulateEffects(character, stats);
		return stats;
	}

	public static BaseStatsSnapshot getBaseStatsSnapshot(Character character) {
		var stats = getAccumulatedBaseStats(character);

		return getBaseStatsSnapshot(character, stats);
	}

	public static BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedBaseStats stats) {
		var snapshot = new BaseStatsSnapshot();

		var strength = (int) stats.getTotalStrength();
		var agility = (int) stats.getTotalAgility();
		var stamina = (int) stats.getTotalStamina();
		var intellect = (int) stats.getTotalIntellect();
		var spirit = (int) stats.getTotalSpirit();
		var maxHealth = getMaxHealth(character.getBaseStatInfo(), stats, stamina);
		var maxMana = getMaxMana(character.getBaseStatInfo(), stats, intellect);

		snapshot.setStrength(strength);
		snapshot.setAgility(agility);
		snapshot.setStamina(stamina);
		snapshot.setIntellect(intellect);
		snapshot.setSpirit(spirit);
		snapshot.setMaxHealth(maxHealth);
		snapshot.setMaxMana(maxMana);

		return snapshot;
	}

	private static int getMaxHealth(BaseStatInfo baseStatInfo, AccumulatedBaseStats stats, double stamina) {
		var healthBonus = HEALTH_PER_STAMINA * (stamina - baseStatInfo.getBaseStamina()) + stats.getMaxHealth();
		var healthBonusPct = stats.getMaxHealthPct();

		return (int) addAndMultiplyByPct(0, healthBonus, healthBonusPct);
	}

	private static int getMaxMana(BaseStatInfo baseStatInfo, AccumulatedBaseStats stats, double intellect) {
		var manaBonus = MANA_PER_INTELLECT * (intellect - baseStatInfo.getBaseIntellect()) + stats.getMaxMana();
		var manaBonusPct = stats.getMaxManaPct();

		return (int) addAndMultiplyByPct(0, manaBonus, manaBonusPct);
	}
}

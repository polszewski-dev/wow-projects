package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.talent.TalentTree;

import java.util.Objects;

import static wow.commons.model.attribute.AttributeCondition.EmptyCondition;
import static wow.commons.model.attribute.AttributeCondition.Operator;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
@Getter
public final class AccumulatedOwnerStats extends AccumulatedPartialStats {
	private double strength;
	private double strengthPct;
	private double agility;
	private double agilityPct;
	private double stamina;
	private double staminaPct;
	private double intellect;
	private double intellectPct;
	private double spirit;
	private double spiritPct;
	private double baseStats;
	private double baseStatsPct;
	private double stats;
	private double statsPct;
	private double maxHealth;
	private double maxHealthPct;
	private double maxMana;
	private double maxManaPct;
	private double hp5;
	private double mp5;
	private double healingTakenPct;
	private double healthRegenPct;
	private double manaRegenPct;
	private double inCombatHealthRegenPct;
	private double inCombatManaRegenPct;

	private final Character owner;

	private AccumulatedOwnerStats(Character owner) {
		this.owner = owner;
	}

	public static AccumulatedOwnerStats forBaseStats(Character owner) {
		Objects.requireNonNull(owner);

		return new AccumulatedOwnerStats(owner);
	}

	public static AccumulatedOwnerStats forRegen(Character owner) {
		Objects.requireNonNull(owner);

		return new AccumulatedOwnerStats(owner);
	}

	private AccumulatedOwnerStats(AccumulatedOwnerStats stats) {
		this(stats.owner);
		this.strength = stats.strength;
		this.strengthPct = stats.strengthPct;
		this.agility = stats.agility;
		this.agilityPct = stats.agilityPct;
		this.stamina = stats.stamina;
		this.staminaPct = stats.staminaPct;
		this.intellect = stats.intellect;
		this.intellectPct = stats.intellectPct;
		this.spirit = stats.spirit;
		this.spiritPct = stats.spiritPct;
		this.baseStats = stats.baseStats;
		this.baseStatsPct = stats.baseStatsPct;
		this.stats = stats.stats;
		this.statsPct = stats.statsPct;
		this.maxHealth = stats.maxHealth;
		this.maxHealthPct = stats.maxHealthPct;
		this.maxMana = stats.maxMana;
		this.maxManaPct = stats.maxManaPct;
		this.hp5 = stats.hp5;
		this.mp5 = stats.mp5;
		this.healingTakenPct = stats.healingTakenPct;
		this.healthRegenPct = stats.healthRegenPct;
		this.manaRegenPct = stats.manaRegenPct;
		this.inCombatHealthRegenPct = stats.inCombatHealthRegenPct;
		this.inCombatManaRegenPct = stats.inCombatManaRegenPct;
	}

	public AccumulatedOwnerStats copy() {
		return new AccumulatedOwnerStats(this);
	}

	@Override
	public int getLevel() {
		return owner.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return 0;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case STRENGTH:
				this.strength += getValue(attribute, scaleFactor);
				break;
			case STRENGTH_PCT:
				this.strengthPct += getValue(attribute, scaleFactor);
				break;
			case AGILITY:
				this.agility += getValue(attribute, scaleFactor);
				break;
			case AGILITY_PCT:
				this.agilityPct += getValue(attribute, scaleFactor);
				break;
			case STAMINA:
				this.stamina += getValue(attribute, scaleFactor);
				break;
			case STAMINA_PCT:
				this.staminaPct += getValue(attribute, scaleFactor);
				break;
			case INTELLECT:
				this.intellect += getValue(attribute, scaleFactor);
				break;
			case INTELLECT_PCT:
				this.intellectPct += getValue(attribute, scaleFactor);
				break;
			case SPIRIT:
				this.spirit += getValue(attribute, scaleFactor);
				break;
			case SPIRIT_PCT:
				this.spiritPct += getValue(attribute, scaleFactor);
				break;
			case BASE_STATS:
				this.baseStats += getValue(attribute, scaleFactor);
				break;
			case BASE_STATS_PCT:
				this.baseStatsPct += getValue(attribute, scaleFactor);
				break;
			case STATS:
				this.stats += getValue(attribute, scaleFactor);
				break;
			case STATS_PCT:
				this.statsPct += getValue(attribute, scaleFactor);
				break;
			case MAX_HEALTH:
				this.maxHealth += getValue(attribute, scaleFactor);
				break;
			case MAX_HEALTH_PCT:
				this.maxHealthPct += getValue(attribute, scaleFactor);
				break;
			case MAX_MANA:
				this.maxMana += getValue(attribute, scaleFactor);
				break;
			case MAX_MANA_PCT:
				this.maxManaPct += getValue(attribute, scaleFactor);
				break;
			case HP5:
				this.hp5 += getValue(attribute, scaleFactor);
				break;
			case MP5:
				this.mp5 += getValue(attribute, scaleFactor);
				break;
			case HEALING_TAKEN_PCT:
				this.healingTakenPct += getValue(attribute, scaleFactor);
				break;
			case HEALTH_REGEN_PCT:
				this.healthRegenPct += getValue(attribute, scaleFactor);
				break;
			case MANA_REGEN_PCT:
				this.manaRegenPct += getValue(attribute, scaleFactor);
				break;
			case IN_COMBAT_HEALTH_REGEN_PCT:
				this.inCombatHealthRegenPct += getValue(attribute, scaleFactor);
				break;
			case IN_COMBAT_MANA_REGEN_PCT:
				this.inCombatManaRegenPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public double getTotalStrength() {
		var baseStatInfo = owner.getBaseStatInfo();

		return getPrimaryStat(baseStatInfo.getBaseStrength() + baseStats, baseStatsPct, strength + stats, strengthPct + statsPct);
	}

	public double getTotalAgility() {
		var baseStatInfo = owner.getBaseStatInfo();

		return getPrimaryStat(baseStatInfo.getBaseAgility() + baseStats, baseStatsPct, agility + stats, agilityPct + statsPct);
	}

	public double getTotalStamina() {
		var baseStatInfo = owner.getBaseStatInfo();

		return getPrimaryStat(baseStatInfo.getBaseStamina() + baseStats, baseStatsPct, stamina + stats, staminaPct + statsPct);
	}

	public double getTotalIntellect() {
		var baseStatInfo = owner.getBaseStatInfo();

		return getPrimaryStat(baseStatInfo.getBaseIntellect() + baseStats, baseStatsPct, intellect + stats, intellectPct + statsPct);
	}

	public double getTotalSpirit() {
		var baseStatInfo = owner.getBaseStatInfo();

		return getPrimaryStat(baseStatInfo.getBaseSpirit() + baseStats, baseStatsPct, spirit + stats, spiritPct + statsPct);
	}

	static double getPrimaryStat(double base, double basePct, double bonus, double bonusPct) {
		return (base * (1 + basePct / 100) + bonus) * (1 + bonusPct / 100);
	}

	@Override
	protected boolean checkCondition(AttributeCondition condition) {
		return switch (condition) {
			case EmptyCondition() ->
					true;

			case Operator operator ->
					checkConditionOperator(operator);

			default ->
					unsupported(condition);
		};
	}

	@Override
	public ActionType getActionType() {
		return null;
	}

	@Override
	public PowerType getPowerType() {
		return null;
	}
}

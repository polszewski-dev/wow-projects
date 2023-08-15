package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.GameVersion;
import wow.character.model.snapshot.AccumulatedSpellStats;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SnapshotState;
import wow.character.service.CharacterCalculationService;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Cost;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static wow.commons.constants.SpellConstants.*;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
@Service
@AllArgsConstructor
public class CharacterCalculationServiceImpl implements CharacterCalculationService {
	@Override
	public Snapshot createSnapshot(Character character, Spell spell, Attributes totalStats) {
		AccumulatedSpellStats stats = new AccumulatedSpellStats(
				totalStats,
				character.getConditions(spell)
		);

		stats.accumulateBaseStats(character.getBaseStatInfo());
		stats.accumulatePrimitiveAttributes();

		return new Snapshot(spell, character, stats);
	}

	@Override
	public void advanceSnapshot(Snapshot snapshot, SnapshotState targetState) {
		for (SnapshotState state : SnapshotState.values()) {
			if (state.compareTo(snapshot.getState()) > 0 && state.compareTo(targetState) <= 0) {
				stateBasedCalc(snapshot, state);
				snapshot.setState(state);
			}
		}
	}

	private void stateBasedCalc(Snapshot snapshot, SnapshotState state) {
		switch (state) {
			case BASE_STATS:
				calcBaseStats(snapshot);
				break;
			case SECONDARY_STATS:
				calcSecondaryStats(snapshot);
				break;
			case CAST_TIME:
				calcCastTime(snapshot);
				break;
			case SPELL_STATS:
				calcSpellStats(snapshot);
				break;
			case COST:
				calcCost(snapshot);
				break;
			case DURATION:
				calcDuration(snapshot);
				break;
			case COOLDOWN:
				calcCooldown(snapshot);
				break;
			case MISC:
				calcMisc(snapshot);
				break;
			case INITIAL, COMPLETE:
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + state);
		}
	}

	private void calcBaseStats(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();

		double stamina = getBaseStat(stats, stats.getStamina(), stats.getStaminaPct());
		double intellect = getBaseStat(stats, stats.getIntellect(), stats.getIntellectPct());
		double spirit = getBaseStat(stats, stats.getSpirit(), stats.getSpiritPct());

		snapshot.setStamina(stamina);
		snapshot.setIntellect(intellect);
		snapshot.setSpirit(spirit);

		snapshot.setMaxHealth(getMaxHealth(snapshot));
		snapshot.setMaxMana(getMaxMana(snapshot));
	}

	private void calcSecondaryStats(Snapshot snapshot) {
		calcHit(snapshot);
		calcCrit(snapshot);
		calcHaste(snapshot);
	}

	private void calcHit(Snapshot snapshot) {
		snapshot.setTotalHit(getTotalHit(snapshot));
		snapshot.setHitChance(getHitChance(snapshot));
	}

	private void calcCrit(Snapshot snapshot) {
		snapshot.setTotalCrit(getTotalCrit(snapshot));
		snapshot.setCritChance(getCritChance(snapshot));
		snapshot.setCritCoeff(getCritCoeff(snapshot));
	}

	private void calcHaste(Snapshot snapshot) {
		snapshot.setTotalHaste(getTotalHaste(snapshot));
		snapshot.setHaste(getHaste(snapshot));
	}

	private void calcSpellStats(Snapshot snapshot) {
		calcSpellPower(snapshot);
		calcSpellCoefficients(snapshot);
	}

	private void calcSpellPower(Snapshot snapshot) {
		snapshot.setSp(snapshot.getStats().getSpellDamage());
		snapshot.setSpMultiplier(getSpMultiplier(snapshot));
		snapshot.setDirectDamageDoneMultiplier(getDirectDamageDoneMultiplier(snapshot));
		snapshot.setDotDamageDoneMultiplier(getDotDamageDoneMultiplier(snapshot));
	}

	private void calcSpellCoefficients(Snapshot snapshot) {
		snapshot.setSpellCoeffDirect(getSpellCoeffDirect(snapshot));
		snapshot.setSpellCoeffDoT(getSpellCoeffDoT(snapshot));
	}

	private void calcCastTime(Snapshot snapshot) {
		double castTime = getCastTime(snapshot);
		double gcd = getGcd(snapshot);

		snapshot.setCastTime(castTime);
		snapshot.setGcd(gcd);
		snapshot.setEffectiveCastTime(max(castTime, gcd));
		snapshot.setInstantCast(castTime == 0);
	}

	private void calcCost(Snapshot snapshot) {
		double baseCost = getBaseCost(snapshot);
		double costPct = snapshot.getStats().getCostPct();
		double costReductionPct = snapshot.getStats().getCostReductionPct();
		double cost = getCost(baseCost, costPct - costReductionPct);
		double costUnreduced = getCost(baseCost, costPct);

		snapshot.setCost(cost);
		snapshot.setCostUnreduced(costUnreduced);
	}

	private void calcCooldown(Snapshot snapshot) {
		snapshot.setCooldown(getCooldown(snapshot));
	}

	private void calcDuration(Snapshot snapshot) {
		snapshot.setDuration(getDuration(snapshot));
	}

	private void calcMisc(Snapshot snapshot) {
		snapshot.setThreatPct(getThreatPct(snapshot));
		snapshot.setPushbackPct(getPushbackPct(snapshot));
	}

	private double getBaseStat(AccumulatedSpellStats stats, double statPoints, double statPct) {
		double base = stats.getBaseStats() + statPoints;
		double baseMultiplier = 1 + (stats.getBaseStatsPct() + statPct) / 100;
		return base * baseMultiplier;
	}

	private double getTotalHit(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		CombatRatingInfo cr = snapshot.getCharacter().getCombatRatingInfo();

		double ratingHit = stats.getHitRating() / cr.getSpellHit();
		double pctHit = stats.getHitPct();

		return ratingHit + pctHit;
	}

	private double getHitChance(Snapshot snapshot) {
		GameVersion gameVersion = snapshot.getCharacter().getGameVersion();
		int levelDifference = snapshot.getCharacter().getTargetEnemy().getLevelDifference();

		double baseSpellHitChancePct = gameVersion.getBaseSpellHitChancePct(levelDifference);
		double maxSpellHitChancePct = gameVersion.getMaxPveSpellHitChancePct();
		double totalHit = snapshot.getTotalHit();

		return min(baseSpellHitChancePct + totalHit, maxSpellHitChancePct) / 100;
	}

	private double getTotalCrit(Snapshot snapshot) {
		Spell spell = snapshot.getSpell();

		if (spell != null && !spell.hasDirectComponent()) {
			return 0;
		}

		AccumulatedSpellStats stats = snapshot.getStats();
		BaseStatInfo baseStats = snapshot.getCharacter().getBaseStatInfo();
		CombatRatingInfo cr = snapshot.getCharacter().getCombatRatingInfo();

		double intellect = snapshot.getIntellect();
		double intCrit = (intellect - baseStats.getBaseIntellect()) / baseStats.getIntellectPerCritPct();
		double ratingCrit = stats.getCritRating() / cr.getSpellCrit();
		double pctCrit = stats.getCritPct();

		return intCrit + ratingCrit + pctCrit;
	}

	private double getCritChance(Snapshot snapshot) {
		double totalCrit = snapshot.getTotalCrit();

		return min(totalCrit, 100) / 100;
	}

	private double getCritCoeff(Snapshot snapshot) {
		Spell spell = snapshot.getSpell();

		if (spell != null && !spell.hasDirectComponent()) {
			return 0;
		}

		AccumulatedSpellStats stats = snapshot.getStats();

		double increasedCriticalDamage = stats.getCritDamagePct() / 100;
		double talentIncrease = stats.getCritDamageMultiplierPct() / 100;
		double extraCritCoeff = stats.getCritCoeffPct();

		return 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;
	}

	private double getTotalHaste(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		CombatRatingInfo cr = snapshot.getCharacter().getCombatRatingInfo();

		double ratingHaste = stats.getHasteRating() / cr.getSpellHaste();
		double pctHaste = stats.getHastePct();

		return pctHaste + ratingHaste;
	}

	private double getHaste(Snapshot snapshot) {
		double totalHaste = snapshot.getTotalHaste();

		return totalHaste / 100;
	}

	private double getSpMultiplier(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();

		return 1 + stats.getSpellDamagePct() / 100;
	}

	private double getDirectDamageDoneMultiplier(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();

		return 1 + (stats.getEffectIncreasePct() + stats.getDamagePct() + stats.getDirectDamagePct()) / 100;
	}

	private double getDotDamageDoneMultiplier(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();

		return 1 + (stats.getEffectIncreasePct() + stats.getDamagePct() + stats.getDotDamagePct()) / 100;
	}

	private double getSpellCoeffDirect(Snapshot snapshot) {
		Spell spell = snapshot.getSpell();

		if (!spell.hasDirectComponent()) {
			return 0;
		}

		AccumulatedSpellStats stats = snapshot.getStats();

		double baseSpellCoeffDirect = spell.getCoeffDirect().getCoefficient();
		double talentSpellCoeff = stats.getSpellPowerCoeffPct() / 100;
		return baseSpellCoeffDirect + talentSpellCoeff;
	}

	private double getSpellCoeffDoT(Snapshot snapshot) {
		Spell spell = snapshot.getSpell();

		if (!spell.hasDotComponent()) {
			return 0;
		}

		AccumulatedSpellStats stats = snapshot.getStats();

		double baseSpellCoeffDoT = spell.getCoeffDot().getCoefficient();
		double talentSpellCoeff = stats.getSpellPowerCoeffPct() / 100;
		return baseSpellCoeffDoT + talentSpellCoeff;
	}

	private double getCastTime(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		Spell spell = snapshot.getSpell();

		double castTimeChange = stats.getCastTime();
		double baseCastTime = spell.getCastTime().getSeconds();
		double changedCastTime = max(baseCastTime + castTimeChange, 0);
		double haste = snapshot.getHaste();

		return changedCastTime / (1 + haste);
	}

	private double getGcd(Snapshot snapshot) {
		double haste = snapshot.getHaste();

		return max(GCD.getSeconds() / (1 + haste), MIN_GCD.getSeconds());
	}

	private double getCost(double baseCost, double costModifierPct) {
		return baseCost * (1 + costModifierPct / 100);
	}

	private double getBaseCost(Snapshot snapshot) {
		Cost cost = snapshot.getSpell().getCost();

		double baseCost = cost.amount();

		if (!cost.baseStatPct().isZero()) {
			double baseStatValue = getBaseStatValue(cost.resourceType(), snapshot);
			double pct = cost.baseStatPct().getCoefficient();

			baseCost += baseStatValue * pct;
		}

		if (!cost.coeff().isZero()) {
			double sp = snapshot.getSp();
			double coeff = cost.coeff().getCoefficient();

			baseCost += sp * coeff;
		}

		return baseCost;
	}

	private double getBaseStatValue(ResourceType resourceType, Snapshot snapshot) {
		BaseStatInfo baseStatInfo = snapshot.getCharacter().getBaseStatInfo();

		return switch (resourceType) {
			case HEALTH -> baseStatInfo.getBaseHealth();
			case MANA -> baseStatInfo.getBaseMana();
			case PET_MANA -> throw new IllegalArgumentException("pets not supported");
		};
	}

	private double getCooldown(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		Spell spell = snapshot.getSpell();

		double originalCooldown = spell.getCooldown().getSeconds();

		if (stats.getCooldown() == 0 && stats.getCooldownPct() == 0) {
			return originalCooldown;
		}

		return addAndMultiplyByPct(
				originalCooldown,
				stats.getCooldown(),
				stats.getCooldownPct()
		);
	}

	private double getDuration(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		Spell spell = snapshot.getSpell();

		if (stats.getDuration() == 0 && stats.getDurationPct() == 0) {
			if (spell.hasDotComponent()) {
				return spell.getDotDuration().getSeconds();
			}
			return 0;
		}

		if (spell.isChanneled()) {
			throw new IllegalArgumentException("Channeled spell");
		}

		if (!spell.hasDotComponent()) {
			throw new IllegalArgumentException("No DoT component");
		}

		return addAndMultiplyByPct(
				spell.getDotDuration().getSeconds(),
				stats.getDuration(),
				stats.getDurationPct()
		);
	}

	private double getThreatPct(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();

		return max(100 + stats.getThreatPct(), 0);
	}

	private double getPushbackPct(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();

		return min(max(100 + stats.getPushbackPct(), 0), 100);
	}

	private double getMaxHealth(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		BaseStatInfo baseStats = snapshot.getCharacter().getBaseStatInfo();
		double stamina = snapshot.getStamina();

		return addAndMultiplyByPct(
				baseStats.getBaseHealth(),
				HEALTH_PER_STAMINA * (stamina - baseStats.getBaseStamina()) + stats.getMaxHealth(),
				stats.getMaxHealthPct()
		);
	}

	private double getMaxMana(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		BaseStatInfo baseStats = snapshot.getCharacter().getBaseStatInfo();
		double intellect = snapshot.getIntellect();

		return addAndMultiplyByPct(
				baseStats.getBaseMana(),
				MANA_PER_INTELLECT * (intellect - baseStats.getBaseIntellect()) + stats.getMaxMana(),
				stats.getMaxManaPct()
		);
	}

	private static double addAndMultiplyByPct(double value, double modifier, double modifierPct) {
		return max(value + modifier, 0) * max(1 + modifierPct / 100, 0);
	}
}

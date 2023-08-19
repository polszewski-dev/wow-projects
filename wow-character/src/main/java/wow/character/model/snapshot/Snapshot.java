package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.Duration;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.TickScheme;

import static wow.character.model.snapshot.SnapshotState.INITIAL;

/**
 * User: POlszewski
 * Date: 2021-08-20
 */
@Getter
@Setter
public class Snapshot {
	private final Character character;
	private final Spell spell;
	private final AccumulatedSpellStats stats;

	private SnapshotState state = INITIAL;

	private double stamina;
	private double intellect;
	private double spirit;

	private double totalHit;
	private double totalCrit;
	private double totalHaste;

	private double sp;
	private double spMultiplier;

	private double hitChance;
	private double critChance;
	private double critCoeff;
	private double haste;

	private double directDamageDoneMultiplier;
	private double dotDamageDoneMultiplier;

	private double spellCoeffDirect;
	private double spellCoeffDoT;

	private double castTime;
	private double gcd;
	private double effectiveCastTime;// max(castTime, gcd)
	private boolean instantCast;

	private double cost;
	private double costUnreduced;

	private double duration;
	private double cooldown;

	private double threatPct;
	private double pushbackPct;

	private double maxHealth;
	private double maxMana;

	private TickScheme tickScheme;

	public Snapshot(Spell spell, Character character, AccumulatedSpellStats stats) {
		this.character = character;
		this.spell = spell;
		this.stats = stats;
	}

	public SpellStatistics getSpellStatistics(RngStrategy rngStrategy, boolean useBothDamageRanges) {
		SpellStatistics result = new SpellStatistics();

		result.setSnapshot(this);
		result.setTotalDamage(getTotalDamage(rngStrategy, useBothDamageRanges));
		result.setCastTime(Duration.seconds(effectiveCastTime));
		result.setDps(result.getTotalDamage() / result.getCastTime().getSeconds());
		result.setManaCost(cost);
		result.setDpm(result.getTotalDamage() / result.getManaCost());

		return result;
	}

	public double getTotalDamage(RngStrategy rngStrategy, boolean useBothDamageRanges) {
		return getDirectDamage(rngStrategy, useBothDamageRanges) + getDotDamage(rngStrategy);
	}

	public double getDirectDamage(RngStrategy rngStrategy, boolean useBothDamageRanges) {
		if (!spell.hasDirectComponent()) {
			return 0;
		}

		int baseDmgMin = spell.getMinDmg();
		int baseDmgMax = spell.getMaxDmg();

		if (useBothDamageRanges) {
			baseDmgMin += spell.getMinDmg2();
			baseDmgMax += spell.getMaxDmg2();
		}

		double actualHitChance = rngStrategy.getHitChance(hitChance);
		double actualCritChance = rngStrategy.getCritChance(critChance);

		double directDamage = rngStrategy.getDamage(baseDmgMin, baseDmgMax);
		directDamage += spellCoeffDirect * sp * spMultiplier;
		directDamage *= directDamageDoneMultiplier;
		directDamage *= actualHitChance;
		directDamage *= (1 - actualCritChance) * 1 + actualCritChance * critCoeff;
		return directDamage;
	}

	public double getDotDamage(RngStrategy rngStrategy) {
		if (!spell.hasDotComponent()) {
			return 0;
		}

		double actualHitChance = rngStrategy.getHitChance(hitChance);

		double dotDamage = spell.getDotDmg();
		dotDamage += spellCoeffDoT * sp * spMultiplier;
		dotDamage *= dotDamageDoneMultiplier;
		dotDamage *= actualHitChance;
		dotDamage *= getDoTDamageChange();
		return dotDamage;
	}

	private double getDoTDamageChange() {
		double originalWeights = spell.getTickScheme().weightSum();
		double adjustedWeights = getTickScheme().weightSum();
		return adjustedWeights / originalWeights;
	}

	public TickScheme getTickScheme() {
		if (tickScheme == null) {
			tickScheme = spell.getTickScheme().adjustBaseDuration(Duration.seconds(duration));
		}
		return tickScheme;
	}
}

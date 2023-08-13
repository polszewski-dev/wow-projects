package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.Duration;
import wow.commons.model.spells.Spell;

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

	private double duration;
	private double cooldown;

	private double threatPct;
	private double pushbackPct;

	private double maxHealth;
	private double maxMana;

	public Snapshot(Spell spell, Character character, AccumulatedSpellStats stats) {
		this.character = character;
		this.spell = spell;
		this.stats = stats;
	}

	public SpellStatistics getSpellStatistics(CritMode critMode, boolean useBothDamageRanges) {
		SpellStatistics result = new SpellStatistics();

		result.setSnapshot(this);
		result.setTotalDamage(getTotalDamage(critMode, useBothDamageRanges));
		result.setCastTime(Duration.seconds(effectiveCastTime));
		result.setDps(result.getTotalDamage() / result.getCastTime().getSeconds());
		result.setManaCost(cost);
		result.setDpm(result.getTotalDamage() / result.getManaCost());

		return result;
	}

	public double getTotalDamage(CritMode critMode, boolean useBothDamageRanges) {
		return getDirectDamage(critMode, useBothDamageRanges) + getDotDamage();
	}

	public double getDirectDamage(CritMode critMode, boolean useBothDamageRanges) {
		if (!spell.hasDirectComponent()) {
			return 0;
		}

		int baseDmgMin = spell.getMinDmg();
		int baseDmgMax = spell.getMaxDmg();

		if (useBothDamageRanges) {
			baseDmgMin += spell.getMinDmg2();
			baseDmgMax += spell.getMaxDmg2();
		}

		double actualCritChance = critMode.getActualCritChance(this);

		double directDamage = (baseDmgMin + baseDmgMax) / 2.0;
		directDamage += spellCoeffDirect * sp * spMultiplier;
		directDamage *= directDamageDoneMultiplier;
		directDamage *= hitChance;
		directDamage *= (1 - actualCritChance) * 1 + actualCritChance * critCoeff;
		return directDamage;
	}

	public double getDotDamage() {
		if (!spell.hasDotComponent()) {
			return 0;
		}

		double dotDamage = spell.getDotDmg();
		dotDamage += spellCoeffDoT * sp * spMultiplier;
		dotDamage *= dotDamageDoneMultiplier;
		dotDamage *= hitChance;
		dotDamage *= 1 + getDoTDamageChange();
		return dotDamage;
	}

	private double getDoTDamageChange() {
		double durationChange = duration - spell.getDotDuration().getSeconds();
		double tickChange = (int)(durationChange / spell.getTickInterval().getSeconds());
		return tickChange / spell.getNumTicks();
	}
}

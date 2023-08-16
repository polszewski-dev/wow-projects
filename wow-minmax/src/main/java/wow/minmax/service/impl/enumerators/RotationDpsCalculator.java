package wow.minmax.service.impl.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.snapshot.RngStrategy;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;
import wow.minmax.service.CalculationService;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@RequiredArgsConstructor
@Getter
public class RotationDpsCalculator {
	private final Character character;
	private final Rotation rotation;
	private final Attributes totalStats;

	private final CalculationService calculationService;

	private double totalDamage = 0;
	private double timeLeft = FIGHT_DURATION;

	private Snapshot fillerSnapshot;
	private double fillerDamage;
	private double fillerEffectiveCastTime;
	private double fillerDps;

	private static final double FIGHT_DURATION = 300;

	public void calculate() {
		setFiller();
		for (Spell spell : rotation.getCooldowns()) {
			addSpell(spell);
		}
		addFillerDamage();
	}

	public double getDps() {
		return totalDamage / FIGHT_DURATION;
	}

	private void setFiller() {
		fillerSnapshot = getSnapshot(rotation.getFiller());
		fillerDamage = getDamage(fillerSnapshot);
		fillerEffectiveCastTime = getEffectiveCastTime(fillerSnapshot);
		fillerDps = fillerDamage / fillerEffectiveCastTime;
	}

	private void addSpell(Spell spell) {
		Snapshot snapshot = getSnapshot(spell);
		double damage = getDamage(snapshot);
		double effectiveCastTime = getEffectiveCastTime(snapshot);
		double dps = damage / effectiveCastTime;

		if (dps <= fillerDps) {
			return;
		}

		double effectiveCooldown = spell.getEffectiveCooldown().getSeconds();
		double numCasts = FIGHT_DURATION / effectiveCooldown;

		totalDamage += numCasts * damage;
		timeLeft -= numCasts * effectiveCastTime;

		if (timeLeft < 0) {
			throw new IllegalArgumentException();
		}

		onRotationSpell(spell, numCasts, damage, snapshot);
	}

	private void addFillerDamage() {
		double numCasts = timeLeft / fillerEffectiveCastTime;

		totalDamage += numCasts * fillerDamage;

		onRotationSpell(rotation.getFiller(), numCasts, fillerDamage, fillerSnapshot);
	}

	private Snapshot getSnapshot(Spell spell) {
		return calculationService.getSnapshot(character, spell, totalStats);
	}

	private double getDamage(Snapshot snapshot) {
		return snapshot.getTotalDamage(RngStrategy.AVERAGED, true);
	}

	private double getEffectiveCastTime(Snapshot fillerSnapshot) {
		return fillerSnapshot.getEffectiveCastTime();
	}

	protected void onRotationSpell(Spell spell, double numCasts, double damage, Snapshot snapshot) {
		// to be overriden
	}
}

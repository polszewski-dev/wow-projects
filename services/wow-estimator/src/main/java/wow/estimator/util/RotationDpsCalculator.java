package wow.estimator.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.build.Rotation;
import wow.commons.model.spell.Ability;
import wow.estimator.model.Snapshot;

import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@RequiredArgsConstructor
@Getter
public class RotationDpsCalculator {
	private final Rotation rotation;
	private final Function<Ability, Snapshot> snapshotFunction;

	private double totalDamage = 0;
	private double timeLeft = FIGHT_DURATION;

	private Snapshot fillerSnapshot;
	private double fillerDamage;
	private double fillerEffectiveCastTime;
	private double fillerDps;

	private static final double FIGHT_DURATION = 300;

	public void calculate() {
		setFiller();
		for (Ability ability : rotation.getCooldowns()) {
			addSpell(ability);
		}
		addFillerDamage();
	}

	public double getDps() {
		return totalDamage / FIGHT_DURATION;
	}

	private void setFiller() {
		fillerSnapshot = getSnapshot(rotation.getFiller());
		fillerDamage = fillerSnapshot.getTotalDamage();
		fillerEffectiveCastTime = fillerSnapshot.getEffectiveCastTime();
		fillerDps = fillerDamage / fillerEffectiveCastTime;
	}

	private void addSpell(Ability ability) {
		Snapshot snapshot = getSnapshot(ability);
		double damage = snapshot.getTotalDamage();
		double effectiveCastTime = snapshot.getEffectiveCastTime();
		double dps = damage / effectiveCastTime;

		if (dps <= fillerDps) {
			return;
		}

		double effectiveCooldown = snapshot.getEffectiveCooldown();
		double numCasts = FIGHT_DURATION / effectiveCooldown;

		totalDamage += numCasts * damage;
		timeLeft -= numCasts * effectiveCastTime;

		if (timeLeft < 0) {
			throw new IllegalArgumentException();
		}

		onRotationSpell(ability, numCasts, damage, snapshot);
	}

	private void addFillerDamage() {
		double numCasts = timeLeft / fillerEffectiveCastTime;

		totalDamage += numCasts * fillerDamage;

		onRotationSpell(rotation.getFiller(), numCasts, fillerDamage, fillerSnapshot);
	}

	private Snapshot getSnapshot(Ability ability) {
		return snapshotFunction.apply(ability);
	}

	protected void onRotationSpell(Ability ability, double numCasts, double damage, Snapshot snapshot) {
		// to be overriden
	}
}

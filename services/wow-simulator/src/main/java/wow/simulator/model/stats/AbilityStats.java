package wow.simulator.model.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
@RequiredArgsConstructor
@Getter
public class AbilityStats {
	private final Ability ability;
	private Duration totalCastTime = Duration.ZERO;
	private int numCasts;
	private int numHit;
	private int numResisted;
	private int numCrit;
	private int totalDamage;

	public void addTotalCastTime(Duration duration) {
		this.totalCastTime = totalCastTime.add(duration);
	}

	public void increaseNumCasts() {
		++numCasts;
	}

	public void increaseNumHit() {
		++numHit;
	}

	public void increaseNumResisted() {
		++numResisted;
	}

	public void increaseTotalDamage(int damage, boolean crit) {
		this.totalDamage += damage;
		if (crit) {
			++this.numCrit;
		}
	}

	public int getDps() {
		if (totalCastTime.isZero()) {
			return 0;
		}
		return (int)(totalDamage / totalCastTime.getSeconds());
	}
}

package wow.evaluator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
@AllArgsConstructor
@Getter
@Setter
public class ProcInfo {
	private int chance;
	private int castTime;
	private int duration;
	private int internalCooldown;
	private double averageUptime;

	public static final double CHANCE_RESOLUTION = 1;
	public static final double CAST_TIME_RESOLUTION = 10;
}
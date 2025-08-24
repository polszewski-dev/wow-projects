package wow.estimator.repository;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
public interface ProcInfoRepository {
	double getAverageUptime(int procChance, int castTime, int duration, int internalCooldown);
}

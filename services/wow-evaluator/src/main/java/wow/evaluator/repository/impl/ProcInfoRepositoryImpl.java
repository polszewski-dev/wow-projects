package wow.evaluator.repository.impl;

import org.springframework.stereotype.Component;
import wow.evaluator.model.ProcInfo;
import wow.evaluator.repository.ProcInfoRepository;
import wow.evaluator.repository.impl.parser.ProcExcelParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
@Component
public class ProcInfoRepositoryImpl implements ProcInfoRepository {
	private record Key(int procChance, int castTime, int duration, int internalCooldown) {}
	private final Map<Key, ProcInfo> procInfoByKey = new HashMap<>();

	public ProcInfoRepositoryImpl(ProcExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getProcInfos().forEach(this::addProcInfo);
	}

	@Override
	public double getAverageUptime(int procChance, int castTime, int duration, int internalCooldown) {
		if (castTime / ProcInfo.CAST_TIME_RESOLUTION > duration) {
			return 0;
		}

		ProcInfo procInfo = getProcInfo(procChance, castTime, duration, internalCooldown);

		return procInfo.getAverageUptime();
	}

	private ProcInfo getProcInfo(int procChance, int castTime, int duration, int internalCooldown) {
		var key = new Key(procChance, castTime, duration, internalCooldown);
		ProcInfo procInfo = procInfoByKey.get(key);

		if (procInfo == null) {
			if (duration == 8) {
				return getProcInfo(procChance, castTime, 10, internalCooldown);
			}
			throw new IllegalArgumentException("Missing entry for " + key);
		}
		return procInfo;
	}

	private void addProcInfo(ProcInfo procInfo) {
		var key = new Key(procInfo.getChance(), procInfo.getCastTime(), procInfo.getDuration(), procInfo.getInternalCooldown());
		procInfoByKey.put(key, procInfo);
	}
}

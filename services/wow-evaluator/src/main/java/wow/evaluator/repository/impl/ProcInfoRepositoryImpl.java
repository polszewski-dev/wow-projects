package wow.evaluator.repository.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.evaluator.model.ProcInfo;
import wow.evaluator.repository.ProcInfoRepository;
import wow.evaluator.repository.impl.parser.ProcExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
@Component
public class ProcInfoRepositoryImpl implements ProcInfoRepository {
	private final Map<String, ProcInfo> procInfoByKey = new HashMap<>();

	@Value("${proc.xls.file.path}")
	private String xlsFilePath;

	@Override
	public double getAverageUptime(int procChance, int castTime, int duration, int internalCooldown) {
		if (castTime / ProcInfo.CAST_TIME_RESOLUTION > duration) {
			return 0;
		}

		ProcInfo procInfo = getProcInfo(procChance, castTime, duration, internalCooldown);

		return procInfo.getAverageUptime();
	}

	private String getKey(int procChance, int castTime, int duration, int internalCooldown) {
		return procChance + "#" + castTime + "#" + duration + "#" + internalCooldown;
	}

	private ProcInfo getProcInfo(int procChance, int castTime, int duration, int internalCooldown) {
		String key = getKey(procChance, castTime, duration, internalCooldown);
		ProcInfo procInfo = procInfoByKey.get(key);

		if (procInfo == null) {
			if (duration == 8) {
				return getProcInfo(procChance, castTime, 10, internalCooldown);
			}
			throw new IllegalArgumentException("Missing entry for " + key);
		}
		return procInfo;
	}

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new ProcExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void addProcInfo(ProcInfo procInfo) {
		String key = getKey(procInfo.getChance(), procInfo.getCastTime(), procInfo.getDuration(), procInfo.getInternalCooldown());
		procInfoByKey.put(key, procInfo);
	}
}

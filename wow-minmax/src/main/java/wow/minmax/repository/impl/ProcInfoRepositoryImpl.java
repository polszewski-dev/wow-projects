package wow.minmax.repository.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.minmax.model.ProcInfo;
import wow.minmax.repository.ProcInfoRepository;
import wow.minmax.repository.impl.parsers.proc.ProcExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
@Repository
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

package wow.commons.repository.impl.parser.pve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.Zone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.ZONES;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ZoneExcelParser extends ExcelParser {
	@Value("${zones.xls.file.path}")
	private final String xlsFilePath;

	@Getter
	private final List<Zone> zones = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ZoneSheetParser(ZONES, this)
		);
	}

	void addZone(Zone zone) {
		zones.add(zone);
	}
}

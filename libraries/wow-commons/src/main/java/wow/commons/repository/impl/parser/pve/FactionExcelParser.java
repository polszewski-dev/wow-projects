package wow.commons.repository.impl.parser.pve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.Faction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.FACTIONS;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class FactionExcelParser extends ExcelParser {
	@Value("${factions.xls.file.path}")
	private final String xlsFilePath;

	@Getter
	private final List<Faction> factions = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new FactionSheetParser(FACTIONS, this)
		);
	}

	void addFaction(Faction faction) {
		factions.add(faction);
	}
}

package wow.commons.repository.impl.parser.pve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.Zone;
import wow.commons.repository.pve.ZoneRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.NPCS;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class NpcExcelParser extends ExcelParser {
	@Value("${npcs.xls.file.path}")
	private final String xlsFilePath;

	private final ZoneRepository zoneRepository;

	@Getter
	private final List<Npc> npcs = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new NpcSheetParser(NPCS, zoneRepository, this)
		);
	}

	void addNpc(Npc npc) {
		addNpcToZones(npc);
		npcs.add(npc);
	}

	private void addNpcToZones(Npc npc) {
		for (Zone zone : npc.getZones()) {
			zone.getNpcs().add(npc);
		}
	}
}

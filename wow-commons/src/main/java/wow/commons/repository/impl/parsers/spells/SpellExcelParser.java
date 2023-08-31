package wow.commons.repository.impl.parsers.spells;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.impl.SpellRepositoryImpl;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parsers.spells.SpellBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@AllArgsConstructor
public class SpellExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SpellRepositoryImpl spellRepository;

	private final Map<SpellId, List<SpellInfo>> spellInfoById = new EnumMap<>(SpellId.class);
	private final Map<TalentId, List<TalentInfo>> talentInfoById = new EnumMap<>(TalentId.class);

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new SpellSheetParser(SPELLS, spellInfoById),
				new SpellRankSheetParser(SPELL_RANKS, spellRepository, spellInfoById),
				new TalentSheetParser(TALENTS, talentInfoById),
				new TalentRankSheetParser(TALENT_RANKS, spellRepository, talentInfoById),
				new BuffSheetParser(BUFFS, spellRepository)
		);
	}
}

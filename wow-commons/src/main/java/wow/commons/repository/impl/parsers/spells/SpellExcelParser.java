package wow.commons.repository.impl.parsers.spells;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.impl.SpellDataRepositoryImpl;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@AllArgsConstructor
public class SpellExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SpellDataRepositoryImpl spellDataRepository;

	private final Map<SpellId, SpellInfo> spellInfoById = new EnumMap<>(SpellId.class);
	private final Map<TalentId, TalentInfo> talentInfoById = new EnumMap<>(TalentId.class);

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new SpellSheetParser("spells", spellInfoById),
				new SpellRankSheetParser("spell_ranks", spellDataRepository, spellInfoById),
				new TalentSheetParser("talents", talentInfoById),
				new TalentRankSheetParser("talent_ranks", spellDataRepository, talentInfoById),
				new EffectSheetParser("effects", spellDataRepository),
				new BuffSheetParser("buffs", spellDataRepository)
		);
	}
}

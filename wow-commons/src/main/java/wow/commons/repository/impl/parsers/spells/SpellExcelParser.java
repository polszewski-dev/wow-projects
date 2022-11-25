package wow.commons.repository.impl.parsers.spells;

import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.repository.impl.SpellDataRepositoryImpl;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public class SpellExcelParser extends ExcelParser {
	private final SpellDataRepositoryImpl spellDataRepository;

	private final Map<SpellId, SpellInfo> spellInfoById = new EnumMap<>(SpellId.class);

	public SpellExcelParser(SpellDataRepositoryImpl spellDataRepository) {
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/spell_data.xls");
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new SpellSheetParser("spells", spellInfoById),
				new SpellRankSheetParser("ranks", spellDataRepository, spellInfoById),
				new TalentSheetParser("talents", spellDataRepository),
				new EffectSheetParser("effects", spellDataRepository),
				new BuffSheetParser("buffs", spellDataRepository)
		);
	}
}

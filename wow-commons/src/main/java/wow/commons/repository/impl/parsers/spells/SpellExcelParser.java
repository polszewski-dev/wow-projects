package wow.commons.repository.impl.parsers.spells;

import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetReader;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public class SpellExcelParser extends ExcelParser {
	private final SpellDataRepositoryImpl spellDataRepository;

	public SpellExcelParser(SpellDataRepositoryImpl spellDataRepository) {
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/spell_data.xls");
	}

	@Override
	protected Stream<ExcelSheetReader> getSheetReaders() {
		return Stream.of(
				new SpellSheetReader("spells", spellDataRepository),
				new SpellRankSheetReader("ranks", spellDataRepository),
				new TalentSheetReader("talents", spellDataRepository),
				new EffectSheetReader("effects", spellDataRepository),
				new BuffSheetReader("buffs", spellDataRepository)
		);
	}
}

package wow.commons.repository.impl.parser.spell;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@AllArgsConstructor
public class SpellExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SpellRepositoryImpl spellRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new AbilitySheetParser(ABILITIES, spellRepository),
				new SpellSheetParser(ABILITY_SPELLS, spellRepository),
				new SpellEffectSheetParser(ABILITY_EFFECTS, spellRepository, MAX_ABILITY_EFFECT_MODIFIER_ATTRIBUTES, MAX_ABILITY_EFFECT_EVENTS),
				new SpellSheetParser(ITEM_SPELLS, spellRepository),
				new SpellEffectSheetParser(ITEM_EFFECTS, spellRepository, MAX_ITEM_EFFECT_MODIFIER_ATTRIBUTES, MAX_ITEM_EFFECT_EVENTS),
				new SpellSheetParser(TALENT_SPELLS, spellRepository),
				new SpellEffectSheetParser(TALENT_EFFECTS, spellRepository, MAX_TALENT_EFFECT_MODIFIER_ATTRIBUTES, MAX_TALENT_EFFECT_EVENTS),//3,1
				new RacialEffectSheetParser(RACIAL_EFFECTS, spellRepository, MAX_RACIAL_EFFECT_MODIFIER_ATTRIBUTES, MAX_RACIAL_EFFECT_EVENTS)
		);
	}
}

package wow.commons.repository.impl.parser.spell;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public class SpellSheetParser extends AbstractSpellSheetParser {
	private final SpellExcelParser parser;

	public SpellSheetParser(String sheetName, SpellExcelParser parser, Config config) {
		super(sheetName, config);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var spell = getSpell();
		parser.addSpell(spell);
	}
}

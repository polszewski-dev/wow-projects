package wow.commons.repository.impl.parser.spell;

import wow.commons.model.spell.Ability;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class AbilitySheetParser extends AbstractSpellSheetParser {
	private final SpellExcelParser parser;

	public AbilitySheetParser(String sheetName, SpellExcelParser parser, Config config) {
		super(sheetName, config);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var ability = (Ability) getSpell();
		parser.addSpell(ability);
	}
}

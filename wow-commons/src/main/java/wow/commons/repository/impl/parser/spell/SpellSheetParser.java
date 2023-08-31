package wow.commons.repository.impl.parser.spell;

import wow.commons.model.spell.Spell;
import wow.commons.repository.impl.SpellRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public class SpellSheetParser extends AbstractSpellSheetParser {
	public SpellSheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName, spellRepository);
	}

	@Override
	protected void readSingleRow() {
		Spell spell = getSpell();
		spellRepository.addSpell(spell);
	}
}

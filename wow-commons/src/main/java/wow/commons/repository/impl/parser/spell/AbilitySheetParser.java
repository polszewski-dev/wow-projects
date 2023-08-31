package wow.commons.repository.impl.parser.spell;

import wow.commons.model.spell.Ability;
import wow.commons.repository.impl.SpellRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class AbilitySheetParser extends AbstractSpellSheetParser {
	public AbilitySheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName, spellRepository);
	}

	@Override
	protected void readSingleRow() {
		Ability ability = (Ability) getSpell();
		spellRepository.addSpell(ability);
	}
}

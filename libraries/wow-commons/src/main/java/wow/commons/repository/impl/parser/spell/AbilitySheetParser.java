package wow.commons.repository.impl.parser.spell;

import wow.commons.model.spell.Ability;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class AbilitySheetParser extends AbstractSpellSheetParser {
	private final SpellRepositoryImpl spellRepository;

	public AbilitySheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName);
		this.spellRepository = spellRepository;
	}

	@Override
	protected void readSingleRow() {
		Ability ability = (Ability) getSpell();
		spellRepository.addSpell(ability);
	}
}

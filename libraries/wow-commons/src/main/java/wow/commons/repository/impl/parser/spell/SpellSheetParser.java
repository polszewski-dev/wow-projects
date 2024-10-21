package wow.commons.repository.impl.parser.spell;

import wow.commons.model.spell.Spell;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public class SpellSheetParser extends AbstractSpellSheetParser {
	private final SpellRepositoryImpl spellRepository;

	public SpellSheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName);
		this.spellRepository = spellRepository;
	}

	@Override
	protected void readSingleRow() {
		Spell spell = getSpell();
		spellRepository.addSpell(spell);
	}
}

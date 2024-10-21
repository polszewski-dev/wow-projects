package wow.scraper.exporter.spell.excel;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellType;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.ABILITY_CATEGORY;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.SPELL_TYPE;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public class SpellSheetWriter extends AbstractSpellSheetWriter<Spell> {
	public SpellSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		writeIdNameHeader();
		writeTimeRestrictionHeader();
		setHeader(SPELL_TYPE);
		setHeader(ABILITY_CATEGORY);
		writeSpellInfoHeader();
	}

	@Override
	public void writeRow(Spell spell) {
		writeIdName(spell);
		writeTimeRestriction(spell.getTimeRestriction());
		setValue(spell.getType(), SpellType.TRIGGERED_SPELL);
		setValue(spell instanceof Ability ability ? ability.getCategory() : null);
		writeSpellInfo(spell);
	}
}

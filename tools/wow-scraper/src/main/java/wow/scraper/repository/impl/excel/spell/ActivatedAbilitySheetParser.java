package wow.scraper.repository.impl.excel.spell;

import wow.scraper.parser.spell.activated.ActivatedAbilityPattern;
import wow.scraper.parser.spell.params.SpellPatternParams;

import static wow.scraper.parser.spell.SpellPatternType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-09-03
 */
public class ActivatedAbilitySheetParser extends AbstractSpellPatternSheetParser {
	public ActivatedAbilitySheetParser(String sheetName, SpellPatternExcelParser parser) {
		super(sheetName, parser);
	}

	@Override
	protected void readSingleRow() {
		var type = getPatternType(SPELL);

		switch (type) {
			case SPELL -> readPrimarySpell();
			case TRIGGERED_SPELL -> readTriggeredSpell();
			default -> throw new IllegalArgumentException(type + "");
		}
	}

	private void readPrimarySpell() {
		var pattern = getPattern();
		var params = getSpellPatternParams();
		var reqVersion = getReqVersion();
		var primarySpell = new ActivatedAbilityPattern(pattern, params, reqVersion);

		parser.add(primarySpell);
	}

	private void readTriggeredSpell() {
		var triggeredSpell = getSpellPatternParams();

		getPrimarySpell()
				.effectApplication()
				.effect()
				.setTriggeredSpell(0, triggeredSpell);
	}

	private SpellPatternParams getPrimarySpell() {
		var pattern = getPattern();
		var reqVersion = getReqVersion();

		return parser.getActivatedAbilityPattern(pattern, reqVersion)
				.orElseThrow()
				.getParams();
	}
}

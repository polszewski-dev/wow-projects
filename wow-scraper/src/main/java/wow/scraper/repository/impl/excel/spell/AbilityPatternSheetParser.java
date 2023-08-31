package wow.scraper.repository.impl.excel.spell;

import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.spell.ability.AbilityPattern;
import wow.scraper.parser.spell.params.SpellPatternParams;
import wow.scraper.repository.impl.SpellPatternRepositoryImpl;

import static wow.scraper.parser.spell.SpellPatternType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public class AbilityPatternSheetParser extends AbstractSpellPatternSheetParser {
	private final ExcelColumn colAbility = column("ability");

	public AbilityPatternSheetParser(String sheetName, SpellPatternRepositoryImpl spellPatternRepository) {
		super(sheetName, spellPatternRepository);
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
		var ability = getAbility();
		var pattern = getPattern();
		var params = getSpellPatternParams();
		var reqVersion = getReqVersion();
		var primarySpell = new AbilityPattern(pattern, params, reqVersion);

		spellPatternRepository.add(ability, primarySpell);
	}

	private void readTriggeredSpell() {
		var triggeredSpell = getSpellPatternParams();

		getPrimarySpell()
				.effectApplication()
				.effect()
				.setTriggeredSpell(0, triggeredSpell);
	}

	private SpellPatternParams getPrimarySpell() {
		var ability = getAbility();
		var pattern = getPattern();
		var reqVersion = getReqVersion();

		return spellPatternRepository.getAbilityPattern(ability, pattern, reqVersion)
				.orElseThrow()
				.getParams();
	}

	private AbilityId getAbility() {
		return colAbility.getEnum(AbilityId::parse);
	}
}

package wow.scraper.exporter.spell.excel;

import wow.scraper.parser.tooltip.AbilityTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class AbilitySheetWriter extends SpellBaseSheetWriter<AbilityTooltipParser> {
	public AbilitySheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader("spell", 40);
		setHeader("req_class");
		setHeader("req_race");
		setHeader("req_talent");
		setHeader("req_version");
		setHeader("tree");
		setHeader("school");
		setHeader("cooldown");
		setHeader("ignores gcd");
		setHeader("bolt");
		setHeader("target");
		setHeader("range");
		setHeader("conversion: from");
		setHeader("conversion: to");
		setHeader("conversion: %");
		setHeader("required effect");
		setHeader("effect removed on hit");
		setHeader("bonus dmg if under effect");
		setHeader("icon");
	}

	@Override
	public void writeRow(AbilityTooltipParser parser) {
		setValue(parser.getName());
		setValue(parser.getCharacterClass());
		setValue("");//race
		setValue(parser.isTalent() ? parser.getName() : "");
		setValue(parser.getGameVersion());
		setValue(parser.getTalentTree().getName().toLowerCase());
		setValue("");//school
		setValue(parser.getCooldown());
		setValue("");//ignores gcd
		setValue("");//bolt
		setValue("");//target
		writeRange(parser);
		setValue("");//conversion: from
		setValue("");//conversion: to
		setValue("");//conversion: %
		setValue("");//required effect
		setValue("");//effect removed on hit
		setValue("");//bonus dmg if under effect
		setValue(parser.getIcon());
	}

	private void writeRange(AbilityTooltipParser parser) {
		if (parser.getRange() != null && parser.isUnlimitedRange()) {
			throw new IllegalArgumentException("Both range types specified " + parser.getName());
		}

		if (parser.getRange() != null) {
			setValue(parser.getRange());
		} else if (parser.isUnlimitedRange()) {
			setValue(-1);
		} else {
			setValue(0);
		}
	}
}

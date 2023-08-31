package wow.scraper.exporter.spell.excel;

import wow.commons.model.spell.ResourceType;
import wow.scraper.parser.tooltip.AbilityTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class AbilityRankSheetWriter extends SpellBaseSheetWriter<AbilityTooltipParser> {
	public AbilityRankSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader("spell", 40);
		setHeader("rank");
		setHeader("req_level");
		setHeader("req_version");
		setHeader("req_phase");
		setHeader("cost: amount");
		setHeader("cost: type");
		setHeader("cost: base stat%");
		setHeader("cost: coeff");
		setHeader("cast time");
		setHeader("channeled");
		setHeader("coeff direct");
		setHeader("min dmg");
		setHeader("max dmg");
		setHeader("min dmg2");
		setHeader("max dmg2");
		setHeader("applied effect");
		setHeader("applied effect duration");
		setHeader("coeff dot");
		setHeader("dot dmg");
		setHeader("tick dmg");
		setHeader("num ticks");
		setHeader("tick interval");
		setHeader("tick weights");
		setHeader("reagent");
		setHeader("tooltip", 120);
	}

	@Override
	public void writeRow(AbilityTooltipParser parser) {
		setValue(parser.getName());
		setValue(parser.getRank());
		setValue(parser.getRequiredLevel());
		setValue(parser.getGameVersion());
		setValue(getPhase(parser));
		writeCost(parser);
		writeCastTime(parser);
		writeDirectDamage(parser);
		writeDotDamage(parser);
		setValue(parser.getReagent());
		setValue(parser.getDescription());
	}

	private void writeCost(AbilityTooltipParser parser) {
		var parsedCosts = parser.getParsedCosts();

		setValue(parsedCosts.getAmount());
		setValue(parsedCosts.getType() != ResourceType.MANA ? parsedCosts.getType() : null);
		setValue(parsedCosts.getBaseStatPct());
		setValue("");//cost coeff
	}

	private void writeCastTime(AbilityTooltipParser parser) {
		var parsedCastTime = parser.getParsedCastTime();

		setValue(parsedCastTime.getCastTime());
		setValue(parsedCastTime.isChanneled() ? "true" : "");
	}

	private void writeDirectDamage(AbilityTooltipParser parser) {
		var parsedDirectComponent = parser.getParsedDirectComponent();

		setValue("");//coeff direct
		setValue(parsedDirectComponent.getMinDmg());
		setValue(parsedDirectComponent.getMaxDmg());
		setValue(parsedDirectComponent.getMinDmg2());
		setValue(parsedDirectComponent.getMaxDmg2());
	}

	private void writeDotDamage(AbilityTooltipParser parser) {
		var parsedDoTComponent = parser.getParsedDoTComponent();

		setValue(parsedDoTComponent.getAppliedEffect() != null ? parsedDoTComponent.getAppliedEffect().getName() : "");
		setValue(parsedDoTComponent.getAppliedEffectDuration());
		setValue("");//coeff dot
		setValue(parsedDoTComponent.getDotDamage());
		setValue(parsedDoTComponent.getTickDamage());
		setValue(parsedDoTComponent.getNumTicks());
		setValue(parsedDoTComponent.getTickInterval());
		setValue("");//tick weights
	}
}

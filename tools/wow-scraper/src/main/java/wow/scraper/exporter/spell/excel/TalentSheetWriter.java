package wow.scraper.exporter.spell.excel;

import wow.commons.model.effect.impl.EffectImpl;
import wow.scraper.parser.tooltip.TalentTooltipParser;

import java.util.List;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class TalentSheetWriter extends SpellBaseSheetWriter<TalentTooltipParser, TalentExcelBuilder> {
	public TalentSheetWriter(TalentExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
		setHeader(TALENT_RANK);
		setHeader(TALENT_MAX_RANK);
		setHeader(TALENT_CALCULATOR_POSITION);
		setHeader(REQ_VERSION);
		setHeader(REQ_CLASS);
		setHeader(TALENT_TREE);
		setHeader(AUGMENTED_ABILITY);
		writeModifierComponentHeader(MAX_TALENT_MODIFIER_ATTRIBUTES);
		writeStatConversionHeader();
		writeEffectIncreasePerEffectOnTargetHeader();
		writeEventHeader(MAX_TALENT_EVENTS);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(TalentTooltipParser parser) {
		var effect = new EffectImpl(List.of());
		effect.setStatConversions(List.of());
		effect.setEvents(List.of());

		setValue(parser.getDetails().getId());
		setValue(parser.getName());
		setValue(parser.getRank());
		setValue(parser.getMaxRank());
		setValue(parser.getTalentCalculatorPosition());
		setValue(parser.getGameVersion());
		setValue(parser.getCharacterClass());
		setValue(parser.getTalentTree());
		setValue(effect.getAugmentedAbilities());
		writeModifierComponent(effect, MAX_TALENT_MODIFIER_ATTRIBUTES);
		writeStatConversions(effect);
		writeEffectIncreasePerEffectOnTarget(effect);
		writeEvents(effect, MAX_TALENT_EVENTS);
		writeIconAndTooltip(parser.getIcon(), parser.getDescription());
	}
}

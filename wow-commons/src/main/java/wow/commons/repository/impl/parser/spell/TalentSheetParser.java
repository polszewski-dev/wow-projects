package wow.commons.repository.impl.parser.spell;

import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentSource;
import wow.commons.model.talent.TalentTree;
import wow.commons.model.talent.impl.TalentImpl;
import wow.commons.repository.impl.SpellRepositoryImpl;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentSheetParser extends AbstractSpellSheetParser {
	public TalentSheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName, spellRepository);
	}

	@Override
	protected void readSingleRow() {
		Talent talent = getTalent();
		spellRepository.addTalent(talent);
	}

	private final ExcelColumn colRank = column(TALENT_RANK);
	private final ExcelColumn colMaxRank = column(TALENT_MAX_RANK);
	private final ExcelColumn colCalculatorPosition = column(TALENT_CALCULATOR_POSITION);
	private final ExcelColumn colTree = column(TALENT_TREE);

	private Talent getTalent() {
		var talentId = colId.getInteger();
		var rank = colRank.getInteger();
		var maxRank = colMaxRank.getInteger();
		var talentCalculatorPosition = colCalculatorPosition.getInteger();
		var tree = colTree.getEnum(TalentTree::parse);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		var talent = new TalentImpl(
				talentId,
				rank,
				maxRank,
				talentCalculatorPosition,
				tree,
				description,
				timeRestriction,
				characterRestriction
		);

		var effect = getEffect();

		effect.setEffectId(-talentId);
		effect.setDescription(description);
		effect.setTimeRestriction(timeRestriction);
		effect.attachSource(new TalentSource(talent));
		talent.setEffect(effect);
		return talent;
	}

	private EffectImpl getEffect() {
		EffectImpl effect = newEffect();
		var modifierComponent = getModifierComponent(MAX_TALENT_MODIFIER_ATTRIBUTES);
		var conversion = getConversion();
		var statConversions = getStatConversions();
		var effectIncreasePerEffectOnTarget = getEffectIncreasePerEffectOnTarget();
		var events = getEvents(MAX_TALENT_EVENTS);

		effect.setMaxStacks(1);
		effect.setModifierComponent(modifierComponent);
		effect.setConversion(conversion);
		effect.setStatConversions(statConversions);
		effect.setEffectIncreasePerEffectOnTarget(effectIncreasePerEffectOnTarget);
		effect.setEvents(events);
		return effect;
	}
}

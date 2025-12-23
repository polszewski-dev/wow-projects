package wow.commons.repository.impl.parser.spell;

import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.EffectScope;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.model.talent.TalentSource;
import wow.commons.model.talent.TalentTree;
import wow.commons.model.talent.impl.TalentImpl;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

import java.util.List;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetConfigs.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentSheetParser extends AbstractSpellBaseSheetParser {
	private final SpellRepositoryImpl spellRepository;
	private final TalentExcelParser parser;

	public TalentSheetParser(String sheetName, SpellRepositoryImpl spellRepository, TalentExcelParser parser) {
		super(sheetName);
		this.spellRepository = spellRepository;
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var talent = getTalent();
		parser.addTalent(talent);
	}

	private final ExcelColumn colRank = column(TALENT_RANK);
	private final ExcelColumn colMaxRank = column(TALENT_MAX_RANK);
	private final ExcelColumn colCalculatorPosition = column(TALENT_CALCULATOR_POSITION);
	private final ExcelColumn colTree = column(TALENT_TREE);

	private Talent getTalent() {
		var talentId = colId.getInteger(TalentId::of);
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

		effect.setId(EffectId.of(-talentId.value()));
		effect.setDescription(description);
		effect.setTimeRestriction(timeRestriction);
		effect.attachSource(new TalentSource(talent));
		talent.setEffect(effect);
		return talent;
	}

	private EffectImpl getEffect() {
		EffectImpl effect = newEffect();
		var modifierComponent = getModifierComponent(MAX_TALENT_MODIFIER_ATTRIBUTES);
		var statConversions = getStatConversions(MAX_TALENT_STAT_CONVERSIONS);
		var events = getEvents(MAX_TALENT_EVENTS);

		effect.setMaxStacks(1);
		effect.setMaxCounters(0);
		effect.setScope(EffectScope.GLOBAL);
		effect.setExclusionGroup(null);
		effect.setModifierComponent(modifierComponent);
		effect.setPreventedSchools(List.of());
		effect.setStatConversions(statConversions);
		effect.setEvents(events);
		return effect;
	}

	@Override
	protected EffectImpl getDummyEffect(EffectId effectId) {
		return (EffectImpl) spellRepository.getEffect(effectId, getTimeRestriction().earliestPhaseId()).orElseThrow();
	}

	@Override
	protected SpellImpl getDummySpell(SpellId spellId) {
		if (spellId == null) {
			return null;
		}
		return (SpellImpl) spellRepository.getSpell(spellId, getTimeRestriction().earliestPhaseId()).orElseThrow();
	}
}

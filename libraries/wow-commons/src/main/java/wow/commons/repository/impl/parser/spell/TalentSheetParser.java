package wow.commons.repository.impl.parser.spell;

import wow.commons.model.effect.EffectScope;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentSource;
import wow.commons.model.talent.TalentTree;
import wow.commons.model.talent.impl.TalentImpl;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;
import wow.commons.repository.impl.spell.TalentRepositoryImpl;

import java.util.List;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentSheetParser extends AbstractSpellSheetParser {
	private final TalentRepositoryImpl talentRepository;
	private final SpellRepositoryImpl spellRepository;

	public TalentSheetParser(String sheetName, TalentRepositoryImpl talentRepository, SpellRepositoryImpl spellRepository) {
		super(sheetName);
		this.talentRepository = talentRepository;
		this.spellRepository = spellRepository;
	}

	@Override
	protected void readSingleRow() {
		Talent talent = getTalent();
		talentRepository.addTalent(talent);
		spellRepository.addEffect(talent.getEffect());
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
		var statConversions = getStatConversions();
		var events = getEvents(MAX_TALENT_EVENTS);

		effect.setMaxStacks(1);
		effect.setScope(EffectScope.GLOBAL);
		effect.setExclusionGroup(null);
		effect.setModifierComponent(modifierComponent);
		effect.setPreventedSchools(List.of());
		effect.setStatConversions(statConversions);
		effect.setEvents(events);
		return effect;
	}

	@Override
	protected EffectImpl getDummyEffect(int effectId) {
		return (EffectImpl) spellRepository.getEffect(effectId, getTimeRestriction().earliestPhaseId()).orElseThrow();
	}

	@Override
	protected SpellImpl getDummySpell(Integer spellId) {
		if (spellId == null) {
			return null;
		}
		return (SpellImpl) spellRepository.getSpell(spellId, getTimeRestriction().earliestPhaseId()).orElseThrow();
	}
}

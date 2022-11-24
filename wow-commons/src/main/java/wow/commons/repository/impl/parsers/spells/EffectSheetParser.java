package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.effects.*;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.PetType;
import wow.commons.repository.impl.SpellDataRepositoryImpl;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EffectSheetParser extends BenefitSheetParser {
	private final ExcelColumn colEffect = column("effect");
	private final ExcelColumn colFriendly = column("friendly");
	private final ExcelColumn colScope = column("scope");
	private final ExcelColumn colMaxStacks = column("max stacks");
	private final ExcelColumn colRemoveAfter = column("remove after");
	private final ExcelColumn colRemoveAfterSchool = column("remove after: school");
	private final ExcelColumn colOnApply = column("on apply");
	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colSchool = column("school");
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colPet = column("pet");
	private final ExcelColumn colStackScaling = column("stack scaling");

	private final SpellDataRepositoryImpl spellDataRepository;

	public EffectSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
		addColumnGroups();
	}

	private void addColumnGroups() {
		this
				.add(EFFECT_INCREASE_PCT, "effect increase%")
				.add(DAMAGE_TAKEN_INCREASE_PCT, "damage taken increase%")
				.add(SPELL_DAMAGE, "sp bonus")
				.add(NUM_NEXT_SPELLS_CAST_INSTANTLY, "#next spells cast instantly")
		;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colEffect;
	}

	@Override
	protected void readSingleRow() {
		EffectInfo effectInfo = getEffectInfo();
		spellDataRepository.addEffectInfo(effectInfo);
	}

	private EffectInfo getEffectInfo() {
		var effectId = colEffect.getEnum(EffectId::parse);
		var friendly = colFriendly.getBoolean();
		var scope = colScope.getEnum(Scope::parse, Scope.PERSONAL);
		var maxStacks = colMaxStacks.getInteger(1);
		var onApply = colOnApply.getEnum(OnApply::parse, null);
		var stackScaling = colStackScaling.getBoolean();
		var attributes = getEffectAttributes();
		var removeCondition = getRemoveCondition();

		return new EffectInfo(effectId, friendly, scope, maxStacks, removeCondition, onApply, attributes, stackScaling);
	}

	private Attributes getEffectAttributes() {
		var talentTree = colTree.getEnum(TalentTree::parse, null);
		var spellSchool = colSchool.getEnum(SpellSchool::parse, null);
		var spells = colSpell.getSet(SpellId::parse);
		var petTypes = colPet.getSet(PetType::parse);

		return readAttributes(talentTree, spellSchool, spells, petTypes);
	}

	private RemoveCondition getRemoveCondition() {
		var removeConditionEvent = colRemoveAfter.getEnum(RemoveEvent::parse, null);
		var removeConditionSpellSchool = colRemoveAfterSchool.getEnum(SpellSchool::parse, null);

		return RemoveCondition.create(removeConditionEvent, removeConditionSpellSchool);
	}
}

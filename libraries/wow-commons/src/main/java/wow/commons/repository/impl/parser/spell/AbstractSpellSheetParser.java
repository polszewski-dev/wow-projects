package wow.commons.repository.impl.parser.spell;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.spell.component.DirectComponentBonus;
import wow.commons.model.spell.impl.*;
import wow.commons.model.talent.TalentTree;
import wow.commons.util.condition.SpellTargetConditionParser;

import java.util.List;

import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;
import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public abstract class AbstractSpellSheetParser extends AbstractSpellBaseSheetParser {
	public record Config(
		int maxDirectCommands,
		int maxApplyEffectCommands
	) {}

	private final Config config;

	protected AbstractSpellSheetParser(String sheetName, Config config) {
		super(sheetName);
		this.config = config;
	}

	private final ExcelColumn colType = column(SPELL_TYPE);

	protected SpellImpl getSpell() {
		var type = colType.getEnum(SpellType::parse, SpellType.TRIGGERED_SPELL);

		return switch (type) {
			case CLASS_ABILITY -> getClassAbility();
			case RACIAL_ABILITY -> getRacialAbility();
			case ACTIVATED_ABILITY -> getActivatedAbility();
			case TRIGGERED_SPELL -> getTriggeredSpell();
		};
	}

	private final ExcelColumn colRank = column(ABILITY_RANK);
	private final ExcelColumn colTree = column(ABILITY_TREE);

	private ClassAbilityImpl getClassAbility() {
		var classAbility = new ClassAbilityImpl();
		var rank = colRank.getInteger();
		var talentTree = colTree.getEnum(TalentTree::parse);
		var cost = getCost();

		initAbility(classAbility);
		classAbility.setRank(rank);
		classAbility.setTalentTree(talentTree);
		classAbility.setCost(cost);
		classAbility.setNameRank(rank);
		return classAbility;
	}

	private RacialAbilityImpl getRacialAbility() {
		var racialAbility = new RacialAbilityImpl();
		var cost = getCost();

		initAbility(racialAbility);
		racialAbility.setCost(cost);
		racialAbility.setNameRank(0);
		return racialAbility;
	}

	private final ExcelColumn colCooldownGroup = column(COOLDOWN_GROUP, true);

	private ActivatedAbilityImpl getActivatedAbility() {
		var cooldownGroup = colCooldownGroup.getEnum(CooldownGroup::parse, null);
		var activatedAbility = new ActivatedAbilityImpl(cooldownGroup);

		initAbility(activatedAbility);
		activatedAbility.setNameRank(0);
		return activatedAbility;
	}

	private TriggeredSpellImpl getTriggeredSpell() {
		var triggeredSpell = new TriggeredSpellImpl();

		initSpell(triggeredSpell);
		return triggeredSpell;
	}

	protected void initSpell(SpellImpl spell) {
		var spellId = colId.getInteger(SpellId::of);
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var cooldown = colCooldown.getDuration(Duration.ZERO);
		var bolt = colBolt.getBoolean();
		var directComponent = getDirectComponent();
		var effectApplication = getEffectApplication();

		spell.setId(spellId);
		spell.setDescription(description);
		spell.setTimeRestriction(timeRestriction);

		spell.setCooldown(cooldown);
		spell.setBolt(bolt);
		spell.setDirectComponent(directComponent);
		spell.setEffectApplication(effectApplication);
	}

	private final ExcelColumn colCategory = column(ABILITY_CATEGORY);
	private final ExcelColumn colCooldown = column(COOLDOWN);
	private final ExcelColumn colRange = column(RANGE);
	private final ExcelColumn colBolt = column(BOLT);
	private final ExcelColumn colEffectRemovedOnHit = column(EFFECT_REMOVED_ON_HIT);

	protected void initAbility(AbilityImpl ability) {
		initSpell(ability);

		var abilityId = colName.getEnum(AbilityId::parse);
		var category = colCategory.getEnum(AbilityCategory::parse, null);
		var castInfo = getCastInfo(ability instanceof ActivatedAbility);
		var range = colRange.getInteger();
		var effectRemovedOnHit = colEffectRemovedOnHit.getEnum(AbilityId::parse, null);
		var characterRestriction = getRestriction();

		ability.setAbilityId(abilityId);
		ability.setCategory(category);
		ability.setCastInfo(castInfo);
		ability.setRange(range);
		ability.setEffectRemovedOnHit(effectRemovedOnHit);
		ability.setCharacterRestriction(characterRestriction);
	}

	private final ExcelColumn colCostAmount = column(COST_AMOUNT).prefixed(COST_PREFIX);
	private final ExcelColumn colCostType = column(COST_TYPE).prefixed(COST_PREFIX);
	private final ExcelColumn colCostBaseStatPct = column(COST_BASE_STAT_PCT).prefixed(COST_PREFIX);
	private final ExcelColumn colReagent = column(COST_REAGENT).prefixed(COST_PREFIX);

	private Cost getCost() {
		var type = colCostType.getEnum(ResourceType::parse, ResourceType.MANA);
		var amount = colCostAmount.getInteger(0);
		var baseStatPct = colCostBaseStatPct.getPercent(Percent.ZERO);
		var coeff = getCoefficient(COST_PREFIX);
		var reagent = colReagent.getEnum(Reagent::parse, null);
		return new Cost(type, amount, baseStatPct, coeff, reagent);
	}

	private final ExcelColumn colCastTime = column(CAST_TIME);
	private final ExcelColumn colChanneled = column(CAST_CHANNELED);
	private final ExcelColumn colIgnoresGcd = column(CAST_IGNORES_GCD);

	private CastInfo getCastInfo(boolean activatedAbility) {
		var castTime = colCastTime.getDuration(Duration.ZERO);
		var channeled = colChanneled.getBoolean();
		var ignoresGcd = activatedAbility || colIgnoresGcd.getBoolean();
		return new CastInfo(castTime, channeled, ignoresGcd);
	}

	private DirectComponent getDirectComponent() {
		var commands = getDirectCommands();

		return commands.isEmpty() ? null : new DirectComponent(commands);
	}

	private List<DirectCommand> getDirectCommands() {
		return readSections(config.maxDirectCommands(), this::getDirectCommand);
	}

	private final ExcelColumn colDirectType = column(DIRECT_TYPE);
	private final ExcelColumn colDirectMin = column(DIRECT_MIN);
	private final ExcelColumn colDirectMax = column(DIRECT_MAX);

	private DirectCommand getDirectCommand(int idx) {
		var prefix = getDirectCommandPrefix(idx);

		if (hasNoTarget(prefix)) {
			return null;
		}

		var target = getTarget(prefix);
		var condition = getTargetCondition(prefix);
		var type = colDirectType.prefixed(prefix).getEnum(ComponentType::parse);
		var coeff = getCoefficient(prefix);
		var min = colDirectMin.prefixed(prefix).getInteger();
		var max = colDirectMax.prefixed(prefix).getInteger();
		var bonus = getDirectComponentBonus(prefix);

		return new DirectCommand(target, condition, type, coeff, min, max, bonus);
	}

	private final ExcelColumn colCondition = column(TARGET_CONDITION, true);

	private SpellTargetCondition getTargetCondition(String prefix) {
		return colCondition.prefixed(prefix).getEnum(
				SpellTargetConditionParser::parseCondition,
				SpellTargetCondition.EMPTY
		);
	}

	private final ExcelColumn colDirectBonusMin = column(DIRECT_BONUS_MIN, true);
	private final ExcelColumn colDirectBonusMax = column(DIRECT_BONUS_MAX, true);
	private final ExcelColumn colDirectBonusRequiredEffect = column(DIRECT_BONUS_REQUIRED_EFFECT, true);

	private DirectComponentBonus getDirectComponentBonus(String prefix) {
		if (colDirectBonusMin.prefixed(prefix).isEmpty()) {
			return null;
		}

		var min = colDirectBonusMin.prefixed(prefix).getInteger();
		var max = colDirectBonusMax.prefixed(prefix).getInteger();
		var requiredEffect = colDirectBonusRequiredEffect.prefixed(prefix).getEnum(AbilityId::parse, null);

		return new DirectComponentBonus(min, max, requiredEffect);
	}

	private EffectApplication getEffectApplication() {
		var commands = getApplyEffectCommands();

		return commands.isEmpty() ? null : new EffectApplication(commands);
	}

	private List<ApplyEffect> getApplyEffectCommands() {
		return readSections(config.maxApplyEffectCommands(), this::getApplyEffectCommand);
	}

	private final ExcelColumn colAppliedEffectId = column(APPLIED_EFFECT_ID);
	private final ExcelColumn colAppliedEffectDuration = column(APPLIED_EFFECT_DURATION);
	private final ExcelColumn colAppliedEffectStacks = column(APPLIED_EFFECT_STACKS);
	private final ExcelColumn colAppliedEffectCharges = column(APPLIED_EFFECT_CHARGES);
	private final ExcelColumn colReplacementMode = column(APPLIED_EFFECT_REPLACEMENT_MODE, true);

	private ApplyEffect getApplyEffectCommand(int idx) {
		var prefix = getApplyEffectCommandPrefix(idx);

		if (hasNoTarget(prefix)) {
			return null;
		}

		var target = getTarget(prefix);
		var condition = getTargetCondition(prefix);
		var effectId = colAppliedEffectId.prefixed(prefix).getInteger(EffectId::of);
		var duration = colAppliedEffectDuration.prefixed(prefix).getAnyDuration();
		var numStacks = colAppliedEffectStacks.prefixed(prefix).getInteger();
		var numCharges = colAppliedEffectCharges.prefixed(prefix).getInteger();
		var replacementMode = colReplacementMode.prefixed(prefix).getEnum(EffectReplacementMode::parse, EffectReplacementMode.DEFAULT);

		var dummy = getDummyEffect(effectId);

		return new ApplyEffect(target, condition, dummy, duration, numStacks, numCharges, replacementMode);
	}
}

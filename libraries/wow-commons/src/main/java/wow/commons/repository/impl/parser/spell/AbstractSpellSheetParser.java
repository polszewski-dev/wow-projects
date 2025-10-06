package wow.commons.repository.impl.parser.spell;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.component.*;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.spell.component.DirectComponentBonus;
import wow.commons.model.spell.impl.*;
import wow.commons.model.talent.TalentTree;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public abstract class AbstractSpellSheetParser extends WowExcelSheetParser {
	protected AbstractSpellSheetParser(String sheetName) {
		super(sheetName);
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
		var directComponents = getDirectComponents();
		var effectApplication = getEffectApplication();

		spell.setId(spellId);
		spell.setDescription(description);
		spell.setTimeRestriction(timeRestriction);

		spell.setCooldown(cooldown);
		spell.setDirectComponents(directComponents);
		spell.setEffectApplication(effectApplication);
	}

	private final ExcelColumn colCategory = column(ABILITY_CATEGORY);
	private final ExcelColumn colCooldown = column(COOLDOWN);
	private final ExcelColumn colRange = column(RANGE);
	private final ExcelColumn colRequiredEffect = column(REQUIRED_EFFECT);
	private final ExcelColumn colEffectRemovedOnHit = column(EFFECT_REMOVED_ON_HIT);

	protected void initAbility(AbilityImpl ability) {
		initSpell(ability);

		var abilityId = colName.getEnum(AbilityId::parse);
		var category = colCategory.getEnum(AbilityCategory::parse, null);
		var castInfo = getCastInfo(ability instanceof ActivatedAbility);
		var range = colRange.getInteger();
		var requiredEffect = colRequiredEffect.getEnum(AbilityId::parse, null);
		var effectRemovedOnHit = colEffectRemovedOnHit.getEnum(AbilityId::parse, null);
		var characterRestriction = getRestriction();

		ability.setAbilityId(abilityId);
		ability.setCategory(category);
		ability.setCastInfo(castInfo);
		ability.setRange(range);
		ability.setRequiredEffect(requiredEffect);
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

	private List<DirectComponent> getDirectComponents() {
		return IntStream.rangeClosed(1, MAX_DIRECT_COMPONENTS)
				.mapToObj(this::getDirectComponent)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colDirectType = column(DIRECT_TYPE);
	private final ExcelColumn colDirectMin = column(DIRECT_MIN);
	private final ExcelColumn colDirectMax = column(DIRECT_MAX);
	private final ExcelColumn colDirectBolt = column(DIRECT_BOLT);

	private DirectComponent getDirectComponent(int idx) {
		var prefix = getDirectComponentPrefix(idx);

		if (colTarget.prefixed(prefix).isEmpty()) {
			return null;
		}

		var target = getTarget(prefix);
		var type = colDirectType.prefixed(prefix).getEnum(ComponentType::parse);
		var coeff = getCoefficient(prefix);
		var min = colDirectMin.prefixed(prefix).getInteger();
		var max = colDirectMax.prefixed(prefix).getInteger();
		var bonus = getDirectComponentBonus(prefix);
		var bolt = colDirectBolt.prefixed(prefix).getBoolean();

		return new DirectComponent(target, type, coeff, min, max, bonus, bolt);
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

	protected List<StatConversion> getStatConversions() {
		return IntStream.rangeClosed(1, MAX_STAT_CONVERSIONS)
				.mapToObj(this::getStatConversion)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colStatConversionFrom = column(STAT_CONVERSION_FROM);
	private final ExcelColumn colStatConversionTo = column(STAT_CONVERSION_TO);
	private final ExcelColumn colStatConversionToCondition = column(STAT_CONVERSION_TO_CONDITION);
	private final ExcelColumn colStatConversionRatio = column(STAT_CONVERSION_RATIO);

	private StatConversion getStatConversion(int idx) {
		var prefix = getStatConversionPrefix(idx);

		if (colStatConversionFrom.prefixed(prefix).isEmpty()) {
			return null;
		}

		var from = colStatConversionFrom.prefixed(prefix).getEnum(AttributeId::parse);
		var to = colStatConversionTo.prefixed(prefix).getEnum(AttributeId::parse);
		var toCondition = colStatConversionToCondition.prefixed(prefix).getEnum(AttributeCondition::parse, AttributeCondition.EMPTY);
		var ratio = colStatConversionRatio.prefixed(prefix).getPercent();

		return new StatConversion(from, to, toCondition, ratio);
	}

	private final ExcelColumn colAppliedEffectId = column(APPLIED_EFFECT_ID);
	private final ExcelColumn colAppliedEffectDuration = column(APPLIED_EFFECT_DURATION);
	private final ExcelColumn colAppliedEffectStacks = column(APPLIED_EFFECT_STACKS);
	private final ExcelColumn colAppliedEffectCharges = column(APPLIED_EFFECT_CHARGES);
	private final ExcelColumn colReplacementMode = column(APPLIED_EFFECT_REPLACEMENT_MODE, true);

	private EffectApplication getEffectApplication() {
		var prefix = APPLY_PREFIX;

		if (colTarget.prefixed(prefix).isEmpty()) {
			return null;
		}

		var target = getTarget(prefix);
		var effectId = colAppliedEffectId.prefixed(prefix).getInteger(EffectId::of);
		var duration = colAppliedEffectDuration.prefixed(prefix).getAnyDuration();
		var numStacks = colAppliedEffectStacks.prefixed(prefix).getInteger();
		var numCharges = colAppliedEffectCharges.prefixed(prefix).getInteger();
		var replacementMode = colReplacementMode.prefixed(prefix).getEnum(EffectReplacementMode::parse, EffectReplacementMode.DEFAULT);

		var dummy = getDummyEffect(effectId);

		return new EffectApplication(target, dummy, duration, numStacks, numCharges, replacementMode);
	}

	protected EffectImpl getDummyEffect(EffectId effectId) {
		var dummy = new EffectImpl(List.of());
		dummy.setId(effectId);
		return dummy;
	}

	protected final ExcelColumn colAugmentedAbility = column(AUGMENTED_ABILITY, true);

	protected EffectImpl newEffect() {
		var augmentedAbilities = colAugmentedAbility.getList(AbilityId::parse);

		return new EffectImpl(augmentedAbilities);
	}

	protected ModifierComponent getModifierComponent(int maxAttributes) {
		var attributes = readAttributes(MOD_PREFIX, maxAttributes);

		if (attributes.isEmpty()) {
			return null;
		}

		return new ModifierComponent(attributes);
	}

	protected List<Event> getEvents(int maxEvents) {
		return IntStream.rangeClosed(1, maxEvents)
				.mapToObj(this::getEvent)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colEventOn = column(EVENT_ON);
	private final ExcelColumn colEventCondition = column(EVENT_CONDITION);
	private final ExcelColumn colEventChance = column(EVENT_CHANCE_PCT);
	private final ExcelColumn colEventAction = column(EVENT_ACTION);
	private final ExcelColumn colEventTriggeredSpell = column(EVENT_TRIGGERED_SPELL);
	private final ExcelColumn colEventActionParams = column(EVENT_ACTION_PARAMS, true);

	private Event getEvent(int idx) {
		var prefix = getEventPrefix(idx);

		if (colEventOn.prefixed(prefix).isEmpty()) {
			return null;
		}

		var types = colEventOn.prefixed(prefix).getList(EventType::parse);
		var condition = colEventCondition.prefixed(prefix).getEnum(AttributeCondition::parse, AttributeCondition.EMPTY);
		var chance = colEventChance.prefixed(prefix).getPercent(Percent._100);
		var actions = colEventAction.prefixed(prefix).getList(EventAction::parse);
		var triggeredSpellId = colEventTriggeredSpell.prefixed(prefix).getNullableInteger(SpellId::ofNullable);
		var actionParams = colEventActionParams.prefixed(prefix).getEnum(EventActionParameters::parse, EventActionParameters.EMPTY);

		var dummy = getDummySpell(triggeredSpellId);

		return new Event(types, condition, chance, actions, dummy, actionParams);
	}

	protected SpellImpl getDummySpell(SpellId spellId) {
		if (spellId == null) {
			return null;
		}
		var dummy = new TriggeredSpellImpl();
		dummy.setId(spellId);
		return dummy;
	}

	private final ExcelColumn colCoeffValue = column(COEFF_VALUE);
	private final ExcelColumn colCoeffSchool = column(COEFF_SCHOOL);

	protected Coefficient getCoefficient(String prefix) {
		var value = colCoeffValue.prefixed(prefix).getPercent(Percent.ZERO);
		var school = colCoeffSchool.prefixed(prefix).getEnum(SpellSchool::parse, null);
		return new Coefficient(value, school);
	}

	private final ExcelColumn colTarget = column(TARGET, true);

	protected SpellTarget getTarget(String prefix) {
		return colTarget.prefixed(prefix).getEnum(SpellTarget::parse);
	}
}

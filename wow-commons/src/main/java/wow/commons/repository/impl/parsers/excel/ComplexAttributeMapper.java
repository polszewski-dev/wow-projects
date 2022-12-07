package wow.commons.repository.impl.parsers.excel;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.complex.special.*;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.spells.EffectId;
import wow.commons.model.talents.TalentTree;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public final class ComplexAttributeMapper {
	public static String toString(ComplexAttribute attribute) {
		if (attribute == null) {
			return null;
		}

		assertNoCondition(attribute);

		if (attribute instanceof EquivalentAbility) {
			return toString((EquivalentAbility)attribute);
		}
		if (attribute instanceof OnUseAbility) {
			return toString((OnUseAbility)attribute);
		}
		if (attribute instanceof ProcAbility) {
			return toString((ProcAbility)attribute);
		}
		if (attribute instanceof TalentProcAbility) {
			return toString((TalentProcAbility)attribute);
		}
		if (attribute instanceof MiscAbility) {
			return toString((MiscAbility)attribute);
		}
		if (attribute instanceof StatConversion) {
			return toString((StatConversion)attribute);
		}
		if (attribute instanceof EffectIncreasePerEffectOnTarget) {
			return toString((EffectIncreasePerEffectOnTarget)attribute);
		}

		throw new IllegalArgumentException("Unhandled type: " + attribute.getClass());
	}

	public static ComplexAttribute fromString(String value) {
		ParseResult parseResult = SimpleRecordMapper.fromString(value);

		if (parseResult.getType() == null) {
			return null;
		}

		switch (parseResult.getType()) {
			case TYPE_EQUIVALENT:
				return toEquivalent(parseResult);
			case TYPE_ON_USE:
				return toOnUse(parseResult);
			case TYPE_PROC:
				return toProc(parseResult);
			case TYPE_TALENT_PROC:
				return toTalentProc(parseResult);
			case TYPE_MISC:
				return toMisc(parseResult);
			case TYPE_STAT_CONVERSION:
				return toStatConversion(parseResult);
			case TYPE_EFFECT_INCREASE:
				return toEffectIncrease(parseResult);
			default:
				throw new IllegalArgumentException("Unhandled type: " + parseResult.getType());
		}
	}

	private static final String TYPE_EQUIVALENT = "Equivalent";
	private static final String EQ_STAT = "stat";
	private static final String EQ_AMOUNT = "amount";
	private static final String EQ_LINE = "line";

	public static String toString(EquivalentAbility equivalentAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		putPrimitiveAttributes(EQ_STAT, EQ_AMOUNT, map, equivalentAbility.getAttributes().getAttributes());
		map.put(EQ_LINE, equivalentAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_EQUIVALENT, map);
	}

	private static EquivalentAbility toEquivalent(ParseResult parseResult) {
		var attributes = getPrimitiveAttributes(EQ_STAT, EQ_AMOUNT, parseResult);
		var line = parseResult.getString(EQ_LINE, null);

		return SpecialAbility.equivalent(attributes, line);
	}

	private static final String TYPE_ON_USE = "OnUse";
	private static final String OU_STAT = "stat";
	private static final String OU_AMOUNT = "amount";
	private static final String OU_DURATION = "duration";
	private static final String OU_COOLDOWN = "cooldown";
	private static final String OU_LINE = "line";

	public static String toString(OnUseAbility onUseAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		putPrimitiveAttributes(OU_STAT, OU_AMOUNT, map, onUseAbility.getAttributes());
		map.put(OU_DURATION, onUseAbility.getDuration());
		map.put(OU_COOLDOWN, onUseAbility.getCooldown());
		map.put(OU_LINE, onUseAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_ON_USE, map);
	}

	private static OnUseAbility toOnUse(ParseResult parseResult) {
		var attributes = getPrimitiveAttributes(OU_STAT, OU_AMOUNT, parseResult);
		var duration = parseResult.getDuration(OU_DURATION);
		var cooldown = parseResult.getDuration(OU_COOLDOWN);
		var line = parseResult.getString(OU_LINE, null);

		return SpecialAbility.onUse(attributes, duration, cooldown, line);
	}

	private static final String TYPE_PROC = "Proc";
	private static final String P_EVENT = "event";
	private static final String P_CHANCE_PCT = "chance%";
	private static final String P_STAT = "stat";
	private static final String P_AMOUNT = "amount";
	private static final String P_DURATION = "duration";
	private static final String P_COOLDOWN = "cooldown";
	private static final String P_LINE = "line";

	public static String toString(ProcAbility procAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(P_EVENT, procAbility.getEvent());
		map.put(P_CHANCE_PCT, procAbility.getChance());
		putPrimitiveAttributes(P_STAT, P_AMOUNT, map, procAbility.getAttributes());
		map.put(P_DURATION, procAbility.getDuration());
		map.put(P_COOLDOWN, procAbility.getCooldown());
		map.put(P_LINE, procAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_PROC, map);
	}

	private static ProcAbility toProc(ParseResult parseResult) {
		var event = parseResult.getEnum(P_EVENT, ProcEvent::parse);
		Attributes attributes = getPrimitiveAttributes(P_STAT, P_AMOUNT, parseResult);
		var chancePct = parseResult.getPercent(P_CHANCE_PCT);
		var duration = parseResult.getDuration(P_DURATION);
		var cooldown = parseResult.getDuration(P_COOLDOWN, null);
		var line = parseResult.getString(P_LINE, null);

		return SpecialAbility.proc(event, chancePct, attributes, duration, cooldown, line);
	}

	private static final String TYPE_TALENT_PROC = "TalentProc";
	private static final String TP_EVENT = "event";
	private static final String TP_CHANCE_PCT = "chance%";
	private static final String TP_EFFECT = "effect";
	private static final String TP_DURATION = "duration";
	private static final String TP_STACKS = "stacks";
	private static final String TP_LINE = "line";

	public static String toString(TalentProcAbility talentProcAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(TP_EVENT, talentProcAbility.getEvent());
		map.put(TP_CHANCE_PCT, talentProcAbility.getChance());
		map.put(TP_EFFECT, talentProcAbility.getEffectId());
		map.put(TP_DURATION, talentProcAbility.getDuration());
		map.put(TP_STACKS, talentProcAbility.getStacks());
		map.put(TP_LINE, talentProcAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_TALENT_PROC, map);
	}

	private static TalentProcAbility toTalentProc(ParseResult parseResult) {
		var event = parseResult.getEnum(TP_EVENT, ProcEvent::parse);
		var effectId = parseResult.getEnum(TP_EFFECT, EffectId::parse);
		var chancePct = parseResult.getPercent(TP_CHANCE_PCT, Percent._100);
		var duration = parseResult.getDuration(TP_DURATION, Duration.INFINITE);
		var stacks = parseResult.getInteger(TP_STACKS, 1);
		var line = parseResult.getString(TP_LINE, null);

		return SpecialAbility.talentProc(event, chancePct, effectId, duration, stacks, line);
	}

	private static final String TYPE_MISC = "Misc";
	private static final String M_LINE = "line";

	public static String toString(MiscAbility talentProcAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(M_LINE, talentProcAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_MISC, map);
	}

	private static MiscAbility toMisc(ParseResult parseResult) {
		var line = parseResult.getString(M_LINE);

		return SpecialAbility.misc(line);
	}

	private static final String TYPE_STAT_CONVERSION = "StatConversion";
	private static final String SC_FROM = "from";
	private static final String SC_TO = "to";
	private static final String SC_RATIO_PCT = "ratio%";

	public static String toString(StatConversion statConversion) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(SC_FROM, statConversion.getFromStat());
		map.put(SC_TO, statConversion.getToStat());
		map.put(SC_RATIO_PCT, statConversion.getRatioPct());

		return SimpleRecordMapper.toString(TYPE_STAT_CONVERSION, map);
	}

	private static StatConversion toStatConversion(ParseResult parseResult) {
		var fromStat = parseResult.getEnum(SC_FROM, StatConversion.Stat::parse);
		var toStat = parseResult.getEnum(SC_TO, StatConversion.Stat::parse);
		var ratioPct = parseResult.getPercent(SC_RATIO_PCT);

		return new StatConversion(fromStat, toStat, ratioPct, AttributeCondition.EMPTY);
	}

	private static final String TYPE_EFFECT_INCREASE = "EffectIncrease";
	private static final String EI_TREE = "tree";
	private static final String EI_PCT = "%";
	private static final String EI_MAX_PCT = "max%";

	public static String toString(EffectIncreasePerEffectOnTarget effectIncrease) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(EI_TREE, effectIncrease.getEffectTree());
		map.put(EI_PCT, effectIncrease.getIncreasePerEffectPct());
		map.put(EI_MAX_PCT, effectIncrease.getMaxIncreasePct());

		return SimpleRecordMapper.toString(TYPE_EFFECT_INCREASE, map);
	}

	private static EffectIncreasePerEffectOnTarget toEffectIncrease(ParseResult parseResult) {
		var effectTree = parseResult.getEnum(EI_TREE, TalentTree::parse);
		var increasePerEffectPct = parseResult.getPercent(EI_PCT);
		var maxIncreasePct = parseResult.getPercent(EI_MAX_PCT);

		return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, AttributeCondition.EMPTY);
	}

	private static void putPrimitiveAttributes(String statKey, String amountKey, Map<String, Object> map, Attributes attributes) {
		List<PrimitiveAttribute> primitiveAttributes = attributes.getPrimitiveAttributeList();

		assertNumberOfAttributesIsWithinLimit(primitiveAttributes);

		if (primitiveAttributes.size() == 1) {
			putPrimitiveAttribute(statKey, amountKey, primitiveAttributes.get(0), map);
			return;
		}

		int i = 1;
		for (PrimitiveAttribute attribute : primitiveAttributes) {
			putPrimitiveAttribute(statKey + i, amountKey + i, attribute, map);
			++i;
		}
	}

	private static void putPrimitiveAttribute(String statKey, String amountKey, PrimitiveAttribute attribute, Map<String, Object> map) {
		map.put(statKey, getIdAndCondition(attribute));
		map.put(amountKey, attribute.getDouble());
	}

	public static String getIdAndCondition(PrimitiveAttribute attribute) {
		if (!attribute.hasCondition()) {
			return attribute.getId().toString();
		}
		return attribute.getId() + "," + attribute.getCondition();
	}

	private static Attributes getPrimitiveAttributes(String statKey, String amountKey, ParseResult parseResult) {
		AttributesBuilder builder = new AttributesBuilder();

		getPrimitiveAttribute(statKey, amountKey, builder, parseResult);

		for (int i = 1; i <= MAX_ATTRIBUTES; ++i) {
			getPrimitiveAttribute(statKey +i, amountKey +i, builder, parseResult);
		}

		return builder.toAttributes();
	}

	private static void getPrimitiveAttribute(String statKey, String amountKey, AttributesBuilder builder, ParseResult parseResult) {
		var statStr = parseResult.getString(statKey, null);

		if (statStr == null) {
			return;
		}

		var attributeSupplier = PrimitiveAttributeSupplier.fromString(statStr);
		var amount = parseResult.getDouble(amountKey, null);
		attributeSupplier.addAttributeList(builder, amount);
	}

	private static final int MAX_ATTRIBUTES = 9;

	private static void assertNumberOfAttributesIsWithinLimit(List<PrimitiveAttribute> attributes) {
		if (attributes.size() > MAX_ATTRIBUTES) {
			throw new IllegalArgumentException(String.format("Can only handle %s instead of %s", MAX_ATTRIBUTES, attributes.size()));
		}
	}

	private static void assertNoCondition(ComplexAttribute attribute) {
		if (attribute.hasCondition()) {
			throw new IllegalArgumentException("Can't handle conditions: " + attribute);
		}
	}

	private ComplexAttributeMapper() {}
}

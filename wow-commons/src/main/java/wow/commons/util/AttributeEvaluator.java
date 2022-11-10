package wow.commons.util;

import wow.commons.model.attributes.*;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.item.*;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public class AttributeEvaluator implements AttributeCollector<AttributeEvaluator> {
	private final AttributeFilter filter;

	private final Map<PrimitiveAttributeId, Double> primitiveAttributes = new EnumMap<>(PrimitiveAttributeId.class);
	private final Map<PrimitiveAttributeId, Map<AttributeCondition, Double>> conditionalPrimitiveAttributes = new EnumMap<>(PrimitiveAttributeId.class);

	private final Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributes = new EnumMap<>(ComplexAttributeId.class);

	private AttributeEvaluator(AttributeFilter filter) {
		this.filter = filter;
	}

	public static AttributeEvaluator of(AttributeFilter filter) {
		return new AttributeEvaluator(filter);
	}

	public static AttributeEvaluator of() {
		return of((AttributeFilter)null);
	}

	public static AttributeEvaluator of(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return of(AttributeFilter.ofNotNullOnly(talentTree, spellSchool, spellId, petType, creatureType));
	}

	public static AttributeEvaluator of(SpellInfo spellInfo) {
		return of(spellInfo.getAttributeFiter());
	}

	@Override
	public AttributeEvaluator addAttributes(AttributeSource attributeSource) {
		if (attributeSource == null) {
			return this;
		}

		Attributes attributes = attributeSource.getAttributes();

		for (var attribute : attributes.getPrimitiveAttributeList()) {
			addAttribute(attribute);
		}

		for (var entry : attributes.getComplexAttributeList().entrySet()) {
			for (ComplexAttribute attribute : entry.getValue()) {
				addAttribute(attribute);
			}
		}

		return this;
	}

	public AttributeEvaluator addAttributes(AttributeCollection attributeCollection) {
		attributeCollection.collectAttributes(this);
		return this;
	}

	public AttributeEvaluator addAttribute(PrimitiveAttribute attribute) {
		if (attribute == null) {
			return this;
		}

		if (!attribute.isMatchedBy(filter)) {
			return this;
		}

		addPrimitiveAttribute(attribute);

		return this;
	}

	private void addPrimitiveAttribute(PrimitiveAttribute attribute) {
		PrimitiveAttributeId id = attribute.getId();
		if (!attribute.hasCondition()) {
			primitiveAttributes.put(id, attribute.getDouble() + primitiveAttributes.getOrDefault(id, 0.0));
		} else {
			var map = conditionalPrimitiveAttributes.computeIfAbsent(id, x -> new HashMap<>());
			map.put(attribute.getCondition(), attribute.getDouble() + map.getOrDefault(attribute.getCondition(), 0.0));
		}
	}

	@Override
	public AttributeEvaluator addAttribute(ComplexAttribute attribute) {
		if (attribute == null) {
			return this;
		}

		if (!attribute.isMatchedBy(filter)) {
			return this;
		}

		complexAttributes.computeIfAbsent(attribute.getId(), x -> new ArrayList<>())
				.add(attribute);

		return this;
	}

	public Attributes getAttributes() {
		AttributesBuilder result = new AttributesBuilder();

		processUnconditionalAttributes(result);
		processConditionalAttributes(result);
		processComplexAttributes(result);
		return result.toAttributes();
	}

	private void processUnconditionalAttributes(AttributesBuilder result) {
		for (var entry : primitiveAttributes.entrySet()) {
			var id = entry.getKey();
			var value = entry.getValue();
			result.addAttribute(id, value);
		}
	}

	private void processConditionalAttributes(AttributesBuilder result) {
		for (var entry : conditionalPrimitiveAttributes.entrySet()) {
			var id = entry.getKey();
			for (var entry2 : entry.getValue().entrySet()) {
				var condition2 = entry2.getKey();
				var value = entry2.getValue();
				result.addAttribute(id, value, condition2);
			}
		}
	}

	private void processComplexAttributes(AttributesBuilder result) {
		for (var entry : complexAttributes.entrySet()) {
			result.addComplexAttributeList(entry.getValue());
		}
	}

	private void solveSetBonuses() {
		var itemSetPieces = complexAttributes
				.getOrDefault(ComplexAttributeId.SET_PIECES, List.of())
				.stream()
				.distinct()
				.collect(Collectors.groupingBy(x -> ((ItemSetPiece) x).getItemSet()));

		for (var entry : itemSetPieces.entrySet()) {
			ItemSet itemSet = entry.getKey();
			int numPieces = entry.getValue().size();

			for (ItemSetBonus itemSetBonus : itemSet.getItemSetBonuses()) {
				if (numPieces >= itemSetBonus.getNumPieces()) {
					addAttributes(itemSetBonus.getBonusStats());
				}
			}
		}

		complexAttributes.remove(ComplexAttributeId.SET_PIECES);
	}

	private void solveSockets() {
		List<ItemSockets> sockets = (List)complexAttributes.getOrDefault(ComplexAttributeId.SOCKETS, List.of());

		int numRed = 0;
		int numYellow = 0;
		int numBlue = 0;

		for (ItemSockets itemSockets : sockets) {
			numRed += itemSockets.getGemCount(SocketType.RED);
			numYellow += itemSockets.getGemCount(SocketType.YELLOW);
			numBlue += itemSockets.getGemCount(SocketType.BLUE);
		}

		for (ItemSockets itemSockets : sockets) {
			for (int i = 1; i <= itemSockets.getSocketCount(); ++i) {
				Gem gem = itemSockets.getGem(i);
				if (gem != null && (gem.getColor() != GemColor.META || gem.isMetaConditionTrue(numRed, numYellow, numBlue))) {
					addAttributes(gem);
				}
			}
			if (itemSockets.allMatch(numRed, numYellow, numBlue)) {
				addAttributes(itemSockets.getSocketBonus());
			}
		}

		complexAttributes.remove(ComplexAttributeId.SOCKETS);
	}

	private void solveAbilities(StatProvider statProvider) {
		List<SpecialAbility> specialAbilities = (List)complexAttributes.get(ComplexAttributeId.SPECIAL_ABILITIES);

		if (specialAbilities == null) {
			return;
		}

		specialAbilities.sort(Comparator.comparingInt(SpecialAbility::getPriority));

		for (SpecialAbility attribute : specialAbilities) {
			Attributes statEquivalent = ((StatEquivalentProvider)attribute).getStatEquivalent(statProvider);
			addAttributes(statEquivalent);
		}

		complexAttributes.remove(ComplexAttributeId.SPECIAL_ABILITIES);
	}

	public AttributesAccessor nothingToSolve() {
		return new AttributesAccessor();
	}

	public AttributesAccessor solveAllLeaveAbilities() {
		solveSetBonuses();
		solveSockets();
		return new AttributesAccessor();
	}

	public AttributesAccessor solveAll(Function<AttributeEvaluator, StatProvider> statProviderFactory) {
		solveSetBonuses();
		solveSockets();
		solveAbilities(statProviderFactory.apply(this));
		return new AttributesAccessor();
	}

	public class AttributesAccessor {
		private AttributesAccessor() {}

		public Attributes getAttributes() {
			return AttributeEvaluator.this.getAttributes();
		}
	}
}

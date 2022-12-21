package wow.character.util;

import wow.character.model.equipment.ItemSockets;
import wow.commons.model.attributes.*;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.item.*;
import wow.commons.util.AttributesBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public class AttributeEvaluator implements AttributeCollector<AttributeEvaluator> {
	private final Map<AttributeCondition, Map<PrimitiveAttributeId, Double>> primitiveAttributes = new HashMap<>();
	private final Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributes = new EnumMap<>(ComplexAttributeId.class);

	private AttributeEvaluator() {}

	public static AttributeEvaluator of() {
		return new AttributeEvaluator();
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

		AttributeCondition condition = attribute.getCondition();
		PrimitiveAttributeId id = attribute.getId();

		var map = primitiveAttributes.computeIfAbsent(condition, x -> new EnumMap<>(PrimitiveAttributeId.class));
		map.put(id, attribute.getDouble() + map.getOrDefault(id, 0.0));

		return this;
	}

	@Override
	public AttributeEvaluator addAttribute(ComplexAttribute attribute) {
		if (attribute == null) {
			return this;
		}

		complexAttributes.computeIfAbsent(attribute.getId(), x -> new ArrayList<>())
				.add(attribute);

		return this;
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
			for (int i = 0; i < itemSockets.getSocketCount(); ++i) {
				Gem gem = itemSockets.getGem(i);
				if (gem != null && (gem.getColor() != GemColor.META || gem.isMetaConditionTrue(numRed, numYellow, numBlue))) {
					addAttributes(gem);
				}
			}
			if (itemSockets.allSocketsHaveMatchingGems(numRed, numYellow, numBlue)) {
				addAttributes(itemSockets.getSocketBonus());
			}
		}

		complexAttributes.remove(ComplexAttributeId.SOCKETS);
	}

	public Attributes nothingToSolve() {
		return getAttributes();
	}

	public Attributes solveAllLeaveAbilities() {
		solveSetBonuses();
		solveSockets();
		return getAttributes();
	}

	private Attributes getAttributes() {
		AttributesBuilder result = new AttributesBuilder();
		processConditionalAttributes(result);
		processComplexAttributes(result);
		return result.toAttributes();
	}

	private void processConditionalAttributes(AttributesBuilder result) {
		for (var entry : primitiveAttributes.entrySet()) {
			var condition = entry.getKey();
			for (var entry2 : entry.getValue().entrySet()) {
				var id = entry2.getKey();
				var value = entry2.getValue();
				result.addAttribute(id, value, condition);
			}
		}
	}

	private void processComplexAttributes(AttributesBuilder result) {
		for (var entry : complexAttributes.entrySet()) {
			result.addComplexAttributeList(entry.getValue());
		}
	}
}

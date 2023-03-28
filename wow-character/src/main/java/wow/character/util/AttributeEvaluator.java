package wow.character.util;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.attributes.*;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.item.*;
import wow.commons.model.spells.SpellId;
import wow.commons.util.AttributesBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.EFFECT_PCT;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public class AttributeEvaluator implements AttributeCollector {
	private final Map<PrimitiveAttributeEntry, PrimitiveAttributeEntry> primitiveAttributes = new HashMap<>();
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

		for (var attribute : attributes.getPrimitiveAttributes()) {
			addAttribute(attribute);
		}

		for (var entry : attributes.getComplexAttributeMap().entrySet()) {
			for (ComplexAttribute attribute : entry.getValue()) {
				addAttribute(attribute);
			}
		}

		return this;
	}

	@Override
	public AttributeEvaluator addAttributes(AttributeSource attributeSource, SpellId sourceSpell) {
		if (attributeSource == null) {
			return this;
		}

		if (sourceSpell == null) {
			return addAttributes(attributeSource);
		}

		Attributes scaledAttributes = getScaledAttributes(attributeSource, sourceSpell);
		addAttributes(scaledAttributes);
		return this;
	}

	@Override
	public AttributeEvaluator addAttributes(Collection<? extends AttributeSource> attributeSources) {
		AttributeCollector.super.addAttributes(attributeSources);
		return this;
	}

	private Attributes getScaledAttributes(AttributeSource attributeSource, SpellId sourceSpell) {
		Double increasePct = getEffectIncreasePct(sourceSpell);
		return attributeSource.getAttributes().scale(1 + increasePct / 100);
	}

	private Double getEffectIncreasePct(SpellId sourceSpell) {
		var key = new PrimitiveAttributeEntry(EFFECT_PCT, AttributeCondition.of(sourceSpell));
		return primitiveAttributes.getOrDefault(key, key).value;
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

		var key = new PrimitiveAttributeEntry(id, condition);
		primitiveAttributes.computeIfAbsent(key, x -> key).value += attribute.getDouble();

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
		for (var key : primitiveAttributes.keySet()) {
			var id = key.id;
			var condition = key.condition;
			var value = key.value;
			result.addAttribute(id, value, condition);
		}
	}

	private void processComplexAttributes(AttributesBuilder result) {
		for (var entry : complexAttributes.entrySet()) {
			result.addComplexAttributeList(entry.getValue());
		}
	}

	@RequiredArgsConstructor
	@EqualsAndHashCode(of = { "id", "condition" })
	private static class PrimitiveAttributeEntry {
		final PrimitiveAttributeId id;
		final AttributeCondition condition;
		double value;
	}
}

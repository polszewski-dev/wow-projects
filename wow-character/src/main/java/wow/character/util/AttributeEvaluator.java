package wow.character.util;

import lombok.AllArgsConstructor;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.attribute.AttributeCollection;
import wow.commons.model.attribute.AttributeCollector;
import wow.commons.model.attribute.AttributeSource;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.model.attribute.complex.ComplexAttributeId;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.item.*;
import wow.commons.model.spell.SpellId;
import wow.commons.util.AttributesBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static wow.commons.model.attribute.primitive.PrimitiveAttributeId.EFFECT_PCT;

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
		double increasePct = getEffectIncreasePct(sourceSpell);
		return attributeSource.getAttributes().scale(1 + increasePct / 100);
	}

	private double getEffectIncreasePct(SpellId sourceSpell) {
		var key = getTranscientKey(EFFECT_PCT, AttributeCondition.of(sourceSpell));
		return primitiveAttributes.getOrDefault(key, key).value;
	}

	private final PrimitiveAttributeEntry transcientKey = new PrimitiveAttributeEntry(null, null, 0);

	private PrimitiveAttributeEntry getTranscientKey(PrimitiveAttributeId id, AttributeCondition condition) {
		transcientKey.id = id;
		transcientKey.condition = condition;
		return transcientKey;
	}

	public AttributeEvaluator addAttributes(AttributeCollection attributeCollection) {
		attributeCollection.collectAttributes(this);
		return this;
	}

	private void addAttribute(PrimitiveAttribute attribute) {
		if (attribute == null) {
			return;
		}

		AttributeCondition condition = attribute.condition();
		PrimitiveAttributeId id = attribute.id();

		var key = getTranscientKey(id, condition);

		PrimitiveAttributeEntry entry = primitiveAttributes.get(key);

		if (entry != null) {
			entry.value += attribute.getDouble();
			return;
		}

		entry = new PrimitiveAttributeEntry(id, condition, attribute.getDouble());
		primitiveAttributes.put(entry, entry);
	}

	@Override
	public AttributeEvaluator addAttribute(ComplexAttribute attribute) {
		if (attribute == null) {
			return this;
		}

		complexAttributes.computeIfAbsent(attribute.id(), x -> new ArrayList<>())
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
				if (numPieces >= itemSetBonus.numPieces()) {
					addAttributes(itemSetBonus.bonusStats());
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

	@AllArgsConstructor
	private static class PrimitiveAttributeEntry {
		PrimitiveAttributeId id;
		AttributeCondition condition;
		double value;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			PrimitiveAttributeEntry that = (PrimitiveAttributeEntry) o;

			if (id != that.id) return false;
			return condition.equals(that.condition);
		}

		@Override
		public int hashCode() {
			int result = id.hashCode();
			result = 31 * result + condition.hashCode();
			return result;
		}
	}
}

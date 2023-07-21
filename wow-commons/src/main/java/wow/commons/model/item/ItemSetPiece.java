package wow.commons.model.item;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public record ItemSetPiece(Item item) implements ComplexAttribute {
	public ItemSetPiece {
		Objects.requireNonNull(item);
	}

	@Override
	public ComplexAttributeId id() {
		return ComplexAttributeId.SET_PIECES;
	}

	@Override
	public AttributeCondition condition() {
		return AttributeCondition.EMPTY;
	}

	public Item getItem() {
		return item;
	}

	public ItemSet getItemSet() {
		return item.getItemSet();
	}

	@Override
	public ItemSetPiece attachCondition(AttributeCondition condition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return String.format("%s - %s", item.getItemSet().getName(), item.getName());
	}
}

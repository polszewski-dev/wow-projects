package wow.commons.model.item;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.ComplexAttribute;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public class ItemSetPiece extends ComplexAttribute {
	private final Item item;

	public ItemSetPiece(Item item) {
		super(AttributeId.SetPieces, null);
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	public ItemSet getItemSet() {
		return item.getItemSet();
	}

	@Override
	public ComplexAttribute attachCondition(AttributeCondition condition) {
		throw new IllegalArgumentException("ItemSetPiece can't be conditional");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemSetPiece)) return false;
		ItemSetPiece that = (ItemSetPiece) o;
		return item.equals(that.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(item);
	}

	@Override
	public String toString() {
		return String.format("%s - %s", item.getItemSet().getName(), item.getName());
	}
}
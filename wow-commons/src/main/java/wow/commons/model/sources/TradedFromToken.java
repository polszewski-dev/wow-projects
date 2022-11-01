package wow.commons.model.sources;

import wow.commons.model.item.Item;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public class TradedFromToken extends NotSourcedFromInstance {
	private final Item item;

	public TradedFromToken(Item item) {
		this.item = item;
	}

	@Override
	public Item getSourceToken() {
		return item;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TradedFromToken that = (TradedFromToken) o;
		return item.equals(that.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(item);
	}

	@Override
	public String toString() {
		return "Token: " + item.getName();
	}
}

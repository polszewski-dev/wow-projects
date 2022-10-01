package wow.commons.model.sources;

import wow.commons.model.item.ItemLink;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class TradedFromToken extends NotSourcedFromInstance {
	private final ItemLink tokenLink;

	TradedFromToken(ItemLink tokenLink) {
		super(null);
		if (tokenLink == null) {
			throw new NullPointerException();
		}
		this.tokenLink = tokenLink;
	}

	@Override
	public ItemLink getTradedFromToken() {
		return tokenLink;
	}

	@Override
	protected int getDefaultPhase() {
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TradedFromToken that = (TradedFromToken) o;
		return tokenLink.equals(that.tokenLink);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tokenLink);
	}

	@Override
	public String toString() {
		return "Token: " + tokenLink.getName();
	}
}

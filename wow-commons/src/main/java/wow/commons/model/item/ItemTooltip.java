package wow.commons.model.item;

import wow.commons.model.sources.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-01-22
 */
public class ItemTooltip {
	private final ItemLink itemLink;
	private final long order;
	private final Set<Source> sources;
	private final List<String> left;
	private final List<String> right;

	public ItemTooltip(ItemLink itemLink, long order, Set<Source> sources, List<String> left, List<String> right) {
		this.itemLink = itemLink;
		this.order = order;
		this.sources = sources;
		this.left = left;
		this.right = right;
	}

	public ItemLink getItemLink() {
		return itemLink;
	}

	public long getOrder() {
		return order;
	}

	public Set<Source> getSources() {
		return sources;
	}

	public List<String> getLeft() {
		return left;
	}

	public List<String> getRight() {
		return right;
	}

	public List<String> getLines() {
		List<String> result = new ArrayList<>(left);
		result.addAll(right);
		return result;
	}
}

package wow.commons.model.item;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.sources.Source;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public class Gem implements AttributeSource, Sourced {
	private final ItemTooltip tooltip;
	private final GemColor color;
	private final Binding binding;
	private final boolean unique;
	private final Attributes stats;
	private final List<MetaEnabler> metaEnablers;

	public Gem(ItemTooltip tooltip, GemColor color, Binding binding, boolean unique, Attributes stats, List<MetaEnabler> metaEnablers) {
		this.tooltip = tooltip;
		this.color = color;
		this.binding = binding;
		this.unique = unique;
		this.stats = stats;
		this.metaEnablers = metaEnablers;
	}

	public int getId() {
		return getItemLink().getId();
	}

	public String getName() {
		return getItemLink().getName();
	}

	public String getShorterName() {
		if (color == GemColor.Meta) {
			return getName();
		}
		return stats.toString();
	}

	public ItemRarity getRarity() {
		return getItemLink().getRarity();
	}

	private ItemLink getItemLink() {
		return tooltip.getItemLink();
	}

	public GemColor getColor() {
		return color;
	}

	public Binding getBinding() {
		return binding;
	}

	public boolean isUnique() {
		return unique;
	}

	@Override
	public Attributes getAttributes() {
		return stats;
	}

	@Override
	public Set<Source> getSources() {
		return tooltip.getSources();
	}

	public boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue) {
		if (metaEnablers == null) {
			return true;
		}

		return metaEnablers.stream().allMatch(metaEnabler -> metaEnabler.isMetaConditionTrue(numRed, numYellow, numBlue));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Gem)) return false;
		Gem gem = (Gem) o;
		return getName().equals(gem.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", getName(), stats);
	}
}

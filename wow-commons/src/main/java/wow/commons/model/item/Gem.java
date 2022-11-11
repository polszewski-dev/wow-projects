package wow.commons.model.item;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.sources.Source;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public class Gem extends AbstractItem {
	private final GemColor color;
	private final List<MetaEnabler> metaEnablers;

	public Gem(int itemId, String name, ItemRarity rarity, Set<Source> sources, GemColor color, List<MetaEnabler> metaEnablers, Attributes stats) {
		super(itemId, name, rarity, stats, sources);
		this.color = color;
		this.metaEnablers = metaEnablers;
	}

	public String getShorterName() {
		if (color == GemColor.META) {
			return getName();
		}
		return getAttributes().toString();
	}

	public GemColor getColor() {
		return color;
	}

	public List<MetaEnabler> getMetaEnablers() {
		return metaEnablers;
	}

	public boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue) {
		if (metaEnablers == null) {
			return true;
		}

		return metaEnablers.stream().allMatch(metaEnabler -> metaEnabler.isMetaConditionTrue(numRed, numYellow, numBlue));
	}
}

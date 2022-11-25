package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
@Getter
public class Gem extends AbstractItem {
	private final GemColor color;
	private final List<MetaEnabler> metaEnablers;

	public Gem(Integer id, Description description, Restriction restriction, Attributes attributes, BasicItemInfo basicItemInfo, GemColor color, List<MetaEnabler> metaEnablers) {
		super(id, description, restriction, attributes, basicItemInfo);
		this.color = color;
		this.metaEnablers = metaEnablers;
		if (getItemType() != ItemType.GEM) {
			throw new IllegalArgumentException("Wrong item type: " + getItemType());
		}
	}

	public String getShorterName() {
		if (color == GemColor.META) {
			return getName();
		}
		return getAttributes().toString();
	}

	public boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue) {
		if (metaEnablers == null) {
			return true;
		}

		return metaEnablers.stream().allMatch(metaEnabler -> metaEnabler.isMetaConditionTrue(numRed, numYellow, numBlue));
	}
}

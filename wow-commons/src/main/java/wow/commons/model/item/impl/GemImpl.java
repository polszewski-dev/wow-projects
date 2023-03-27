package wow.commons.model.item.impl;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class GemImpl extends AbstractItemImpl implements Gem {
	private final GemColor color;
	private final List<MetaEnabler> metaEnablers;

	public GemImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Attributes attributes,
			BasicItemInfo basicItemInfo,
			GemColor color,
			List<MetaEnabler> metaEnablers
	) {
		super(id, description, timeRestriction, characterRestriction, attributes, basicItemInfo);
		this.color = color;
		this.metaEnablers = metaEnablers;
		assertItemType(ItemType.GEM);
	}
}

package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@Setter
public class GemImpl extends AbstractItemImpl implements Gem {
	private List<Effect> effects;
	private final GemColor color;
	private final List<MetaEnabler> metaEnablers;
	private final Set<PveRole> pveRoles;

	public GemImpl(
			int id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BasicItemInfo basicItemInfo,
			GemColor color,
			List<MetaEnabler> metaEnablers,
			Set<PveRole> pveRoles
	) {
		super(id, description, timeRestriction, characterRestriction, basicItemInfo);
		this.color = color;
		this.metaEnablers = metaEnablers;
		this.pveRoles = pveRoles;
		assertItemType(ItemType.GEM);
	}

	@Override
	public boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue) {
		if (metaEnablers.isEmpty()) {
			return true;
		}

		for (var metaEnabler : metaEnablers) {
			if (!metaEnabler.isMetaConditionTrue(numRed, numYellow, numBlue)) {
				return false;
			}
		}

		return true;
	}
}

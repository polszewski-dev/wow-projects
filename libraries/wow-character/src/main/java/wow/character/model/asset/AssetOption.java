package wow.character.model.asset;

import wow.commons.util.EnumUtil;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
public sealed interface AssetOption {
	enum ExclusionGroup {
		ARMOR,
		CURSE,
		DEMONIC_SACRIFICE,
		FIRE_TOTEM,
		AIR_TOTEM,
		WATER_TOTEM,
		BLESSING,
		AURA,
		;

		public static ExclusionGroup parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	record SingleOption(
			String name,
			boolean isDefault,
			ExclusionGroup exclusionGroup,
			String talentName,
			String preparationPhaseScript,
			String warmUpPhaseScript
	) implements AssetOption {
		public SingleOption {
			Objects.requireNonNull(name);
		}
	}

	record OneOfManyOption(
			List<SingleOption> list
	) implements AssetOption {
		public OneOfManyOption {
			Objects.requireNonNull(list);
		}
	}
}

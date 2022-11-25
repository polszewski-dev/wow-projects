package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@AllArgsConstructor
@Getter
public class CastInfo {
	private final int manaCost;
	@NonNull
	private final Duration castTime;
	private final boolean channeled;
	private final AdditionalCost additionalCost;
	private final AppliedEffect appliedEffect;
}

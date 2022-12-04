package wow.commons.model.config;

import lombok.*;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class ConfigurationElement<T> implements Restricted {
	@NonNull
	private final T id;

	@NonNull
	private final Description description;

	@NonNull
	private final Restriction restriction;

	public int getRequiredLevel() {
		return restriction.getRequiredLevel();
	}

	public String getName() {
		return description.getName();
	}

	public String getIcon() {
		return description.getIcon();
	}

	public String getTooltip() {
		return description.getTooltip();
	}

	public boolean isAvailableDuring(Phase phase) {
		return restriction.getPhase().isEarlierOrTheSame(phase);
	}

	@Override
	public String toString() {
		return getName();
	}
}

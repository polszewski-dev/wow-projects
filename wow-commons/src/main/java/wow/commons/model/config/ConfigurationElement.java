package wow.commons.model.config;

import lombok.*;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class ConfigurationElement<T> implements TimeRestricted, CharacterRestricted {
	@NonNull
	private final T id;

	@NonNull
	private final Description description;

	@NonNull
	private final TimeRestriction timeRestriction;

	@NonNull
	private final CharacterRestriction characterRestriction;

	public int getRequiredLevel() {
		return characterRestriction.getLevel();
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

	@Override
	public String toString() {
		return getName();
	}
}

package wow.commons.model.config;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public interface Described {
	Description getDescription();

	default String getName() {
		return getDescription().name();
	}

	default String getIcon() {
		return getDescription().icon();
	}

	default String getTooltip() {
		return getDescription().tooltip();
	}
}

package wow.commons.model;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
public interface Condition {
	default boolean isEmpty() {
		return false;
	}
}

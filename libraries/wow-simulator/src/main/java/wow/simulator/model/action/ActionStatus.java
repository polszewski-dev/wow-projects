package wow.simulator.model.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
@AllArgsConstructor
@Getter
public enum ActionStatus {
	CREATED(false),
	IN_PROGRESS(false),
	FINISHED(true),
	INTERRUPTED(true);

	private final boolean terminal;
}

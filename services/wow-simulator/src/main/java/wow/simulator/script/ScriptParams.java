package wow.simulator.script;

import wow.simulator.model.unit.Unit;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 06.08.2026
 */
public record ScriptParams(
		Unit caster
) {
	public ScriptParams {
		Objects.requireNonNull(caster);
	}
}

package wow.simulator.model.context;

import lombok.Getter;
import wow.commons.model.spell.Spell;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public abstract class Context implements SimulationContextSource {
	protected final Unit caster;
	protected final Spell spell;

	protected final Context parentContext;

	protected Context(Unit caster, Spell spell, Context parentContext) {
		this.caster = caster;
		this.spell = spell;
		this.parentContext = parentContext;
	}

	@Override
	public SimulationContext getSimulationContext() {
		return caster.getSimulationContext();
	}
}

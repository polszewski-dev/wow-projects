package wow.simulator.model.context;

import lombok.Getter;
import wow.commons.model.spell.Spell;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public abstract class Context implements SimulationContextSource {
	protected final Unit caster;
	protected final Spell spell;

	protected final int serialNo;
	private static int serialNoGenerator;

	protected Context(Unit caster, Spell spell) {
		this.caster = caster;
		this.spell = spell;
		this.serialNo = serialNoGenerator++;
	}

	@Override
	public SimulationContext getSimulationContext() {
		return caster.getSimulationContext();
	}

	public abstract Context getParentContext();

	protected CommandContext getParentCommandContext() {
		var parentContext = getParentContext();

		if (parentContext == null) {
			return null;
		}

		if (parentContext instanceof CommandContext commandContext) {
			return commandContext;
		}

		return parentContext.getParentCommandContext();
	}

	public List<Context> contextTrace() {
		var result = new ArrayList<Context>();

		for (var context = this; context != null; context = context.getParentContext()) {
			result.add(context);
		}

		return result;
	}

	@Override
	public abstract String toString();
}

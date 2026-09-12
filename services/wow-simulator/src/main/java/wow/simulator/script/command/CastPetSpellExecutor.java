package wow.simulator.script.command;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.CastPetSpell;

/**
 * User: POlszewski
 * Date: 2026-08-12
 */
public class CastPetSpellExecutor extends DynamicExecutor {
	private final AbilityId abilityId;

	private CastPetSpellExecutor(CastPetSpell command, ScriptParams params) {
		super(params, command.condition(), command.target(), command.optional());
		this.abilityId = command.abilityId();
	}

	public static CastPetSpellExecutor create(CastPetSpell command, ScriptParams params) {
		return new CastPetSpellExecutor(command, params);
	}

	@Override
	public boolean isValid() {
		return true;//to be checked at runtime
	}

	@Override
	public boolean allConditionsAreMet() {
		return getActualCaster() != null && getAbility() != null && super.allConditionsAreMet();
	}

	@Override
	protected Unit getActualCaster() {
		if (caster.isPet()) {
			return caster;
		}

		return caster.getActivePet();
	}

	@Override
	public void execute() {
		caster.petCast(
				this::getAbility,
				this::getTarget
		);
	}

	@Override
	protected Ability getAbility() {
		return getActualCaster().getAbility(abilityId).orElse(null);
	}
}

package wow.simulator.model.context;

import wow.character.model.snapshot.SpellCastSnapshot;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.GroupCooldownId;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

/**
 * User: POlszewski
 * Date: 2023-08-13
 */
public class SpellCastContext extends Context {
	private final SpellCastSnapshot snapshot;
	private final Ability ability;
	private final TargetResolver targetResolver;

	public SpellCastContext(Unit caster, Ability ability, TargetResolver targetResolver, SpellCastSnapshot snapshot) {
		super(caster, ability, null);
		this.snapshot = snapshot;
		this.ability = ability;
		this.targetResolver = targetResolver;
	}

	public Duration getGcd() {
		return Duration.seconds(snapshot.getGcd());
	}

	public Duration getCastTime() {
		return Duration.seconds(snapshot.getCastTime());
	}

	public void paySpellCost() {
		if (ability instanceof ActivatedAbility activatedAbility) {
			caster.triggerCooldown(ability, ability.getCooldown());
			triggerGroupCooldown(activatedAbility);
		} else {
			var costSnapshot = caster.paySpellCost(ability);
			var cooldown = Duration.seconds(costSnapshot.getCooldown());

			caster.triggerCooldown(ability, cooldown);
			setPaidCost(costSnapshot);
		}
	}

	private void triggerGroupCooldown(ActivatedAbility activatedAbility) {
		var groupCooldownId = activatedAbility.getGroupCooldownId();

		if (groupCooldownId != null) {
			var duration = getGroupCooldownDuration(groupCooldownId);
			caster.triggerCooldown(groupCooldownId, duration);
		}
	}

	private Duration getGroupCooldownDuration(GroupCooldownId groupCooldownId) {
		return switch (groupCooldownId.group()) {
			case POTION, CONJURED_ITEM ->
					Duration.seconds(120);
			case TRINKET ->
					(Duration) ability.getApplyEffectCommands().getFirst().duration();
		};
	}

	public SpellResolutionContext createSpellResolutionContext(CastSpellAction action) {
		return new SpellResolutionContext(caster, spell, targetResolver, this, action);
	}
}

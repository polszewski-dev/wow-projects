package wow.simulator.model.context;

import wow.character.model.snapshot.SpellCastSnapshot;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.GroupCooldownId;
import wow.commons.model.spell.SpellTarget;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.UnitId;

import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-13
 */
public class SpellCastContext extends Context {
	private final SpellCastSnapshot snapshot;
	private final Ability ability;
	private final Map<UnitId, SpellResolutionContext> spellResolutionContextMap = new HashMap<>();

	public SpellCastContext(Unit caster, Ability ability, SpellCastSnapshot snapshot) {
		super(caster, ability);
		this.snapshot = snapshot;
		this.ability = ability;
	}

	public Duration getGcd() {
		return Duration.seconds(snapshot.getGcd());
	}

	public Duration getCastTime() {
		return Duration.seconds(snapshot.getCastTime());
	}

	public boolean isInstantCast() {
		return snapshot.isInstantCast();
	}

	@Override
	public SpellCastConversions getConversions() {
		return new SpellCastConversions(caster, ability);
	}

	public void paySpellCost() {
		if (ability instanceof ActivatedAbility activatedAbility) {
			caster.triggerCooldown(ability, ability.getCooldown());
			triggerGroupCooldown(activatedAbility);
		} else {
			var costSnapshot = caster.paySpellCost(ability);
			var cooldown = Duration.seconds(costSnapshot.getCooldown());
			var cost = costSnapshot.getCostToPayUnreduced();

			caster.triggerCooldown(ability, cooldown);
			getConversions().performPaidCostConversion(cost);
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
					ability.getEffectApplication().duration();
		};
	}

	public SpellResolutionContext getSpellResolutionContext(SpellTarget spellTarget, TargetResolver targetResolver) {
		var target = targetResolver.getTarget(spellTarget);

		return getSpellResolutionContext(target);
	}

	public SpellResolutionContext getSpellResolutionContext(Unit target) {
		return spellResolutionContextMap.computeIfAbsent(
				target.getId(),
				x -> new SpellResolutionContext(caster, ability, target)
		);
	}
}

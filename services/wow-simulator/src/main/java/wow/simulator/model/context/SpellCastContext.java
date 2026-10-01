package wow.simulator.model.context;

import lombok.Getter;
import wow.character.model.snapshot.SpellCastSnapshot;
import wow.character.model.snapshot.SpellCostSnapshot;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.GroupCooldownId;
import wow.simulator.model.unit.PrimaryTarget;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-13
 */
public class SpellCastContext extends Context {
	private final Ability ability;
	private final PrimaryTarget primaryTarget;
	private final SpellCastSnapshot snapshot;

	@Getter
	private int lastManaPaid;
	@Getter
	private int lastHealthPaid;

	public SpellCastContext(Unit caster, Ability ability, PrimaryTarget primaryTarget) {
		super(caster, ability);
		this.ability = ability;
		this.primaryTarget = primaryTarget;
		this.snapshot = caster.getSpellCastSnapshot(ability, primaryTarget.getSingleTarget());
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
			var costSnapshot = caster.paySpellCost(ability, primaryTarget, this);
			var cooldown = Duration.seconds(costSnapshot.getCooldown());

			caster.triggerCooldown(ability, cooldown);
			setPaidCost(costSnapshot);
		}
	}

	private void setPaidCost(SpellCostSnapshot costSnapshot) {
		var cost = costSnapshot.getCostToPayUnreduced();

		switch (cost.resourceType()) {
			case MANA -> this.lastManaPaid = cost.amount();
			case HEALTH -> this.lastHealthPaid = cost.amount();
			default -> {
				// ignored
			}
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

	public void resolveCastSpell() {
		var spellResolutionContext = new SpellResolutionContext(caster, ability, this);

		spellResolutionContext.resolveCastSpell(primaryTarget);
	}
}

package wow.simulator.model.unit.action;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.context.EventContext;
import wow.simulator.model.context.SpellCastContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.PrimaryTarget;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.UnitImpl;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class CastSpellAction extends UnitAction {
	@Getter
	private final Ability ability;
	private final PrimaryTarget primaryTarget;

	private TargetResolver targetResolver;
	private SpellCastContext castContext;
	private List<EffectInstance> appliedEffects = List.of();

	public CastSpellAction(Unit owner, Ability ability, PrimaryTarget primaryTarget) {
		super(owner);
		this.ability = ability;
		this.primaryTarget = primaryTarget;
	}

	@Override
	protected void setUp() {
		if (!owner.canCast(ability, primaryTarget)) {
			getGameLog().canNotBeCasted(this);
			finish();
			return;
		}

		createTargetResolver();
		createSpellCastContext();
		performCast();
	}

	@Override
	protected void onFinished() {
		if (ability.isChanneled() && !appliedEffects.isEmpty()) {
			var channelAction = new ChannelSpellAction(owner, ability, appliedEffects.getFirst());

			((UnitImpl) owner).replaceCurrentAction(channelAction);
		} else {
			((UnitImpl) owner).actionTerminated(this);
		}
	}

	@Override
	protected void onInterrupted() {
		getGameLog().castInterrupted(this);
		((UnitImpl) owner).actionTerminated(this);
	}

	private void createTargetResolver() {
		this.targetResolver = primaryTarget.getTargetResolver(owner);
	}

	private void createSpellCastContext() {
		var castSnapshot = owner.getSpellCastSnapshot(ability);

		this.castContext = new SpellCastContext(owner, ability, targetResolver, castSnapshot);
	}

	private void performCast() {
		onBeginCast();

		if (triggersGcd()) {
			owner.triggerGcd(castContext.getGcd());
		}

		fromNowAfter(
				castContext.getCastTime(),
				() -> {
					onEndCast();
					paySpellCost();
					resolveSpell();
				}
		);
	}

	private void onBeginCast() {
		getGameLog().beginCast(this);
	}

	private void onEndCast() {
		getGameLog().endCast(this);
	}

	private void paySpellCost() {
		castContext.paySpellCost();
		removeEffectRemovedOnHit();
		EventContext.fireSpellCastEvent(owner, primaryTarget.getSingleTarget(), ability, castContext);
	}

	private void removeEffectRemovedOnHit() {
		var effectRemovedOnHit = ability.getEffectRemovedOnHit();

		if (effectRemovedOnHit != null) {
			primaryTarget.requireSingleTarget()
					.removeEffect(effectRemovedOnHit, owner);
		}
	}

	private void resolveSpell() {
		var spellResolutionContext = castContext.createSpellResolutionContext(this);

		this.appliedEffects = spellResolutionContext.resolveCastSpell();
	}

	public String getAbilityName() {
		return ability.getName();
	}

	public Duration getCastTime() {
		return castContext.getCastTime();
	}

	@Override
	public boolean triggersGcd() {
		return !ability.getCastInfo().ignoresGcd();
	}
}

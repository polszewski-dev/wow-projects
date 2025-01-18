package wow.simulator.model.unit.action;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.context.EventContext;
import wow.simulator.model.context.SpellCastContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.PrimaryTarget;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;

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

	private EffectInstance channeledEffect;

	public CastSpellAction(Unit owner, Ability ability, PrimaryTarget primaryTarget) {
		super(owner);
		this.ability = ability;
		this.primaryTarget = primaryTarget;
	}

	@Override
	protected void setUp() {
		this.targetResolver = primaryTarget.getTargetResolver(owner);

		if (!owner.canCast(ability, primaryTarget)) {
			getGameLog().canNotBeCasted(this);
			finish();
			return;
		}

		createSpellCastContext();

		if (ability.isChanneled()) {
			channelSpell();
		} else if (castContext.isInstantCast()) {
			instaCastSpell();
		} else {
			castSpell();
		}

		if (!ability.getCastInfo().ignoresGcd()) {
			owner.triggerGcd(castContext.getGcd(), this);
		}
	}

	private void createSpellCastContext() {
		var castSnapshot = owner.getSpellCastSnapshot(ability);

		this.castContext = new SpellCastContext(owner, ability, targetResolver, castSnapshot);
	}

	private void instaCastSpell() {
		beginCast();
		endCast();
	}

	private void castSpell() {
		beginCast();

		fromNowAfter(castContext.getCastTime(), this::endCast);
	}

	private void beginCast() {
		onBeginCast();
	}

	private void endCast() {
		onEndCast();
		paySpellCost();
		resolveSpell();
	}

	private void channelSpell() {
		beginCast();
		endCast();
	}

	private void onBeginCast() {
		getGameLog().beginCast(this);
	}

	private void onEndCast() {
		getGameLog().endCast(this);
	}

	private void onBeginChannel() {
		getGameLog().beginChannel(this);
	}

	private void onEndChannel() {
		getGameLog().endChannel(this);
	}

	private void paySpellCost() {
		castContext.paySpellCost();
		removeEffectRemovedOnHit();
		EventContext.fireSpellCastEvent(owner, primaryTarget.getSingleTarget(), ability);
	}

	private void removeEffectRemovedOnHit() {
		var effectRemovedOnHit = ability.getEffectRemovedOnHit();

		if (effectRemovedOnHit != null) {
			primaryTarget.requireSingleTarget()
					.removeEffect(effectRemovedOnHit, owner);
		}
	}

	private void resolveSpell() {
		castContext.createSpellResolutionContext();

		for (var directComponent : ability.getDirectComponents()) {
			getSimulation().delayedAction(
					getDelay(directComponent), () -> directComponentAction(directComponent)
			);
		}

		applyEffect();
	}

	private Duration getDelay(DirectComponent directComponent) {
		var flightTime = Duration.ZERO;
		return directComponent.bolt() ? flightTime : Duration.ZERO;
	}

	private void directComponentAction(DirectComponent directComponent) {
		var resolutionContext = castContext.getSpellResolutionContext();

		resolutionContext.directComponentAction(directComponent, this);
	}

	private void applyEffect() {
		var effectApplication = ability.getEffectApplication();

		if (effectApplication == null) {
			return;
		}

		var resolutionContext = castContext.getSpellResolutionContext();

		if (ability.isChanneled()) {
			if (!resolutionContext.hitRoll(this, primaryTarget.requireSingleTarget())) {
				return;
			}
			onBeginChannel();
		}

		var appliedEffect = resolutionContext.applyEffect(this, primaryTarget.requireSingleTarget());

		if (ability.isChanneled()) {
			this.channeledEffect = appliedEffect;
			this.channeledEffect.setOnEffectFinished(this::onEndChannel);
			fromNowAfter(channeledEffect.getDuration(), () -> {});
		}
	}

	@Override
	public void onRemovedFromQueue() {
		if (ability.isChanneled()) {
			getGameLog().channelInterrupted(this);
		} else {
			getGameLog().castInterrupted(this);
		}
		super.onRemovedFromQueue();
		if (ability.isChanneled()) {
			channeledEffect.removeSelf();
		}
	}

	public AbilityId getAbilityId() {
		return ability.getAbilityId();
	}

	public Duration getCastTime() {
		return castContext.getCastTime();
	}
}

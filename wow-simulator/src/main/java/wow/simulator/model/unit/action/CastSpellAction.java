package wow.simulator.model.unit.action;

import wow.commons.model.Duration;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.context.SpellCastContext;
import wow.simulator.model.effect.UnitEffect;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class CastSpellAction extends UnitAction {
	private final Ability ability;
	private final TargetResolver targetResolver;

	private SpellCastContext castContext;

	private UnitEffect channeledEffect;

	public CastSpellAction(Unit owner, Ability ability, TargetResolver targetResolver) {
		super(owner);
		this.ability = ability;
		this.targetResolver = targetResolver;
	}

	@Override
	protected void setUp() {
		if (!owner.canCast(ability, targetResolver)) {
			getGameLog().canNotBeCasted(this);
			finish();
			return;
		}

		this.castContext = owner.getSpellCastContext(ability);

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
		// events here
	}

	private void onEndCast() {
		getGameLog().endCast(this);
		// events here
	}

	private void onBeginChannel() {
		getGameLog().beginChannel(this);
	}

	private void onEndChannel() {
		getGameLog().endChannel(this);
	}

	private void paySpellCost() {
		castContext.paySpellCost();
	}

	private void resolveSpell() {
		for (var directComponent : ability.getDirectComponents()) {
			getSimulation().delayedAction(
					getDelay(directComponent),
					() -> directComponentAction(directComponent)
			);
		}
		applyEffect();
	}

	private Duration getDelay(DirectComponent directComponent) {
		var flightTime = Duration.ZERO;
		return directComponent.bolt() ? flightTime : Duration.ZERO;
	}

	private void directComponentAction(DirectComponent directComponent) {
		if (directComponent.type() == ComponentType.DAMAGE) {
			dealDirectDamage(directComponent);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void dealDirectDamage(DirectComponent directComponent) {
		var target = targetResolver.getTarget(directComponent.target());
		var resolutionContext = castContext.getSpellResolutionContext(target);

		resolutionContext.dealDirectDamage(directComponent, this);
	}

	private void applyEffect() {
		var effectApplication = ability.getEffectApplication();

		if (effectApplication == null) {
			return;
		}

		var target = targetResolver.getTarget(effectApplication.target());
		var resolutionContext = castContext.getSpellResolutionContext(target);

		if (ability.isChanneled()) {
			if (!resolutionContext.hitRoll(this)) {
				return;
			}
			onBeginChannel();
		}

		var appliedEffect = resolutionContext.applyEffect(this);

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

	public Ability getAbility() {
		return ability;
	}

	public AbilityId getAbilityId() {
		return ability.getAbilityId();
	}
}

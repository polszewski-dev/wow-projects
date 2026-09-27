package wow.simulator.model.unit.action;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.context.SpellCastContext;
import wow.simulator.model.unit.PrimaryTarget;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.UnitImpl;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class CastSpellAction extends UnitAction {
	@Getter
	private final Ability ability;
	private final Unit target;
	@Getter
	private PrimaryTarget primaryTarget;

	private SpellCastContext castContext;

	public CastSpellAction(Unit owner, Ability ability, Unit target) {
		super(owner);
		this.ability = ability;
		this.target = target;
	}

	@Override
	protected void setUp() {
		this.primaryTarget = owner.getPrimaryTarget(ability, target);

		if (!owner.canCast(ability, primaryTarget)) {
			getGameLog().canNotBeCasted(this);
			finish();
			return;
		}

		createSpellCastContext();
		performCast();
	}

	@Override
	protected void onFinished() {
		((UnitImpl) owner).actionTerminated(this);
	}

	@Override
	protected void onInterrupted() {
		getGameLog().castInterrupted(this);
		((UnitImpl) owner).actionTerminated(this);
	}

	private void createSpellCastContext() {
		this.castContext = new SpellCastContext(owner, ability, primaryTarget);
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
					fireSpellCastEvent();
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
	}

	private void fireSpellCastEvent() {
		owner.getEventBus().spellCast(ability, primaryTarget.getSingleTarget(), castContext);
	}

	private void resolveSpell() {
		var spellResolutionContext = castContext.createSpellResolutionContext(this);

		spellResolutionContext.resolveCastSpell();
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

package wow.simulator.model.unit.action;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2025-02-11
 */
public class ChannelSpellAction extends UnitAction {
	private final EffectInstance channeledEffect;
	@Getter
	private final Ability ability;

	public ChannelSpellAction(Unit owner, Ability ability, EffectInstance channeledEffect) {
		super(owner);
		this.ability = ability;
		this.channeledEffect = channeledEffect;
	}

	@Override
	protected void setUp() {
		this.channeledEffect.setOnEffectFinished(this::onEndChannel);
		fromNowAfter(channeledEffect.getDuration(), () -> {});
		onBeginChannel();
	}

	@Override
	protected void onInterrupted() {
		getGameLog().channelInterrupted(this);
		channeledEffect.removeSelf();

		super.onInterrupted();
	}

	private void onBeginChannel() {
		getGameLog().beginChannel(this);
	}

	private void onEndChannel() {
		getGameLog().endChannel(this);
	}

	public AbilityId getAbilityId() {
		return getAbility().getAbilityId();
	}

	public Duration getChannelTime() {
		return channeledEffect.getRemainingDuration();
	}

	@Override
	public boolean triggersGcd() {
		return false;
	}
}

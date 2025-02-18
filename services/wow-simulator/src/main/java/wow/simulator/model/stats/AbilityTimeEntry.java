package wow.simulator.model.stats;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@Getter
@Setter
public final class AbilityTimeEntry extends TimeEntry {
	private final Ability ability;
	private Time gcdEnd;
	private Time channelBegin;
	private Time channelEnd;

	public AbilityTimeEntry(Ability ability, Time begin) {
		super(begin);
		this.ability = ability;
	}

	@Override
	public void complete(Time time) {
		super.complete(time);
		if (gcdEnd == null) {
			gcdEnd = time;
		}
		if (channelBegin != null && channelEnd == null) {
			channelEnd = time;
		}
	}

	@Override
	public Duration getElapsedTime() {
		var gcd = gcdEnd.subtract(begin);

		if (channelBegin != null) {
			var castPlusChannel = channelEnd.subtract(begin);

			return castPlusChannel.max(gcd);
		}

		var cast = end.subtract(begin);

		return cast.max(gcd);
	}

	public void setChannelBegin(Time channelBegin) {
		if (!channelBegin.equals(end)) {
			throw new IllegalStateException();
		}

		this.channelBegin = channelBegin;
	}

	public boolean isFinished() {
		return end != null && (channelBegin == null || channelEnd != null);
	}
}

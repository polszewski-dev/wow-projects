package wow.simulator.converter;

import org.springframework.stereotype.Component;
import wow.character.model.character.Raid;
import wow.commons.client.converter.AbstractPlayerConverter;
import wow.commons.client.converter.AbstractRaidConverter;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.UnitRaid;

/**
 * User: POlszewski
 * Date: 2026-03-10
 */
@Component
public class RaidConverter extends AbstractRaidConverter<Player> {
	public RaidConverter(AbstractPlayerConverter<Player> playerConverter) {
		super(playerConverter);
	}

	@Override
	protected Raid<Player> newRaid() {
		return new UnitRaid<>();
	}
}

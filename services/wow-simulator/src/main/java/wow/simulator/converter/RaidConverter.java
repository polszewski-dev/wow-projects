package wow.simulator.converter;

import org.springframework.stereotype.Component;
import wow.commons.client.converter.AbstractPlayerConverter;
import wow.commons.client.converter.AbstractRaidConverter;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2026-03-10
 */
@Component
public class RaidConverter extends AbstractRaidConverter<Player> {
	public RaidConverter(AbstractPlayerConverter<Player> playerConverter) {
		super(playerConverter);
	}
}

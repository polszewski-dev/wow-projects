package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.commons.client.converter.AbstractPlayerConverter;
import wow.commons.client.converter.AbstractRaidConverter;
import wow.minmax.model.Player;

/**
 * User: POlszewski
 * Date: 2026-03-11
 */
@Component
public class RaidConverter extends AbstractRaidConverter<Player> {
	public RaidConverter(AbstractPlayerConverter<Player> playerConverter) {
		super(playerConverter);
	}
}

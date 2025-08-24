package wow.estimator.client.dto.upgrade;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record FindUpgradesResponseDTO(
		List<UpgradeDTO> upgrades
) {
}

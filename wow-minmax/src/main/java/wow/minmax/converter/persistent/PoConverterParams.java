package wow.minmax.converter.persistent;

import wow.commons.model.pve.PhaseId;
import wow.minmax.service.PlayerProfileService;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public class PoConverterParams {
	public static final String PLAYER_PROFILE_SERVICE = "playerProfileService";
	public static final String PHASE = "phase";

	public static Map<String, Object> createParams(PhaseId phaseId, PlayerProfileService playerProfileService) {
		return Map.of(
				PHASE, phaseId,
				PLAYER_PROFILE_SERVICE, playerProfileService
		);
	}

	public static PhaseId getPhase(Map<String, Object> params) {
		return (PhaseId) params.get(PHASE);
	}

	public static PlayerProfileService getPlayerProfileService(Map<String, Object> params) {
		return (PlayerProfileService) params.get(PLAYER_PROFILE_SERVICE);
	}

	private PoConverterParams() {}
}

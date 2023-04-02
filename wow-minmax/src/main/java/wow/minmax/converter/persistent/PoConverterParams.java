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
	public static final String PHASE_ID = "phaseId";

	public static Map<String, Object> createParams(PhaseId phaseId, PlayerProfileService playerProfileService) {
		return Map.of(
				PHASE_ID, phaseId,
				PLAYER_PROFILE_SERVICE, playerProfileService
		);
	}

	public static PhaseId getPhaseId(Map<String, Object> params) {
		return (PhaseId) params.get(PHASE_ID);
	}

	public static PlayerProfileService getPlayerProfileService(Map<String, Object> params) {
		return (PlayerProfileService) params.get(PLAYER_PROFILE_SERVICE);
	}

	private PoConverterParams() {}
}

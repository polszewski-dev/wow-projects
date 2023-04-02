package wow.minmax.converter.persistent;

import wow.commons.model.pve.PhaseId;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public class PoConverterParams {
	public static final String PHASE_ID = "phaseId";

	public static Map<String, Object> createParams(PhaseId phaseId) {
		return Map.of(
				PHASE_ID, phaseId
		);
	}

	public static PhaseId getPhaseId(Map<String, Object> params) {
		return (PhaseId) params.get(PHASE_ID);
	}

	private PoConverterParams() {}
}

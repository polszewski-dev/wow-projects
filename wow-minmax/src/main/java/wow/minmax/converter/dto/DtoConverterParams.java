package wow.minmax.converter.dto;

import wow.commons.model.pve.PhaseId;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public final class DtoConverterParams {
	public static final String PHASE = "phase";

	public static Map<String, Object> createParams(PhaseId phaseId) {
		return Map.of(PHASE, phaseId);
	}

	public static PhaseId getPhase(Map<String, Object> params) {
		return (PhaseId) params.get(PHASE);
	}

	private DtoConverterParams() {}
}

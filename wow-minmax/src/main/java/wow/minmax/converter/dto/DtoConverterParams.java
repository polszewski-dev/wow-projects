package wow.minmax.converter.dto;

import wow.commons.model.pve.Phase;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public final class DtoConverterParams {
	public static final String PHASE = "phase";

	public static Map<String, Object> createParams(Phase phase) {
		return Map.of(PHASE, phase);
	}

	public static Phase getPhase(Map<String, Object> params) {
		return (Phase) params.get(PHASE);
	}

	private DtoConverterParams() {}
}

package wow.minmax.converter.persistent;

import wow.character.model.character.Character;
import wow.commons.model.pve.PhaseId;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public class PoConverterParams {
	private static final String CHARACTER = "character";

	public static Map<String, Object> createParams(Character character) {
		return Map.of(
				CHARACTER, character
		);
	}

	public static PhaseId getPhaseId(Map<String, Object> params) {
		return getCharacter(params).getPhaseId();
	}

	public static Character getCharacter(Map<String, Object> params) {
		return (Character) params.get(CHARACTER);
	}

	private PoConverterParams() {}
}

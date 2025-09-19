package wow.character.model.script;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public record Script(
		Map<ScriptSectionType, ScriptSection> sectionsByType
) {
	public Script {
		Objects.requireNonNull(sectionsByType);
	}

	public ScriptSection getSection(ScriptSectionType sectionType) {
		return sectionsByType.getOrDefault(sectionType, ScriptSection.EMPTY);
	}
}

package wow.character.model.script;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static wow.character.model.script.ScriptCommandTarget.DEFAULT;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
@RequiredArgsConstructor
@Getter
class ScriptCommandConditionParser {
	private final String line;
	private List<ScriptCommandCondition> conditions = new ArrayList<>();
	private ScriptCommandTarget target;

	public void parse() {
		if (target == null) {
			target = DEFAULT;
		}
	}
}

package wow.simulator.script.command;

import lombok.RequiredArgsConstructor;
import wow.character.model.script.ScriptCommandCondition;
import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;

import java.util.regex.Pattern;

import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
@RequiredArgsConstructor
public class ScriptConditionChecker {
	private final Unit caster;
	private final Ability ability;
	private final Unit target;

	public boolean check(ScriptCommandCondition condition) {
		return switch (condition) {
			case Or(var left, var right) ->
					check(left) || check(right);

			case And(var left, var right) ->
					check(left) && check(right);

			case Not(var right) ->
					!check(right);

			case EmptyCondition() -> true;

			case CasterHasEffect(var effectNamePattern) ->
					caster.hasEffect(getPattern(effectNamePattern));

			case TargetHasEffect(var effectNamePattern) ->
					target.hasEffect(getPattern(effectNamePattern), caster);
		};
	}

	private Pattern getPattern(String regex) {
		var realRegex = "^" + regex.replace("*", ".*") + "$";

		return Pattern.compile(realRegex);
	}
}

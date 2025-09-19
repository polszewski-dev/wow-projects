package wow.estimator.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.script.ScriptCommand;
import wow.character.model.script.ScriptCompiler;
import wow.commons.model.spell.AbilityId;

import java.util.LinkedHashSet;
import java.util.List;

import static wow.character.model.script.ScriptCommand.CastSequence;
import static wow.character.model.script.ScriptCommand.CastSpell;
import static wow.character.model.script.ScriptSectionType.ROTATION;

/**
 * User: POlszewski
 * Date: 2023-05-15
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RotationTemplate {
	private final String value;
	private final List<AbilityId> abilityIds;

	public static RotationTemplate parse(String scriptPath) {
		var abilityIds = getAbilityIds(scriptPath);

		return new RotationTemplate(scriptPath, abilityIds);
	}

	private static List<AbilityId> getAbilityIds(String scriptPath) {
		var script = ScriptCompiler.compileResource(scriptPath);
		var rotationSection = script.getSection(ROTATION);
		var abilityIds = new LinkedHashSet<AbilityId>();

		for (var command : rotationSection.commands()) {
			switch (command) {
				case CastSequence(var list) -> {
					for (var composableCommand : list) {
						if (composableCommand instanceof CastSpell castSpell) {
							abilityIds.add(castSpell.abilityId());
						}
					}
				}
				case CastSpell castSpell ->
					abilityIds.add(castSpell.abilityId());
				case ScriptCommand.UseItem ignored -> {
					// ignore
				}
			}
		}

		return List.copyOf(abilityIds);
	}

	public Rotation createRotation() {
		return Rotation.create(this);
	}

	@Override
	public String toString() {
		return value;
	}
}

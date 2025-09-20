package wow.estimator.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.script.ScriptCompiler;
import wow.commons.model.spell.AbilityId;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static wow.character.model.script.ScriptCommand.*;
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
						addAbilityId(composableCommand, abilityIds);
					}
				}
				case ComposableCommand composableCommand ->
					addAbilityId(composableCommand, abilityIds);
			}
		}

		return List.copyOf(abilityIds);
	}

	private static void addAbilityId(ComposableCommand command, Set<AbilityId> abilityIds) {
		switch (command) {
			case CastSpell castSpell ->
				abilityIds.add(castSpell.abilityId());
			case CastSpellRank castSpellRank ->
				abilityIds.add(AbilityId.of(castSpellRank.abilityName()));
			case UseItem ignored -> {
				// ignore
			}
		}
	}

	public Rotation createRotation() {
		return Rotation.create(this);
	}

	@Override
	public String toString() {
		return value;
	}
}

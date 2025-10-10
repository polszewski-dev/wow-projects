package wow.character.model.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.constant.AbilityIds;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static wow.character.model.script.ScriptCommand.*;
import static wow.character.model.script.ScriptCommandCondition.*;
import static wow.character.model.script.ScriptCommandTarget.DEFAULT;
import static wow.character.model.script.ScriptCommandTarget.TARGET;
import static wow.character.model.script.ScriptSectionType.ROTATION;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.categorization.ItemSlot.TRINKET_2;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
class ScriptCompilerTest {
	@Test
	void compileResource() {
		var section = getScriptSection("/script/script-example.txt");

		assertThat(section.commands()).isEqualTo(List.of(
				command(DESTRUCTION_POTION),
				command(BLOOD_FURY),
				command(BERSERKING),
				command(TRINKET_1),
				command(TRINKET_2),
				command(AMPLIFY_CURSE, CURSE_OF_DOOM),
				command(CURSE_OF_DOOM),
				command(CORRUPTION),
				command(IMMOLATE),
				command(SHADOW_BOLT),
				command(SHADOWBURN),
				command(SHOOT)
		));
	}

	@Test
	void commandSyntax() {
		var section = getScriptSection("/script/various-commands.txt");

		assertThat(section.commands()).isEqualTo(List.of(
				command(SHADOW_BOLT),
				command(SHADOW_BOLT, 1),
				command(SHADOW_BOLT, 10),
				command(SHADOW_BOLT, TARGET),
				command(SHADOW_BOLT, 1, TARGET),
				command(SHADOW_BOLT, 10, TARGET)
		));
	}

	@Test
	void lineContinuation() {
		var section = getScriptSection("/script/line-continuations.txt");

		assertThat(section.commands()).isEqualTo(List.of(
				command(AMPLIFY_CURSE, CURSE_OF_DOOM),
				command(AMPLIFY_CURSE, CURSE_OF_AGONY, CORRUPTION),
				command(SHADOW_BOLT),
				command(CURSE_OF_DOOM, new And(
						new Not(new TargetHasEffect("Curse of *")),
						new LessThanOrEqual(
								new FullDuration(),
								new RemainingCooldown(AbilityIds.CURSE_OF_DOOM)
						)
				))
		));
	}

	@Test
	void castSequences() {
		var section = getScriptSection("/script/cast-sequences.txt");

		assertThat(section.commands()).isEqualTo(List.of(
				compose(List.of(
						command(TRINKET_1, true),
						command(TRINKET_2, true),
						command(AMPLIFY_CURSE, true),
						command(CURSE_OF_DOOM)
				))
		));
	}

	@Test
	void macros() {
		var sectionBefore = getScriptSection("/script/before-adding-macros.txt");
		var sectionAfter = getScriptSection("/script/after-adding-macros.txt");

		var expectedCommands = List.of(
				command(CURSE_OF_DOOM, new Not(new TargetHasEffect("Curse of *"))),
				command(CURSE_OF_AGONY, new And(
						new Not(new TargetHasEffect("Curse of *")),
						new LessThanOrEqual(
								new FullDuration(),
								new RemainingCooldown(AbilityIds.CURSE_OF_DOOM)
						)
				)),
				command(CURSE_OF_AGONY, new And(
						new Not(new TargetHasEffect("Curse of *")),
						new Not(new CanCastMoreBeforeSimulationEnds(AbilityIds.CURSE_OF_DOOM))
				))
		);

		assertThat(sectionBefore.commands()).isEqualTo(expectedCommands);
		assertThat(sectionAfter.commands()).isEqualTo(expectedCommands);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/wow/character/script/vanilla/priest-holy.txt",
			"/wow/character/script/vanilla/priest-shadow.txt",
			"/wow/character/script/vanilla/warlock-affliction.txt",
			"/wow/character/script/vanilla/warlock-curse-plus-shadow-bolt.txt",
			"/wow/character/script/vanilla/warlock-destro-shadow.txt",
			"/wow/character/script/vanilla/warlock-shadow-bolt-spam.txt",
			"/wow/character/script/tbc/priest-holy.txt",
			"/wow/character/script/tbc/priest-shadow.txt",
			"/wow/character/script/tbc/warlock-affliction.txt",
			"/wow/character/script/tbc/warlock-affliction-without-ua.txt",
			"/wow/character/script/tbc/warlock-curse-plus-shadow-bolt.txt",
			"/wow/character/script/tbc/warlock-destro-shadow.txt",
			"/wow/character/script/tbc/warlock-shadow-bolt-spam.txt"
	})
	void scriptFilesCompileWithoutErrors(String resourcePath) {
		assertThatNoException().isThrownBy(
				() -> ScriptCompiler.compileResource(resourcePath)
		);
	}

	ScriptCommand command(String... abilityNames) {
		var castSpellCommands = Stream.of(abilityNames)
				.map(this::command)
				.toList();

		return compose(castSpellCommands);
	}

	CastSpell command(String abilityName, ScriptCommandTarget target, ScriptCommandCondition condition, boolean optional) {
		return new CastSpell(
				condition,
				AbilityId.of(abilityName),
				target,
				optional
		);
	}

	CastSpell command(String abilityName) {
		return command(abilityName, DEFAULT, EMPTY, false);
	}

	CastSpell command(String abilityName, ScriptCommandTarget target) {
		return command(abilityName, target, EMPTY, false);
	}

	CastSpell command(String abilityName, ScriptCommandCondition condition) {
		return command(abilityName, DEFAULT, condition, false);
	}

	CastSpell command(String abilityName, boolean optional) {
		return command(abilityName, DEFAULT, EMPTY, optional);
	}

	CastSpellRank command(String abilityName, int rank) {
		return command(abilityName, rank, DEFAULT);
	}

	CastSpellRank command(String abilityName, int rank, ScriptCommandTarget target) {
		return new CastSpellRank(
				EMPTY,
				abilityName,
				rank,
				target,
				false
		);
	}

	UseItem command(ItemSlot itemSlot) {
		return command(itemSlot, false);
	}

	UseItem command(ItemSlot itemSlot, boolean optional) {
		return new UseItem(
				EMPTY,
				itemSlot,
				DEFAULT,
				optional
		);
	}

	ScriptSection getScriptSection(String path) {
		var script = ScriptCompiler.compileResource(path);
		return script.getSection(ROTATION);
	}
}
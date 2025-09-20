package wow.character.model.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static wow.character.model.script.ScriptCommand.*;
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
		var script = ScriptCompiler.compileResource("/script/script-example.txt");
		var section = script.getSection(ROTATION);

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
		var script = ScriptCompiler.compileResource("/script/various-commands.txt");
		var section = script.getSection(ROTATION);

		assertThat(section.commands()).isEqualTo(List.of(
				command(SHADOW_BOLT),
				command(SHADOW_BOLT, 1),
				command(SHADOW_BOLT, 10),
				command(SHADOW_BOLT, TARGET),
				command(SHADOW_BOLT, 1, TARGET),
				command(SHADOW_BOLT, 10, TARGET)
		));
	}

	ScriptCommand command(String... abilityNames) {
		var castSpellCommands = Stream.of(abilityNames)
				.map(this::command)
				.toList();

		return compose(castSpellCommands);
	}

	CastSpell command(String abilityName, ScriptCommandTarget target) {
		return new CastSpell(
				List.of(),
				AbilityId.of(abilityName),
				target
		);
	}

	CastSpell command(String abilityName) {
		return command(abilityName, DEFAULT);
	}

	CastSpellRank command(String abilityName, int rank) {
		return command(abilityName, rank, DEFAULT);
	}


	CastSpellRank command(String abilityName, int rank, ScriptCommandTarget target) {
		return new CastSpellRank(
				List.of(),
				abilityName,
				rank,
				target
		);
	}

	UseItem command(ItemSlot itemSlot) {
		return new UseItem(
				List.of(),
				itemSlot,
				DEFAULT
		);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/wow/character/script/vanilla/priest-holy.txt",
			"/wow/character/script/vanilla/priest-shadow.txt",
			"/wow/character/script/vanilla/warlock-destro-shadow.txt",
			"/wow/character/script/tbc/priest-holy.txt",
			"/wow/character/script/tbc/priest-shadow.txt",
			"/wow/character/script/tbc/warlock-destro-shadow.txt",
	})
	void scriptFilesCompileWithoutErrors(String resourcePath) {
		assertThatNoException().isThrownBy(
				() -> ScriptCompiler.compileResource(resourcePath)
		);
	}
}
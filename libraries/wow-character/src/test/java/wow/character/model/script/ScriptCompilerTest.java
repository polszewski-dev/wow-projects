package wow.character.model.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;
import wow.test.commons.AbilityNames;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static wow.character.model.script.ScriptCommand.*;
import static wow.character.model.script.ScriptCommandTarget.DEFAULT;
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
		var script = ScriptCompiler.compileResource("/script/script1.txt");
		var section = script.getSection(ROTATION);

		assertThat(section.commands()).isEqualTo(List.of(
				command(AbilityNames.DESTRUCTION_POTION),
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

	ScriptCommand command(String... abilityNames) {
		var castSpellCommands = Stream.of(abilityNames)
				.map(AbilityId::of)
				.map(this::castSpell)
				.toList();

		return compose(castSpellCommands);
	}

	CastSpell castSpell(AbilityId abilityId) {
		return new CastSpell(
				List.of(),
				abilityId,
				DEFAULT
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
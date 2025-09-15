package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
class SpellbookTest extends WowCharacterSpringTest {
	@Test
	void copy() {
		Spellbook copy = spellbook.copy();

		spellbook.reset();

		assertThat(copy.getAbilityById()).isNotEmpty();
	}

	@Test
	void reset() {
		spellbook.reset();

		assertThat(spellbook.getAbilityById()).isEmpty();
	}

	@Test
	void addAbility() {
		var ability = spellbook.getAbility(SHADOW_BOLT).orElseThrow();

		spellbook.reset();
		spellbook.addAbility(ability);

		assertThat(spellbook.getAbility(ability.getAbilityId()).orElseThrow()).isSameAs(ability);
	}

	@Test
	void addAbilities() {
		var ability1 = spellbook.getAbility(SHADOW_BOLT, 1).orElseThrow();
		var ability2 = spellbook.getAbility(SHADOW_BOLT, 2).orElseThrow();

		spellbook.reset();
		spellbook.addAbilities(List.of(ability1, ability2));

		assertThat(spellbook.getAbility(SHADOW_BOLT, 1).orElseThrow()).isSameAs(ability1);
		assertThat(spellbook.getAbility(SHADOW_BOLT, 2).orElseThrow()).isSameAs(ability2);
	}

	@Test
	void getAbility() {
		var spell = spellbook.getAbility(SHADOW_BOLT).orElseThrow();

		assertThat(spell.getName()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getRank()).isEqualTo(11);
	}

	@Test
	void getAbilityByRank() {
		var spell = spellbook.getAbility(SHADOW_BOLT, 1).orElseThrow();

		assertThat(spell.getName()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getRank()).isEqualTo(1);
	}

	Character character;
	Spellbook spellbook;

	@BeforeEach
	void setup() {
		character = getCharacter();
		spellbook = character.getSpellbook();
	}
}
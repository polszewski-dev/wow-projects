package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.spell.Spell;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
class SpellbookTest extends WowCharacterSpringTest {
	@Test
	void copy() {
		Spellbook copy = spellbook.copy();

		spellbook.reset();

		assertThat(copy.getSpellById()).isNotEmpty();
	}

	@Test
	void reset() {
		spellbook.reset();

		assertThat(spellbook.getSpellById()).isEmpty();
	}

	@Test
	void addSpell() {
		Spell spell = spellbook.getSpell(SHADOW_BOLT).orElseThrow();

		spellbook.reset();
		spellbook.addSpell(spell);

		assertThat(spellbook.getSpell(spell.getSpellId()).orElseThrow()).isSameAs(spell);
	}

	@Test
	void addSpells() {
		Spell spell1 = spellbook.getSpell(SHADOW_BOLT, 1).orElseThrow();
		Spell spell2 = spellbook.getSpell(SHADOW_BOLT, 2).orElseThrow();

		spellbook.reset();
		spellbook.addSpells(List.of(spell1, spell2));

		assertThat(spellbook.getSpell(SHADOW_BOLT, 1).orElseThrow()).isSameAs(spell1);
		assertThat(spellbook.getSpell(SHADOW_BOLT, 2).orElseThrow()).isSameAs(spell2);
	}

	@Test
	void getSpell() {
		Spell spell = spellbook.getSpell(SHADOW_BOLT).orElseThrow();

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getRank()).isEqualTo(11);
	}

	@Test
	void getSpellByRank() {
		Spell spell = spellbook.getSpell(SHADOW_BOLT, 1).orElseThrow();

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
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
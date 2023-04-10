package wow.character.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class SpellServiceTest extends WowCharacterSpringTest {
	@Autowired
	SpellService underTest;

	@Test
	void getSpell() {
		Spell spell = underTest.getSpellHighestRank(SHADOW_BOLT, getCharacter());

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getRank()).isEqualTo(11);
	}

	@Test
	void getAvailableBuffs() {
		List<Buff> buffs = underTest.getBuffs(getCharacter());

		List<String> names = buffs.stream().map(Buff::getName).toList();

		assertThat(names).hasSameElementsAs(List.of(
				"Arcane Brilliance",
				"Prayer of Fortitude",
				"Prayer of Spirit",
				"Gift of the Wild",
				"Greater Blessing of Kings",
				"Fel Armor",
				"Touch of Shadow",
				"Burning Wish",
				"Brilliant Wizard Oil",
				"Superior Wizard Oil",
				"Well Fed (sp)",
				"Flask of Supreme Power",
				"Flask of Pure Death",
				"Moonkin Aura",
				"Wrath of Air Totem",
				"Totem of Wrath",
				"Misery",
				"Shadow Weaving",
				"Improved Scorch",
				"Curse of the Elements",
				"Curse of the Elements (improved)",
				"Drums of Battle",
				"Destruction"
		));
	}
}
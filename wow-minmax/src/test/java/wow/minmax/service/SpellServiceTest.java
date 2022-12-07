package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class SpellServiceTest extends ServiceTest {
	@Autowired
	SpellService underTest;

	@Test
	void getSpell() {
		Spell spell = underTest.getSpell(SpellId.SHADOW_BOLT, 70);

		assertThat(spell.getSpellId()).isEqualTo(SpellId.SHADOW_BOLT);
		assertThat(spell.getRank()).isEqualTo(11);
	}

	@Test
	void getAvailableBuffs() {
		List<Buff> buffs = underTest.getAvailableBuffs(playerProfile);

		List<String> names = buffs.stream().map(Buff::getName).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Arcane Brilliance",
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
				"Destruction",
				"Blood Fury"
		));
	}
}
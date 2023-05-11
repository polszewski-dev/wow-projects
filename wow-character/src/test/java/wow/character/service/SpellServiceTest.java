package wow.character.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.buffs.Buff;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class SpellServiceTest extends WowCharacterSpringTest {
	@Autowired
	SpellService underTest;

	@Test
	void getAvailableSpells() {
		var spells = underTest.getAvailableSpells(getCharacter());

		assertThat(spells.stream().map(spell -> spell.getSpellId() + "#" + spell.getRank()).toList()).hasSameElementsAs(List.of(
				"Corruption#1",
				"Corruption#2",
				"Corruption#3",
				"Corruption#4",
				"Corruption#5",
				"Corruption#6",
				"Corruption#8",
				"Curse of Agony#1",
				"Curse of Agony#2",
				"Curse of Agony#3",
				"Curse of Agony#4",
				"Curse of Agony#5",
				"Curse of Agony#6",
				"Curse of Agony#7",
				"Curse of Doom#1",
				"Curse of Doom#2",
				"Death Coil#1",
				"Death Coil#2",
				"Death Coil#3",
				"Death Coil#4",
				"Drain Life#1",
				"Drain Life#2",
				"Drain Life#3",
				"Drain Life#4",
				"Drain Life#5",
				"Drain Life#6",
				"Drain Life#7",
				"Drain Life#8",
				"Life Tap#1",
				"Life Tap#2",
				"Life Tap#3",
				"Life Tap#4",
				"Life Tap#5",
				"Life Tap#6",
				"Life Tap#7",
				"Seed of Corruption#1",
				"Seed of Corruption (direct)#1",
				"Hellfire#1",
				"Hellfire#2",
				"Hellfire#3",
				"Hellfire#4",
				"Immolate#1",
				"Immolate#2",
				"Immolate#3",
				"Immolate#4",
				"Immolate#5",
				"Immolate#6",
				"Immolate#7",
				"Immolate#8",
				"Immolate#9",
				"Incinerate#1",
				"Incinerate#2",
				"Rain of Fire#1",
				"Rain of Fire#2",
				"Rain of Fire#3",
				"Rain of Fire#4",
				"Rain of Fire#5",
				"Searing Pain#1",
				"Searing Pain#2",
				"Searing Pain#3",
				"Searing Pain#4",
				"Searing Pain#5",
				"Searing Pain#6",
				"Searing Pain#7",
				"Searing Pain#8",
				"Shadow Bolt#1",
				"Shadow Bolt#2",
				"Shadow Bolt#3",
				"Shadow Bolt#4",
				"Shadow Bolt#5",
				"Shadow Bolt#6",
				"Shadow Bolt#7",
				"Shadow Bolt#8",
				"Shadow Bolt#9",
				"Shadow Bolt#10",
				"Shadow Bolt#11",
				"Shadowburn#1",
				"Shadowburn#2",
				"Shadowburn#3",
				"Shadowburn#4",
				"Shadowburn#5",
				"Shadowburn#6",
				"Shadowburn#7",
				"Shadowburn#8"
		));
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
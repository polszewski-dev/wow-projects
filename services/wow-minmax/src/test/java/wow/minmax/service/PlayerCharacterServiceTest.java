package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.character.ProfIdSpecId;
import wow.commons.model.pve.Faction;
import wow.minmax.model.config.ScriptInfo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.profession.ProfessionId.*;
import static wow.commons.model.profession.ProfessionSpecializationId.*;
import static wow.commons.model.pve.FactionExclusionGroupId.SCRYERS_ALDOR;
import static wow.test.commons.ExclusiveFactionNames.ALDOR;
import static wow.test.commons.ExclusiveFactionNames.SCRYERS;

/**
 * User: POlszewski
 * Date: 2025-10-02
 */
class PlayerCharacterServiceTest extends ServiceTest {
	@Test
	void getAvailableProfessions() {
		var availableProfessions = underTest.getAvailableProfessions(CHARACTER_KEY);

		var list = availableProfessions.stream()
				.map(x -> new ProfIdSpecId(x.professionId(), x.specializationId()))
				.toList();

		assertThat(list).hasSameElementsAs(List.of(
				new ProfIdSpecId(ALCHEMY),
				new ProfIdSpecId(ALCHEMY, POTION_MASTER),
				new ProfIdSpecId(ALCHEMY, ELIXIR_MASTER),
				new ProfIdSpecId(ALCHEMY, TRANSMUTATION_MASTER),
				new ProfIdSpecId(ENCHANTING),
				new ProfIdSpecId(JEWELCRAFTING),
				new ProfIdSpecId(TAILORING),
				new ProfIdSpecId(TAILORING, SPELLFIRE_TAILORING),
				new ProfIdSpecId(TAILORING, SHADOWEAVE_TAILORING),
				new ProfIdSpecId(TAILORING, MOONCLOTH_TAILORING),
				new ProfIdSpecId(LEATHERWORKING),
				new ProfIdSpecId(LEATHERWORKING, DRAGONSCALE_LEATHERWORKING),
				new ProfIdSpecId(LEATHERWORKING, ELEMENTAL_LEATHERWORKING),
				new ProfIdSpecId(LEATHERWORKING, TRIBAL_LEATHERWORKING),
				new ProfIdSpecId(BLACKSMITHING),
				new ProfIdSpecId(BLACKSMITHING, ARMORSMITH),
				new ProfIdSpecId(BLACKSMITHING, WEAPONSMITH),
				new ProfIdSpecId(BLACKSMITHING, MASTER_SWORDSMITH),
				new ProfIdSpecId(BLACKSMITHING, MASTER_AXESMITH),
				new ProfIdSpecId(BLACKSMITHING, MASTER_HAMMERSMITH),
				new ProfIdSpecId(ENGINEERING),
				new ProfIdSpecId(ENGINEERING, GOBLIN_ENGINEER),
				new ProfIdSpecId(ENGINEERING, GNOMISH_ENGINEER),
				new ProfIdSpecId(HERBALISM),
				new ProfIdSpecId(MINING),
				new ProfIdSpecId(SKINNING)
		));
	}

	@Test
	void changeProfession() {
		assertThat(character.hasProfession(ENCHANTING)).isTrue();
		assertThat(character.hasProfession(TAILORING)).isTrue();

		assertThat(character.hasProfession(ALCHEMY)).isFalse();
		assertThat(character.hasProfessionSpecialization(TRANSMUTATION_MASTER)).isFalse();

		var player = underTest.changeProfession(CHARACTER_KEY, 1, new ProfIdSpecId(ALCHEMY, TRANSMUTATION_MASTER));

		assertThat(player.hasProfession(ALCHEMY)).isTrue();
		assertThat(player.hasProfessionSpecialization(TRANSMUTATION_MASTER)).isTrue();

		assertThat(player.hasProfession(ENCHANTING)).isTrue();
	}

	@Test
	void getAvailableExclusiveFactions() {
		var availableExclusiveFactions = underTest.getAvailableExclusiveFactions(CHARACTER_KEY);

		assertThat(availableExclusiveFactions).hasSize(1);

		var group = availableExclusiveFactions.getFirst();

		assertThat(group.groupId()).isEqualTo(SCRYERS_ALDOR);
		assertThat(group.selectedFaction().getName()).isEqualTo(SCRYERS);
		assertThat(group.availableFactions().stream().map(Faction::getName)).hasSameElementsAs(List.of(
				ALDOR, SCRYERS
		));
	}

	@Test
	void changeExclusiveFaction() {
		assertThat(character.hasExclusiveFaction(ALDOR)).isFalse();
		assertThat(character.hasExclusiveFaction(SCRYERS)).isTrue();

		var player = underTest.changeExclusiveFaction(CHARACTER_KEY, ALDOR);

		assertThat(player.hasExclusiveFaction(ALDOR)).isTrue();
		assertThat(player.hasExclusiveFaction(SCRYERS)).isFalse();
	}

	@Test
	void change() {
		assertThat(character.getTalentLink()).isEqualTo("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025");

		var player = underTest.changeTalents(CHARACTER_KEY, "https://www.wowhead.com/tbc/talent-calc/warlock/55022000102351055103--50500051220001");

		assertThat(player.getTalentLink()).isEqualTo("https://www.wowhead.com/tbc/talent-calc/warlock/55022000102351055103--50500051220001");
	}

	@Test
	void getAvailableScripts() {
		var availableScripts = underTest.getAvailableScripts(CHARACTER_KEY);

		var list = availableScripts.stream()
				.map(ScriptInfo::path)
				.toList();

		assertThat(list).hasSameElementsAs(List.of(
				"warlock-destro-shadow",
				"warlock-curse-plus-shadow-bolt",
				"warlock-shadow-bolt-spam",
				"warlock-affliction",
				"warlock-affliction-without-ua"
		));
	}

	@Test
	void changeScript() {
		var newScript = "warlock-shadow-bolt-spam";

		assertThat(character.getBuild().getScript()).isEqualTo("warlock-destro-shadow");

		var player = underTest.changeScript(CHARACTER_KEY, newScript);

		assertThat(player.getBuild().getScript()).isEqualTo(newScript);
	}

	@Autowired
	PlayerCharacterService underTest;
}
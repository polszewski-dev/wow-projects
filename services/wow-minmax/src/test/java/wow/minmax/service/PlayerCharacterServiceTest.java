package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.character.ProfIdSpecId;
import wow.minmax.model.config.ScriptInfo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.profession.ProfessionId.*;
import static wow.commons.model.profession.ProfessionSpecializationId.*;

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
	void getAvailableScripts() {
		var availableScripts = underTest.getAvailableScripts(CHARACTER_KEY);

		var list = availableScripts.stream()
				.map(ScriptInfo::path)
				.toList();

		assertThat(list).hasSameElementsAs(List.of(
				"warlock-destro-shadow.txt",
				"warlock-shadow-bolt-spam.txt"
		));
	}

	@Test
	void changeScript() {
		var newScript = "warlock-shadow-bolt-spam.txt";

		assertThat(character.getBuild().getScript()).isEqualTo("warlock-destro-shadow.txt");

		var player = underTest.changeScript(CHARACTER_KEY, newScript);

		assertThat(player.getBuild().getScript()).isEqualTo(newScript);
	}

	@Autowired
	PlayerCharacterService underTest;
}
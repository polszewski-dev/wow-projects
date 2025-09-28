package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.commons.model.profession.ProfessionId.*;
import static wow.commons.model.profession.ProfessionSpecializationId.SHADOWEAVE_TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.WEAPONSMITH;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2023-08-02
 */
class CharacterProfessionsTest extends WowCharacterSpringTest {
	@Test
	void noProfessions() {
		assertThat(professions.getList()).isEqualTo(
				Arrays.asList(null, null)
		);
		assertThat(professions.hasProfession(TAILORING)).isFalse();
		assertThat(professions.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isFalse();
	}

	@Test
	void oneProfession() {
		professions.add(TAILORING, SHADOWEAVE_TAILORING, 375);

		assertThat(professions.getList(ProfIdSpecIdLevel::new)).isEqualTo(Arrays.asList(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 375),
				null
		));

		assertThat(professions.hasProfession(TAILORING)).isTrue();
		assertThat(professions.hasProfession(TAILORING, 375)).isTrue();
		assertThat(professions.hasProfession(TAILORING, 300)).isTrue();
		assertThat(professions.hasProfession(TAILORING, 400)).isFalse();
		assertThat(professions.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isTrue();
	}

	@Test
	void twoProfessions() {
		professions.add(TAILORING, SHADOWEAVE_TAILORING, 375);
		professions.add(ENCHANTING, 300);

		assertThat(professions.getList(ProfIdSpecIdLevel::new)).isEqualTo(List.of(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 375),
				new ProfIdSpecIdLevel(ENCHANTING, 300)
		));
	}

	@Test
	void addMaxLevel() {
		professions.addMaxLevel(TAILORING, SHADOWEAVE_TAILORING);
		professions.addMaxLevel(ENCHANTING);

		assertThat(professions.getList(ProfIdSpecIdLevel::new)).isEqualTo(List.of(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 375),
				new ProfIdSpecIdLevel(ENCHANTING, 375)
		));
	}

	@Test
	void set() {
		var expected = List.of(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 375),
				new ProfIdSpecIdLevel(ENCHANTING, 300)
		);

		professions.set(expected);

		assertThat(professions.getList(ProfIdSpecIdLevel::new)).isEqualTo(expected);
	}

	@Test
	void setMaxLevels() {
		var expected = List.of(
				new ProfIdSpecId(TAILORING, SHADOWEAVE_TAILORING),
				new ProfIdSpecId(ENCHANTING)
		);

		professions.setMaxLevels(expected);

		assertThat(professions.getList(ProfIdSpecIdLevel::new)).isEqualTo(List.of(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 375),
				new ProfIdSpecIdLevel(ENCHANTING, 375)
		));
	}

	@Test
	void max2Professions() {
		professions.add(TAILORING, SHADOWEAVE_TAILORING, 375);
		professions.add(ENCHANTING, 300);

		assertThatThrownBy(() -> professions.add(MINING, 300)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void canAddAgainAfterReset() {
		professions.add(TAILORING, SHADOWEAVE_TAILORING, 375);
		professions.add(ENCHANTING, 300);

		professions.reset();

		professions.add(BLACKSMITHING, WEAPONSMITH, 1);
		professions.add(JEWELCRAFTING, 2);

		assertThat(professions.getList(ProfIdSpecIdLevel::new)).isEqualTo(List.of(
				new ProfIdSpecIdLevel(BLACKSMITHING, WEAPONSMITH, 1),
				new ProfIdSpecIdLevel(JEWELCRAFTING, 2)
		));
	}

	@Test
	void canSetBothToNull() {
		professions.set(Arrays.asList(null, null));

		assertThat(professions.getList()).isEqualTo(
				Arrays.asList(null, null)
		);
	}

	@Test
	void canSetBothToNull_maxLevel() {
		professions.set(Arrays.asList(null, null));

		assertThat(professions.getList()).isEqualTo(
				Arrays.asList(null, null)
		);
	}

	@Test
	void canNotAddDuplicateProfession() {
		professions.add(TAILORING, 375);

		assertThatThrownBy(() -> professions.add(TAILORING, SHADOWEAVE_TAILORING, 375)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void canNotSetDuplicateProfessions() {
		var duplicates = List.of(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 1),
				new ProfIdSpecIdLevel(TAILORING, 1)
		);

		assertThatThrownBy(() -> professions.set(duplicates)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void canNotSetDuplicateProfessions_maxLevel() {
		var duplicates = List.of(
				new ProfIdSpecId(TAILORING, SHADOWEAVE_TAILORING),
				new ProfIdSpecId(TAILORING)
		);

		assertThatThrownBy(() -> professions.setMaxLevels(duplicates)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void reset() {
		professions.add(TAILORING, SHADOWEAVE_TAILORING, 375);
		professions.add(ENCHANTING, 300);

		assertThat(professions.getList()).hasSize(2);

		professions.reset();

		assertThat(professions.getList()).isEqualTo(Arrays.asList(null, null));
	}

	@Test
	void copy() {
		professions.add(TAILORING, SHADOWEAVE_TAILORING, 375);
		professions.add(ENCHANTING, 300);

		var copy = professions.copy();

		professions.reset();

		assertThat(copy.getList(ProfIdSpecIdLevel::new)).isEqualTo(List.of(
				new ProfIdSpecIdLevel(TAILORING, SHADOWEAVE_TAILORING, 375),
				new ProfIdSpecIdLevel(ENCHANTING, 300)
		));

		assertThat(professions.getList()).isEqualTo(Arrays.asList(null, null));
	}

	CharacterProfessions professions;

	@BeforeEach
	void setup() {
		var phase = phaseRepository.getPhase(TBC_P5).orElseThrow();
		var availableProfessions = phase.getGameVersion().getProfessions();

		professions = new CharacterProfessions(availableProfessions, phase, phase.getMaxLevel());
	}
}
package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.attributes.condition.AttributeCondition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.commons.model.professions.ProfessionId.*;
import static wow.commons.model.professions.ProfessionSpecializationId.SHADOWEAVE_TAILORING;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2023-08-02
 */
class CharacterProfessionsTest extends WowCharacterSpringTest {
	@Test
	void noProfessions() {
		CharacterProfessions professions = new CharacterProfessions();

		assertThat(professions.getList()).isEmpty();
		assertThat(professions.hasProfession(TAILORING)).isFalse();
		assertThat(professions.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isFalse();
		assertThat(professions.hasProfession(ENCHANTING)).isFalse();
		assertThat(professions.getConditions()).isEmpty();
	}

	@Test
	void twoProfessions() {
		CharacterProfessions professions = new CharacterProfessions();

		professions.addProfession(tailoring, tailoringSpecialization, 375);
		professions.addProfession(enchanting, null, 300);

		assertThat(professions.getList()).hasSize(2);
		assertThat(professions.getList().get(0).getProfessionId()).isEqualTo(TAILORING);
		assertThat(professions.getList().get(0).getSpecializationId()).isEqualTo(SHADOWEAVE_TAILORING);
		assertThat(professions.getList().get(0).getLevel()).isEqualTo(375);
		assertThat(professions.getList().get(1).getProfessionId()).isEqualTo(ENCHANTING);
		assertThat(professions.getList().get(1).getSpecializationId()).isNull();
		assertThat(professions.getList().get(1).getLevel()).isEqualTo(300);

		assertThat(professions.hasProfession(TAILORING)).isTrue();
		assertThat(professions.hasProfession(TAILORING, 300)).isTrue();
		assertThat(professions.hasProfession(TAILORING, 375)).isTrue();
		assertThat(professions.hasProfession(TAILORING, 400)).isFalse();
		assertThat(professions.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isTrue();

		assertThat(professions.hasProfession(ENCHANTING)).isTrue();
		assertThat(professions.hasProfession(ENCHANTING, 300)).isTrue();
		assertThat(professions.hasProfession(ENCHANTING, 375)).isFalse();
		assertThat(professions.hasProfession(ENCHANTING, 400)).isFalse();

		assertThat(professions.getConditions()).hasSameElementsAs(List.of(
				AttributeCondition.of(TAILORING),
				AttributeCondition.of(SHADOWEAVE_TAILORING),
				AttributeCondition.of(ENCHANTING)
		));
	}

	@Test
	void reset() {
		CharacterProfessions professions = new CharacterProfessions();

		professions.addProfession(tailoring, tailoringSpecialization, 375);
		professions.addProfession(enchanting, null, 300);

		assertThat(professions.getList()).hasSize(2);

		professions.reset();

		assertThat(professions.getList()).isEmpty();
		assertThat(professions.hasProfession(TAILORING)).isFalse();
		assertThat(professions.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isFalse();
		assertThat(professions.hasProfession(ENCHANTING)).isFalse();
		assertThat(professions.getConditions()).isEmpty();
	}

	@Test
	void setProfessions() {
		CharacterProfessions professions = new CharacterProfessions();

		professions.addProfession(tailoring, tailoringSpecialization, 375);
		professions.addProfession(enchanting, null, 300);

		professions.setProfessions(List.of(
				new CharacterProfession(herbalism, null, 200),
				new CharacterProfession(mining, null, 250)
		));

		assertThat(professions.getList()).hasSize(2);
		assertThat(professions.getList().get(0).getProfessionId()).isEqualTo(HERBALISM);
		assertThat(professions.getList().get(0).getSpecializationId()).isNull();
		assertThat(professions.getList().get(0).getLevel()).isEqualTo(200);
		assertThat(professions.getList().get(1).getProfessionId()).isEqualTo(MINING);
		assertThat(professions.getList().get(1).getSpecializationId()).isNull();
		assertThat(professions.getList().get(1).getLevel()).isEqualTo(250);
	}

	@Test
	void copy() {
		CharacterProfessions professions = new CharacterProfessions();

		professions.addProfession(tailoring, tailoringSpecialization, 375);
		professions.addProfession(enchanting, null, 300);

		CharacterProfessions copy = professions.copy();

		professions.reset();

		assertThat(copy.getList()).hasSize(2);
		assertThat(copy.getList().get(0).getProfessionId()).isEqualTo(TAILORING);
		assertThat(copy.getList().get(0).getSpecializationId()).isEqualTo(SHADOWEAVE_TAILORING);
		assertThat(copy.getList().get(0).getLevel()).isEqualTo(375);
		assertThat(copy.getList().get(1).getProfessionId()).isEqualTo(ENCHANTING);
		assertThat(copy.getList().get(1).getSpecializationId()).isNull();
		assertThat(copy.getList().get(1).getLevel()).isEqualTo(300);

		assertThat(professions.getList()).isEmpty();
	}

	@Test
	void max2Professions() {
		CharacterProfessions professions = new CharacterProfessions();

		professions.addProfession(tailoring, tailoringSpecialization, 375);
		professions.addProfession(enchanting, null, 300);

		assertThatThrownBy(() -> professions.addProfession(mining, null, 300)).isInstanceOf(IllegalArgumentException.class);

		var incorrect = List.of(
				new CharacterProfession(herbalism, null, 200),
				new CharacterProfession(mining, null, 250),
				new CharacterProfession(enchanting, null, 300)
		);

		assertThatThrownBy(() -> professions.setProfessions(incorrect)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void noDuplicateProfessions() {
		CharacterProfessions professions = new CharacterProfessions();

		professions.addProfession(tailoring, null, 375);

		assertThatThrownBy(() -> professions.addProfession(tailoring, tailoringSpecialization, 375)).isInstanceOf(IllegalArgumentException.class);

		var incorrect = List.of(
				new CharacterProfession(tailoring, null, 300),
				new CharacterProfession(tailoring, tailoringSpecialization, 375)
		);

		assertThatThrownBy(() -> professions.setProfessions(incorrect)).isInstanceOf(IllegalArgumentException.class);
	}

	GameVersion gameVersion;
	Profession tailoring;
	ProfessionSpecialization tailoringSpecialization;
	Profession enchanting;
	Profession herbalism;
	Profession mining;

	@BeforeEach
	void setup() {
		gameVersion = characterRepository.getGameVersion(TBC).orElseThrow();

		tailoring = gameVersion.getProfession(TAILORING);
		tailoringSpecialization = tailoring.getSpecialization(SHADOWEAVE_TAILORING);

		enchanting = gameVersion.getProfession(ENCHANTING);

		herbalism = gameVersion.getProfession(HERBALISM);
		mining = gameVersion.getProfession(MINING);
	}
}
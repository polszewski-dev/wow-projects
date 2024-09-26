package wow.commons.repository.character;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.profession.ProfessionProficiencyId.MASTER;
import static wow.commons.model.profession.ProfessionSpecializationId.*;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.GameVersionId.VANILLA;

/**
 * User: POlszewski
 * Date: 28.09.2024
 */
class ProfessionRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	ProfessionRepository underTest;

	@Test
	void professionsAreCorrect() {
		var tailoring = underTest.getProfession(TAILORING, TBC).orElseThrow();

		assertThat(tailoring.getProfessionId()).isEqualTo(TAILORING);
		assertThat(tailoring.getName()).isEqualTo("Tailoring");
		assertThat(tailoring.getIcon()).isEqualTo("trade_tailoring");

		assertThat(tailoring.getSpecializations().stream().map(ProfessionSpecialization::getSpecializationId).toList()).hasSameElementsAs(List.of(
				SPELLFIRE_TAILORING,
				SHADOWEAVE_TAILORING,
				MOONCLOTH_TAILORING
		));
	}

	@Test
	void professionSpecsAreCorrect() {
		var profession = underTest.getProfession(TAILORING, TBC).orElseThrow();
		var specialization = profession.getSpecialization(SPELLFIRE_TAILORING).orElseThrow();

		assertThat(specialization.getSpecializationId()).isEqualTo(SPELLFIRE_TAILORING);
		assertThat(specialization.getName()).isEqualTo("Spellfire Tailoring");
		assertThat(specialization.getIcon()).isEqualTo("classic_spell_holy_blessingofprotection");

		assertThat(specialization.getProfession().getProfessionId()).isEqualTo(TAILORING);
	}

	@Test
	void noSpecForVanillaTailoring() {
		var profession = underTest.getProfession(TAILORING, VANILLA).orElseThrow();

		assertThat(profession.getSpecializations()).isEmpty();
	}

	@Test
	void professionProficienciesAreCorrect() {
		var proficiency = underTest.getProficiency(MASTER, TBC).orElseThrow();

		assertThat(proficiency.getProficiencyId()).isEqualTo(MASTER);
		assertThat(proficiency.getName()).isEqualTo("Master");
		assertThat(proficiency.getMaxSkilll()).isEqualTo(375);
		assertThat(proficiency.getReqLevel(ProfessionType.CRAFTING)).isEqualTo(50);
		assertThat(proficiency.getReqLevel(ProfessionType.GATHERING)).isEqualTo(40);
		assertThat(proficiency.getReqLevel(ProfessionType.SECONDARY)).isEqualTo(50);
	}
}
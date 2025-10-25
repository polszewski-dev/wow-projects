package wow.commons.repository.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-10-25
 */
class ItemSetRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	ItemSetRepository itemSetRepository;

	@Test
	void getItemSet() {
		var itemSet = itemSetRepository.getItemSet("Spellstrike Infusion", TBC_P5).orElseThrow();

		assertThat(itemSet.getName()).isEqualTo("Spellstrike Infusion");
		assertThat(itemSet.getItemSetBonuses()).hasSize(1);
		assertThat(itemSet.getItemSetBonuses().getFirst().numPieces()).isEqualTo(2);
		assertThat(itemSet.getItemSetBonuses().getFirst().bonusEffect().getDescription().tooltip()).isEqualTo(
				"Gives a chance when your harmful spells land to increase the damage of your spells and effects by 92 for 10 sec. (Proc chance: 5%)"
		);
		assertThat(itemSet.getRequiredProfession()).isEqualTo(TAILORING);
		assertThat(itemSet.getPieces()).hasSameElementsAs(List.of(
				"Spellstrike Hood",
				"Spellstrike Pants"
		));
	}
}
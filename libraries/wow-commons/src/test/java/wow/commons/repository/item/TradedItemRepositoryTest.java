package wow.commons.repository.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.config.TimeRestriction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.Binding.BINDS_ON_PICK_UP;
import static wow.commons.model.categorization.ItemRarity.EPIC;
import static wow.commons.model.categorization.ItemType.TOKEN;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.pve.PhaseId.TBC_P3;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class TradedItemRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	TradedItemRepository tradedItemRepository;

	@Test
	void token() {
		var tradedItem = tradedItemRepository.getTradedItem(31101, TBC_P5).orElseThrow();

		assertThat(tradedItem.getId()).isEqualTo(31101);
		assertThat(tradedItem.getName()).isEqualTo("Pauldrons of the Forgotten Conqueror");
		assertThat(tradedItem.getItemType()).isEqualTo(TOKEN);
		assertThat(tradedItem.getRarity()).isEqualTo(EPIC);
		assertThat(tradedItem.getBinding()).isEqualTo(BINDS_ON_PICK_UP);
		assertThat(tradedItem.isUnique()).isFalse();
		assertThat(tradedItem.getItemLevel()).isEqualTo(70);
		assertThat(tradedItem.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC_P3));
		assertThat(tradedItem.getCharacterRestriction().level()).isEqualTo(70);
		assertThat(tradedItem.getCharacterRestriction().characterClassIds()).hasSameElementsAs(List.of(PALADIN, PRIEST, WARLOCK));
	}
}

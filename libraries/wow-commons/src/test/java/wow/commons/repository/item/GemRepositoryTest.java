package wow.commons.repository.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.ItemSource;
import wow.commons.model.item.MetaEnabler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.categorization.Binding.NO_BINDING;
import static wow.commons.model.categorization.ItemRarity.EPIC;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class GemRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	GemRepository gemRepository;

	/*
		Reckless Pyrestone	Phase 3

		Item Level 100
		+5 Spell Haste Rating and +6 Spell Damage
		"Matches a Red or Yellow Socket."
		Sell Price: 6
	 */

	@Test
	void basicGemInfo() {
		var gem = gemRepository.getGem(35760, TBC_P5).orElseThrow();

		assertThat(gem.getId()).isEqualTo(35760);
		assertThat(gem.getName()).isEqualTo("Reckless Pyrestone");
		assertThat(gem.getRarity()).isEqualTo(EPIC);
		assertThat(gem.getBinding()).isEqualTo(NO_BINDING);
		assertThat(gem.isUnique()).isFalse();
		assertThat(gem.getItemLevel()).isEqualTo(100);
		assertThat(gem.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC_P5));
		assertThat(gem.getCharacterRestriction().level()).isNull();
		assertThat(gem.getIcon()).isEqualTo("inv_jewelcrafting_pyrestone_02");
	}

	@Test
	void normalGemStats() {
		var gem = gemRepository.getGem(35760, TBC_P5).orElseThrow();
		var source = new ItemSource(gem);

		assertThat(gem.getColor()).isEqualTo(GemColor.ORANGE);

		assertThat(gem.getEffects()).hasSize(2);

		assertEffect(gem.getEffects().get(0), HASTE_RATING, 5, MiscCondition.SPELL, "+5 Spell Haste Rating", source);
		assertEffect(gem.getEffects().get(1), POWER, 6, MiscCondition.SPELL_DAMAGE, "+6 Spell Damage", source);
	}

	/*
		Chaotic Skyfire Diamond	Phase 1

		Item Level 70
		+12 Spell Critical & 3% Increased Critical Damage
		Requires at least 2 Blue Gems
		"Only fits in a meta gem slot."
		Sell Price: 3
	 */

	@Test
	void metaGemStats() {
		var gem = gemRepository.getGem(34220, TBC_P5).orElseThrow();
		var source = new ItemSource(gem);

		assertThat(gem.getColor()).isEqualTo(GemColor.META);
		assertThat(gem.getMetaEnablers()).hasSameElementsAs(List.of(MetaEnabler.AT_LEAST_2_BLUES));

		assertThat(gem.getEffects()).hasSize(2);
		assertEffect(gem.getEffects().get(0), CRIT_RATING, 12, MiscCondition.SPELL, "+12 Spell Critical", source);
		assertEffect(gem.getEffects().get(1), CRIT_DAMAGE_PCT, 3, "3% Increased Critical Damage", source);
	}
}

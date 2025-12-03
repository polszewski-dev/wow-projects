package wow.commons.repository.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.constant.AttributeConditions;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.GroupCooldownId;
import wow.commons.model.spell.SpellTargets;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.DESTRUCTION_POTION;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
class ConsumableRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	ConsumableRepository consumableRepository;

	@Test
	void getConsumableById() {
		var consumableId = ConsumableId.of(22839);

		var consumable = consumableRepository.getConsumable(consumableId, PhaseId.TBC_P5).orElseThrow();

		assertThat(consumable.getId()).isEqualTo(consumableId);
		assertThat(consumable.getName()).isEqualTo("Destruction Potion");
		assertThat(consumable.getRequiredLevel()).isEqualTo(60);
		assertThat(consumable.getTooltip()).isEqualTo("Use: Increases spell critical chance by 2% and spell damage by 120 for 15 sec. (2 Min Cooldown)");

		assertThat(consumable.getActivatedAbility().getAbilityId()).isEqualTo(DESTRUCTION_POTION);
		assertThat(consumable.getActivatedAbility().getCooldown()).isEqualTo(Duration.seconds(120));
		assertThat(consumable.getActivatedAbility().getGroupCooldownId()).isEqualTo(GroupCooldownId.POTION);

		assertEffectApplication(consumable.getActivatedAbility(), SpellTargets.SELF, 15, 1, 1, 1);
		assertEffect(
				consumable.getActivatedAbility().getApplyEffectCommands().getFirst().effect(),
				Attributes.of(
						Attribute.of(AttributeId.CRIT_PCT, 2, AttributeConditions.SPELL),
						Attribute.of(AttributeId.POWER, 120, AttributeConditions.SPELL_DAMAGE)
				),
				"Spell Critical increased by 2% and spell damage increased by 120.",
				null
		);
	}

	@Test
	void getConsumableByName() {
		var consumable = consumableRepository.getConsumable("Destruction Potion", PhaseId.TBC_P5).orElseThrow();

		assertId(consumable, 22839);
		assertThat(consumable.getName()).isEqualTo("Destruction Potion");
	}
}
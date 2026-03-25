package wow.commons.repository.spell;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffExclusionGroup;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AttributeConditions.SPELL;
import static wow.commons.constant.AttributeConditions.SPELL_DAMAGE;
import static wow.commons.model.attribute.AttributeId.CRIT_RATING;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.buff.BuffCategory.CONSUME;
import static wow.commons.model.buff.BuffType.OIL;
import static wow.commons.model.categorization.PveRole.CASTER_DPS;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.test.commons.BuffNames.BRILLIANT_WIZARD_OIL;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class BuffRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	BuffRepository buffRepository;

	@Test
	void buff_attributes_are_correct() {
		var buff = buffRepository.getBuff(BRILLIANT_WIZARD_OIL, TBC_P5).getFirst();

		assertThat(buff.getName()).isEqualTo(BRILLIANT_WIZARD_OIL);
		assertThat(buff.getRequiredLevel()).isEqualTo(55);
		assertThat(buff.getType()).isEqualTo(OIL);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.OIL);
		assertThat(buff.getSourceSpell()).isNull();
		assertThat(buff.getPveRoles()).hasSameElementsAs(Set.of(CASTER_DPS));
		assertThat(buff.getCategories()).hasSameElementsAs(Set.of(CONSUME));

		assertModifier(buff.getEffect(), List.of(
				Attribute.of(POWER, 36, SPELL_DAMAGE),
				Attribute.of(CRIT_RATING, 14, SPELL)
		));
	}

	@Test
	void available_buffs_are_correct() {
		var buffNames = buffRepository.getAvailableBuffs(TBC_P5, buff -> true).stream()
				.map(Buff::getName)
				.toList();

		assertThat(buffNames).hasSameElementsAs(List.of(
				"Brilliant Wizard Oil",
				"Superior Wizard Oil",
				"Runn Tum Tuber",
				"Well Fed (sp)",
				"Flask of Supreme Power",
				"Flask of Pure Death",
				"Flask of Blinding Light",
				"Spirit of Zanza",
				"Greater Arcane Elixir",
				"Elixir of Shadow Power",
				"Drums of Battle",
				"Rallying Cry of the Dragonslayer",
				"Spirit of Zandalar",
				"Warchief's Blessing",
				"Sayge's Dark Fortune of Damage",
				"Mol'dar's Moxie",
				"Slip'kik's Savvy",
				"Songflower Serenade"
		));
	}
}

package wow.commons.repository.spell;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.condition.ConditionOperator;
import wow.commons.model.buff.BuffExclusionGroup;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffType;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.DAMAGE_TAKEN_PCT;
import static wow.commons.model.attribute.AttributeId.INTELLECT;
import static wow.commons.model.buff.BuffCategory.RAID_BUFF;
import static wow.commons.model.categorization.PveRole.CASTER_DPS;
import static wow.commons.model.spell.SpellSchool.*;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class BuffRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	BuffRepository buffRepository;

	@Test
	void buffIsCorrect() {
		var buff = buffRepository.getBuff(BuffId.CURSE_OF_THE_ELEMENTS, 4, PhaseId.TBC_P5).orElseThrow();

		assertThat(buff.getBuffId()).isEqualTo(BuffId.CURSE_OF_THE_ELEMENTS);
		assertThat(buff.getRank()).isEqualTo(4);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getRequiredLevel()).isNull();
		assertThat(buff.getType()).isEqualTo(BuffType.DEBUFF);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.COE);
		assertThat(buff.getSourceSpell()).isNull();
		assertThat(buff.getPveRoles()).hasSameElementsAs(Set.of(CASTER_DPS));
		assertThat(buff.getCategories()).hasSameElementsAs(Set.of(RAID_BUFF));

		assertModifier(buff.getEffect(), List.of(
				Attribute.of(DAMAGE_TAKEN_PCT, 10, ConditionOperator.comma(
						AttributeCondition.of(SHADOW),
						AttributeCondition.of(FIRE),
						AttributeCondition.of(FROST),
						AttributeCondition.of(ARCANE)
				))
		));
	}

	@Test
	void bufByNamefIsCorrect() {
		var buffNames = buffRepository.getAvailableBuffs(PhaseId.TBC_P5).stream()
				.map(buff -> buff.getName() + "#" + buff.getRank())
				.toList();

		assertThat(buffNames).hasSameElementsAs(List.of(
				"Arcane Brilliance#1",
				"Arcane Brilliance#2",
				"Prayer of Fortitude#2",
				"Prayer of Fortitude#3",
				"Prayer of Spirit#1",
				"Prayer of Spirit#2",
				"Gift of the Wild#2",
				"Gift of the Wild#3",
				"Greater Blessing of Kings#0",
				"Demon Armor#5",
				"Demon Armor#6",
				"Fel Armor#1",
				"Fel Armor#2",
				"Fel Armor (improved)#2",
				"Touch of Shadow#0",
				"Burning Wish#0",
				"Shadowform#0",
				"Brilliant Wizard Oil#0",
				"Superior Wizard Oil#0",
				"Runn Tum Tuber#0",
				"Well Fed (sp)#0",
				"Flask of Supreme Power#0",
				"Flask of Pure Death#0",
				"Spirit of Zanza#0",
				"Greater Arcane Elixir#0",
				"Elixir of Shadow Power#0",
				"Moonkin Aura#0",
				"Wrath of Air Totem#1",
				"Totem of Wrath#1",
				"Drums of Battle#0",
				"Destruction#0",
				"Misery#0",
				"Rallying Cry of the Dragonslayer#0",
				"Spirit of Zandalar#0",
				"Warchief's Blessing#0",
				"Sayge's Dark Fortune of Damage#0",
				"Mol'dar's Moxie#0",
				"Slip'kik's Savvy#0",
				"Songflower Serenade#0",
				"Shadow Weaving#0",
				"Improved Scorch#0",
				"Curse of the Elements#3",
				"Curse of the Elements#4",
				"Curse of the Elements (improved)#3",
				"Curse of the Elements (improved)#4"
		));
	}

	@Nested
	class BuffAttributes {
		@ParameterizedTest(name = "{0}")
		@CsvSource({
				"VANILLA_P1, 1, 31",
				"TBC_P1, 2, 40",
		})
		void talent(PhaseId phaseId, int rank, int statValue) {
			var buff = buffRepository.getBuff(BuffId.ARCANE_BRILLIANCE, rank, phaseId).orElseThrow();

			assertModifier(buff.getEffect(), List.of(
					Attribute.of(INTELLECT, statValue)
			));
		}
	}
}

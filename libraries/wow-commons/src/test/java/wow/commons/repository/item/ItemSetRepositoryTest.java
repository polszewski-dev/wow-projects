package wow.commons.repository.item;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.effect.EffectId;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AttributeConditions.INCINERATE;
import static wow.commons.constant.AttributeConditions.SHADOW_BOLT;
import static wow.commons.model.attribute.AttributeCondition.comma;
import static wow.commons.model.attribute.AttributeId.DAMAGE_PCT;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-10-25
 */
class ItemSetRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	ItemSetRepository itemSetRepository;

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Infusion, TBC_P1",
			"Malefic Raiment, TBC_P3"
	})
	void required_phase_is_correct(String name, PhaseId expected) {
		var itemSet = getItemSet(name);

		var actual = itemSet.getEarliestPhaseId();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Infusion, ",
			"Malefic Raiment, Warlock"
	})
	void required_class_is_correct(String name, String expectedStr) {
		var itemSet = getItemSet(name);

		var actual = itemSet.getRequiredCharacterClassIds();
		var expected = toList(expectedStr, CharacterClassId::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Infusion, TAILORING",
			"Malefic Raiment, "
	})
	void required_profession_is_correct(String name, ProfessionId expected) {
		var itemSet = getItemSet(name);

		var actual = itemSet.getRequiredProfession();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getBonusData")
	void bonus_is_correct(BonusData data) {
		var itemSet = getItemSet(data.name());

		var actual = itemSet.getItemSetBonuses().stream()
				.map(BonusLine::of)
				.toList();
		var expected = data.bonusLines();

		assertThat(actual).isEqualTo(expected);
	}

	static List<BonusData> getBonusData() {
		return List.of(
				new BonusData(
						"Spellstrike Infusion",
						List.of(
								new BonusLine(2, EffectId.of(224266), "Gives a chance when your harmful spells land to increase the damage of your spells and effects by 92 for 10 sec. (Proc chance: 5%)")
						)
				),
				new BonusData(
						"Malefic Raiment",
						List.of(
								new BonusLine(2, EffectId.of(231051), "Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health."),
								new BonusLine(4, DAMAGE_PCT, 6, comma(SHADOW_BOLT, INCINERATE), "Increases the damage dealt by your Shadow Bolt and Incinerate abilities by 6%.")
						)
				)
		);
	}

	record BonusData(String name, List<BonusLine> bonusLines) {}

	record BonusLine(int numPieces, EffectId effectId, StatLine statLine, String tooltip) {
		BonusLine(int numPieces, EffectId effectId, String tooltip) {
			this(numPieces, effectId, null, tooltip);
		}

		BonusLine(int numPieces, AttributeId id, double value, AttributeCondition condition, String tooltip) {
			this(numPieces, null, new StatLine(id, value, condition, null), tooltip);
		}

		static BonusLine of(ItemSetBonus bonus) {
			var bonusEffect = bonus.bonusEffect();

			if (bonusEffect.hasModifierComponent()) {
				return new BonusLine(bonus.numPieces(), null, new StatLine(bonusEffect.getModifierComponent().attributes(), null), bonusEffect.getTooltip());
			} else {
				return new BonusLine(bonus.numPieces(), bonusEffect.getId(), bonusEffect.getTooltip());
			}
		}
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Infusion, Spellstrike Hood+Spellstrike Pants",
			"Malefic Raiment, Gloves of the Malefic+Hood of the Malefic+Leggings of the Malefic+Mantle of the Malefic+Robe of the Malefic+Boots of the Malefic+Bracers of the Malefic+Belt of the Malefic"
	})
	void pieces_is_correct(String name, String expectedStr) {
		var itemSet = getItemSet(name);

		var actual = itemSet.getPieces();
		var expected = toList(expectedStr, Function.identity());

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Infusion",
			"Malefic Raiment"
	})
	void tooltip_is_correct(String name) {
		var itemSet = getItemSet(name);

		var actual = itemSet.getTooltip();

		assertThat(actual).isBlank();
	}

	@ParameterizedTest
	@CsvSource({
			"Spellstrike Infusion, inv_helmet_27",
			"Malefic Raiment, inv_helmet_103"
	})
	void icon_is_correct(String name, String expected) {
		var itemSet = getItemSet(name);

		var actual = itemSet.getIcon();

		assertThat(actual).isEqualTo(expected);
	}

	ItemSet getItemSet(String name) {
		return itemSetRepository.getItemSet(name, TBC_P5).orElseThrow();
	}
}
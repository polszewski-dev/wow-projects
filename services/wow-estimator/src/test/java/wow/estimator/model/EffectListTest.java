package wow.estimator.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.ActivatedAbility;
import wow.estimator.WowEstimatorSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.POWER_PCT;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2023-11-13
 */
class EffectListTest extends WowEstimatorSpringTest {
	@Test
	void addEffectSingleStack() {
		var effectList = new EffectList(character);
		var effect = getEffect(28189); // Fel Armor

		effectList.addEffect(effect);

		assertThat(effectList.getEffects()).isEqualTo(List.of(effect));
		assertThat(effectList.getEffectCount()).isEqualTo(1);
		assertThat(effectList.getStackCount(0)).isEqualTo(1);
		assertThat(effectList.getEffect(0)).isSameAs(effect);
		assertThat(effectList.getActivatedAbilities()).isEmpty();
	}

	@Test
	void addEffectMultipleStacks() {
		var effectList = new EffectList(character);
		var effect = getEffect(120131856); // Darkmoon Card: Crusade

		effectList.addEffect(effect, 10);

		assertThat(effectList.getEffects()).isEqualTo(List.of(effect));
		assertThat(effectList.getEffectCount()).isEqualTo(1);
		assertThat(effectList.getStackCount(0)).isEqualTo(10);
		assertThat(effectList.getEffect(0)).isSameAs(effect);
		assertThat(effectList.getActivatedAbilities()).isEmpty();
	}

	@Test
	void addEffectActivatedAbility() {
		var effectList = new EffectList(character);
		var activatedAbility = getActivatedAbility(132483); // The Skull of Gul'dan

		effectList.addActivatedAbility(activatedAbility);

		assertThat(effectList.getEffects()).isEmpty();
		assertThat(effectList.getEffectCount()).isZero();
		assertThat(effectList.getActivatedAbilities()).isEqualTo(List.of(activatedAbility));
	}

	@Test
	void removeEffectSingleStack() {
		var effectList = new EffectList(character);
		var effect = getEffect(28189); // Fel Armor

		effectList.addEffect(effect);
		effectList.removeEffect(SpecialAbility.of(effect));

		assertThat(effectList.getEffects()).isEmpty();
		assertThat(effectList.getEffectCount()).isZero();
	}

	@Test
	void removeEffectMultipleStacks() {
		var effectList = new EffectList(character);
		var effect = getEffect(120131856); // Darkmoon Card: Crusade

		effectList.addEffect(effect, 10);

		effectList.removeEffect(SpecialAbility.of(effect));

		assertThat(effectList.getEffects()).isEmpty();
		assertThat(effectList.getEffectCount()).isZero();
	}

	@Test
	void removeEffectNotOnTheListModifierOnly() {
		var effectList = new EffectList(character);
		var effect = EffectImpl.newAttributeEffect(Attributes.of(
				POWER_PCT, 1
		));

		effectList.removeEffect(SpecialAbility.of(effect));

		assertThat(effectList.getEffects()).hasSize(1);
		assertThat(effectList.getStackCount(0)).isEqualTo(1);

		var negativeEffect = effectList.getEffect(0);

		assertThat(negativeEffect.hasModifierComponentOnly()).isTrue();
		assertThat(negativeEffect.getModifierAttributeList()).isEqualTo(List.of(
				Attribute.of(POWER_PCT, -1)
		));
	}

	@Test
	void removeEffectActivatedAbility() {
		var effectList = new EffectList(character);
		var activatedAbility = getActivatedAbility(132483); // The Skull of Gul'dan

		effectList.addActivatedAbility(activatedAbility);

		effectList.removeEffect(SpecialAbility.of(activatedAbility));

		assertThat(effectList.getActivatedAbilities()).isEmpty();
	}

	@Test
	void removeAll() {
		var effectList = new EffectList(character);
		var effect = getEffect(28189); // Fel Armor
		var activatedAbility = getActivatedAbility(132483); // The Skull of Gul'dan

		effectList.addEffect(effect);
		effectList.addActivatedAbility(activatedAbility);

		effectList.removeAll();

		assertThat(effectList.getEffects()).isEmpty();
		assertThat(effectList.getActivatedAbilities()).isEmpty();
	}

	Player character;

	@BeforeEach
	void setup() {
		character = getCharacter();
		equipGearSet(character);
	}

	private Effect getEffect(int effectId) {
		return spellRepository.getEffect(EffectId.of(effectId), TBC_P5).orElseThrow();
	}

	private ActivatedAbility getActivatedAbility(int spellId) {
		return (ActivatedAbility) spellRepository.getSpell(spellId, TBC_P5).orElseThrow();
	}
}
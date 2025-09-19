package wow.character.constant;

import wow.commons.model.spell.AbilityId;
import wow.test.commons.AbilityNames;

/**
 * User: POlszewski
 * Date: 2025-09-15
 */
public interface AbilityIds {
	AbilityId AMPLIFY_CURSE = AbilityId.of(AbilityNames.AMPLIFY_CURSE);
	AbilityId ATIESH_GREATSTAFF_OF_THE_GUARDIAN = AbilityId.of(AbilityNames.ATIESH_GREATSTAFF_OF_THE_GUARDIAN);
	AbilityId BERSERKING = AbilityId.of(AbilityNames.BERSERKING);
	AbilityId BLOOD_FURY = AbilityId.of(AbilityNames.BLOOD_FURY);
	AbilityId CORRUPTION = AbilityId.of(AbilityNames.CORRUPTION);
	AbilityId CREATE_FIRESTONE_GREATER = AbilityId.of(AbilityNames.CREATE_FIRESTONE_GREATER);
	AbilityId CURSE_OF_AGONY = AbilityId.of(AbilityNames.CURSE_OF_AGONY);
	AbilityId CURSE_OF_DOOM = AbilityId.of(AbilityNames.CURSE_OF_DOOM);
	AbilityId CURSE_OF_EXHAUSTION = AbilityId.of(AbilityNames.CURSE_OF_EXHAUSTION);
	AbilityId DESTRUCTION_POTION = AbilityId.of(AbilityNames.DESTRUCTION_POTION);
	AbilityId DIVINE_SPIRIT = AbilityId.of(AbilityNames.DIVINE_SPIRIT);
	AbilityId DRAIN_LIFE = AbilityId.of(AbilityNames.DRAIN_LIFE);
	AbilityId DRAIN_SOUL = AbilityId.of(AbilityNames.DRAIN_SOUL);
	AbilityId FLAMESTRIKE = AbilityId.of(AbilityNames.FLAMESTRIKE);
	AbilityId IMMOLATE = AbilityId.of(AbilityNames.IMMOLATE);
	AbilityId INCINERATE = AbilityId.of(AbilityNames.INCINERATE);
	AbilityId LIFE_TAP = AbilityId.of(AbilityNames.LIFE_TAP);
	AbilityId PRAYER_OF_SPIRIT = AbilityId.of(AbilityNames.PRAYER_OF_SPIRIT);
	AbilityId SHADOWBURN = AbilityId.of(AbilityNames.SHADOWBURN);
	AbilityId SHADOW_BOLT = AbilityId.of(AbilityNames.SHADOW_BOLT);
	AbilityId SHADOW_WORD_PAIN = AbilityId.of(AbilityNames.SHADOW_WORD_PAIN);
	AbilityId SOUL_FIRE = AbilityId.of(AbilityNames.SOUL_FIRE);
	AbilityId TOUCH_OF_WEAKNESS = AbilityId.of(AbilityNames.TOUCH_OF_WEAKNESS);

	void doNotImplementThis();
}

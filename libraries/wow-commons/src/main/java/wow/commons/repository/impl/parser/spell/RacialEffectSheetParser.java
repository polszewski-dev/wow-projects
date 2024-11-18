package wow.commons.repository.impl.parser.spell;

import wow.commons.model.effect.Effect;
import wow.commons.model.effect.RacialSource;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.effect.impl.RacialEffectImpl;
import wow.commons.model.spell.AbilityId;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2024-11-18
 */
public class RacialEffectSheetParser extends SpellEffectSheetParser {
	public RacialEffectSheetParser(String sheetName, SpellRepositoryImpl spellRepository, int maxModAttributes, int maxEvents) {
		super(sheetName, spellRepository, maxModAttributes, maxEvents);
	}

	@Override
	protected Effect getEffect() {
		var effect = (RacialEffectImpl) super.getEffect();
		var restriction = getRestriction();

		effect.setCharacterRestriction(restriction);
		effect.attachSource(new RacialSource(this.getDescription()));
		return effect;
	}

	@Override
	protected EffectImpl newEffect() {
		var augmentedAbility = colAugmentedAbility.getEnum(AbilityId::parse, null);

		return new RacialEffectImpl(augmentedAbility);
	}
}

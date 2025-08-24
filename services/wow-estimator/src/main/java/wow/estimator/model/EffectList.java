package wow.estimator.model;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedStats;
import wow.character.util.AbstractEffectCollector;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.ActivatedAbility;
import wow.estimator.util.NonModifierHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-11-09
 */
public class EffectList extends AbstractEffectCollector {
	private final List<Effect> effects = new ArrayList<>();
	private final List<Integer> stackCounts = new ArrayList<>();
	private final List<ActivatedAbility> activatedAbilities = new ArrayList<>();

	public EffectList(Character character) {
		super(character);
	}

	@Override
	public void addEffect(Effect effect, int stackCount) {
		if (effect.hasAugmentedAbilities()) {
			return;
		}
		effects.add(effect);
		stackCounts.add(stackCount);
	}

	@Override
	public void addActivatedAbility(ActivatedAbility activatedAbility) {
		activatedAbilities.add(activatedAbility);
	}

	public EffectList copy() {
		var copy = new EffectList(character);
		copy.copyFieldsFrom(this);
		copy.activatedAbilities.addAll(activatedAbilities);
		copy.effects.addAll(effects);
		copy.stackCounts.addAll(stackCounts);
		return copy;
	}

	public List<ActivatedAbility> getActivatedAbilities() {
		return activatedAbilities;
	}

	public int getEffectCount() {
		return effects.size();
	}

	public Effect getEffect(int idx) {
		return effects.get(idx);
	}

	public int getStackCount(int idx) {
		return stackCounts.get(idx);
	}

	public List<Effect> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	public void addEffect(SpecialAbility specialAbility) {
		specialAbility.consume(this::addEffect, this::addActivatedAbility);
	}

	public void removeEffect(SpecialAbility specialAbility) {
		specialAbility.consume(this::removeEffect, activatedAbilities::remove);
	}

	private void removeEffect(Effect effect) {
		int idx = effects.indexOf(effect);
		if (idx >= 0) {
			effects.remove(idx);
			stackCounts.remove(idx);
		} else if (effect.hasModifierComponentOnly()) {
			addEffect(getNegativeModifier(effect));
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Effect getNegativeModifier(Effect effect) {
		var modifierComponent = effect.getModifierComponent();

		if (modifierComponent == null || modifierComponent.attributes().isEmpty()) {
			return Effect.EMPTY;
		}

		return EffectImpl.newAttributeEffect(
				effect.getAugmentedAbilities(),
				modifierComponent.attributes().scale(-1),
				null
		);
	}

	public void removeAll() {
		effects.clear();
		stackCounts.clear();
		activatedAbilities.clear();
	}

	public static EffectList createSolved(Character character) {
		var effectList = new EffectList(character);
		effectList.solveAll();
		return effectList;
	}

	public static EffectList createSolvedForTarget(Player player) {
		return createSolved(player.getTarget());
	}

	public void accumulateAttributes(AccumulatedStats result, NonModifierHandler nonModifierHandler) {
		for (int i = 0; i < getEffectCount(); ++i) {
			var effect = getEffect(i);
			var stackCount = getStackCount(i);

			if (effect.hasModifierComponent()) {
				var modifierAttributeList = effect.getModifierAttributeList();
				result.accumulateAttributes(modifierAttributeList, stackCount);
			} else if (nonModifierHandler != null) {
				nonModifierHandler.handleNonModifier(effect, stackCount);
			}
		}

		if (nonModifierHandler != null) {
			nonModifierHandler.handleActivatedAbilities(activatedAbilities);
		}
	}
}

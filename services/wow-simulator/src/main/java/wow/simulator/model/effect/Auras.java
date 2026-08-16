package wow.simulator.model.effect;

import lombok.RequiredArgsConstructor;
import wow.character.model.character.Party;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.AttributeEffect;
import wow.commons.model.item.ItemSet;
import wow.commons.model.spell.ActivatedAbility;
import wow.simulator.model.unit.Unit;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2026-08-15
 */
@RequiredArgsConstructor
public class Auras implements EffectCollection {
	private final Party<? extends Unit> party;

	private boolean needsRefresh;

	private final Set<Effect> uniqueEffects = new LinkedHashSet<>();
	private final Map<String, Effect> nonUniqueEffectsByName = new LinkedHashMap<>();

	@Override
	public void collectEffects(EffectCollector collector) {
		refresh();

		for (var effect : uniqueEffects) {
			collector.addEffect(effect);
		}

		for (var effect : nonUniqueEffectsByName.values()) {
			collector.addEffect(effect);
		}
	}

	private void refresh() {
		if (!needsRefresh) {
			return;
		}

		collectAuras();

		needsRefresh = false;
	}

	private void collectAuras() {
		var auraCollector = new AuraCollector();

		party.forEachMemberAndPet((Unit memberOrPet) -> memberOrPet.collectAuras(auraCollector));

		uniqueEffects.clear();
		nonUniqueEffectsByName.clear();

		for (var effect : auraCollector.effects) {
			if (effect instanceof AttributeEffect) {
				uniqueEffects.add(effect);
			} else {
				var existingEffect = nonUniqueEffectsByName.get(effect.getName());

				if (existingEffect == null || getEffectStrength(effect) > getEffectStrength(existingEffect)) {
					nonUniqueEffectsByName.put(effect.getName(), effect);
				}
			}
		}
	}

	public void invalidate() {
		this.needsRefresh = true;
	}

	private static double getEffectStrength(Effect effect) {
		return effect.getModifierAttributeList().stream().mapToDouble(Attribute::value).sum();
	}

	private static class AuraCollector implements EffectCollector {
		private final List<Effect> effects = new ArrayList<>();

		@Override
		public void addEffect(Effect effect, int stackCount) {
			if (effect.hasAugmentedAbilities() || !effect.isAura()) {
				return;
			}

			if (stackCount != 1) {
				throw new IllegalArgumentException("No stacks for auras");
			}

			effects.add(effect);
		}

		@Override
		public void addActivatedAbility(ActivatedAbility activatedAbility) {
			// ignored
		}

		@Override
		public void addItemSockets(ItemSockets itemSockets) {
			// ignored
		}

		@Override
		public void addItemSet(ItemSet itemSet) {
			// ignored
		}
	}
}

package wow.minmax.model;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.AccumulatedBaseStats;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActivatedAbility;
import wow.minmax.util.NonModifierHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-11-13
 */
@Getter
@Setter
public class AccumulatedRotationStats implements NonModifierHandler {
	private AccumulatedBaseStats baseStats;
	private final Map<AbilityId, AccumulatedDamagingAbilityStats> statsByAbilityId = new EnumMap<>(AbilityId.class);
	private final List<StatConversion> statConversions = new ArrayList<>();
	private final List<Effect> nonModifierEffects = new ArrayList<>();
	private final List<Integer> nonModifierStackCounts = new ArrayList<>();
	private final List<ActivatedAbility> activatedAbilities = new ArrayList<>();

	public AccumulatedRotationStats copy() {
		var copy = new AccumulatedRotationStats();

		copy.baseStats = baseStats.copy();

		for (var entry : statsByAbilityId.entrySet()) {
			var abilityId = entry.getKey();
			var statsCopy = entry.getValue().copy();

			copy.statsByAbilityId.put(abilityId, statsCopy);
		}

		copy.statConversions.addAll(statConversions);
		copy.nonModifierEffects.addAll(nonModifierEffects);
		copy.nonModifierStackCounts.addAll(nonModifierStackCounts);
		copy.activatedAbilities.addAll(activatedAbilities);

		return copy;
	}

	public AccumulatedDamagingAbilityStats get(AbilityId abilityId) {
		return statsByAbilityId.get(abilityId);
	}

	public void addStats(Ability ability, AccumulatedDamagingAbilityStats abilityStats) {
		statsByAbilityId.put(ability.getAbilityId(), abilityStats);
	}

	public void increasePower(double increase) {
		for (var stats : statsByAbilityId.values()) {
			stats.increasePower(increase);
		}
	}

	@Override
	public void handleNonModifier(Effect effect, int stackCount) {
		if (!effect.getStatConversions().isEmpty()) {
			this.statConversions.addAll(effect.getStatConversions());
		} else {
			this.nonModifierEffects.add(effect);
			this.nonModifierStackCounts.add(stackCount);
		}
	}

	@Override
	public void handleActivatedAbilities(List<ActivatedAbility> activatedAbilities) {
		this.activatedAbilities.addAll(activatedAbilities);
	}

	public int getNonModifierEffectCount() {
		return nonModifierEffects.size();
	}

	public Effect getNonModifierEffect(int idx) {
		return nonModifierEffects.get(idx);
	}

	public int getNonModifierStackCount(int idx) {
		return nonModifierStackCounts.get(idx);
	}
}

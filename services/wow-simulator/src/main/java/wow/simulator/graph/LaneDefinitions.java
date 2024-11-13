package wow.simulator.graph;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-08-21
 */
public abstract class LaneDefinitions {
	public static LaneDefinitions get(CharacterClassId characterClassId) {
		if (characterClassId == CharacterClassId.WARLOCK) {
			return new WarlockLaneDefinitions();
		} else {
			throw new IllegalArgumentException("" + characterClassId);
		}
	}

	public abstract Map<String, Color> getColorsByName();

	public abstract List<Lane> getEffectLanes();

	public abstract List<Lane> getCooldownLanes();

	public Color getSpellColor(Spell spell) {
		Color color = getColorsByName().get(spell.getName());
		if (color == null) {
			throw new IllegalArgumentException("" + spell);
		}
		return color;
	}

	public Lane getCooldownLane(AbilityId abilityId) {
		return getCooldownLanes().stream()
				.filter(x -> x.matches(abilityId))
				.findAny()
				.orElseThrow();
	}

	public Lane getEffectLane(AbilityId abilityId) {
		return getEffectLanes().stream()
				.filter(x -> x.matches(abilityId))
				.findAny()
				.orElseThrow();
	}

	protected Map.Entry<String, Color> color(AbilityId abilityId, Color color) {
		return color(abilityId.getName(), color);
	}

	protected Map.Entry<String, Color> color(String name, Color color) {
		return Map.entry(name, color);
	}

	protected Lane lane(int laneId, AbilityId abilityId, String label) {
		return lane(laneId, abilityId.getName(), label);
	}

	protected Lane lane(int laneId, AbilityId abilityId) {
		return lane(laneId, abilityId, abilityId.getName());
	}

	protected Lane lane(int laneId, String name, String label) {
		return new Lane(laneId, name, label, getColor(name).orElseThrow());
	}

	protected Lane lane(int laneId, String name) {
		return lane(laneId, name, name);
	}

	protected Optional<Color> getColor(String name) {
		return Optional.ofNullable(getColorsByName().get(name));
	}

	public boolean isIgnored(EffectInstance effect) {
		return getIgnoredEffects().contains(effect.getSourceAbilityId());
	}

	protected abstract Set<AbilityId> getIgnoredEffects();
}

package wow.simulator.graph;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.spells.EffectId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.simulator.model.effect.Effect;

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
		Color color = getColorsByName().get(spell.getSpellId().getName());
		if (color == null) {
			throw new IllegalArgumentException("" + spell);
		}
		return color;
	}

	public Lane getCooldownLane(SpellId spellId) {
		return getCooldownLanes().stream()
				.filter(x -> x.matches(spellId))
				.findAny()
				.orElseThrow();
	}

	public Lane getEffectLane(SpellId spellId) {
		return getEffectLanes().stream()
				.filter(x -> x.matches(spellId))
				.findAny()
				.orElseThrow();
	}

	protected Map.Entry<String, Color> color(SpellId spellId, Color color) {
		return Map.entry(spellId.getName(), color);
	}

	protected Map.Entry<String, Color> color(EffectId effectId, Color color) {
		return Map.entry(effectId.getName(), color);
	}

	protected Lane lane(int laneId, SpellId spellId, String label) {
		return new Lane(laneId, spellId, label, getColor(spellId.getName()).orElseThrow());
	}

	protected Lane lane(int laneId, EffectId effectId) {
		return lane(laneId, effectId, null);
	}

	protected Lane lane(int laneId, EffectId effectId, String label) {
		return new Lane(laneId, effectId, label, getColor(effectId.getName()).orElseThrow());
	}

	protected Optional<Color> getColor(String name) {
		return Optional.ofNullable(getColorsByName().get(name));
	}

	public boolean isIgnored(Effect effect) {
		return getIgnoredEffects().contains(effect.getSourceSpell().getSpellId());
	}

	protected abstract Set<SpellId> getIgnoredEffects();
}

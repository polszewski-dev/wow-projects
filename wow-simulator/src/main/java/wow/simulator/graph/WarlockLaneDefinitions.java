package wow.simulator.graph;

import wow.commons.model.spell.AbilityId;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static wow.commons.model.spell.AbilityId.*;

/**
 * User: POlszewski
 * Date: 2023-08-21
 */
public class WarlockLaneDefinitions extends LaneDefinitions {
	private static final String SHADOW_VULNERABILITY = "Shadow Vulnerability";
	private static final String SHADOW_TRANCE = "Shadow Trance";

	private final Map<String, Color> colorsByName = Map.ofEntries(
			color(SHADOW_BOLT, new Color(169, 178, 0xF0)),
			color(SHADOWBURN, new Color(240, 137, 231)),
			color(DRAIN_LIFE, new Color(124, 240, 152)),
			color(LIFE_TAP, new Color(149, 74, 182)),
			color(CURSE_OF_AGONY, new Color(196, 240, 56)),
			color(CURSE_OF_DOOM, new Color(196, 240, 56)),
			color(CORRUPTION,new Color(134, 208, 240)),
			color(UNSTABLE_AFFLICTION,new Color(240, 238, 135)),
			color(SIPHON_LIFE,new Color(128, 240, 174)),
			color(IMMOLATE,new Color(240, 170, 124)),
			color(BLOOD_FURY, new Color(240, 61, 85)),
			color(AMPLIFY_CURSE, new Color(196, 240, 56)),
			color(SHADOW_VULNERABILITY, new Color(197, 67, 240)),
			color(SHADOW_TRANCE, new Color(197, 67, 240))
	);

	private final List<Lane> effectLanes = List.of(
			lane(2, CURSE_OF_AGONY),
			lane(2, CURSE_OF_DOOM),
			lane(3, CORRUPTION),
			lane(4, UNSTABLE_AFFLICTION),
			lane(5, SIPHON_LIFE),
			lane(6, IMMOLATE),
			lane(7, BLOOD_FURY),
			lane(8, SHADOW_VULNERABILITY),
			lane(9, SHADOW_TRANCE),
			lane(10, DRAIN_LIFE)
	);

	private final List<Lane> cooldownLanes = List.of(
			lane(20, AMPLIFY_CURSE, "Amplify Curse cd"),
			lane(22, CURSE_OF_DOOM, "CoD cd"),
			lane(24, SHADOWBURN, "ShadowBurn cd"),
			lane(26, BLOOD_FURY, "Blood Fury cd")
	);

	private static final Set<AbilityId> IGNORED_EFFECTS = Set.of();

	@Override
	public Map<String, Color> getColorsByName() {
		return colorsByName;
	}

	@Override
	public List<Lane> getEffectLanes() {
		return effectLanes;
	}

	@Override
	public List<Lane> getCooldownLanes() {
		return cooldownLanes;
	}

	@Override
	protected Set<AbilityId> getIgnoredEffects() {
		return IGNORED_EFFECTS;
	}
}

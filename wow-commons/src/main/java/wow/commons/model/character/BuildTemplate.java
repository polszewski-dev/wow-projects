package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spells.SpellId;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
@Getter
public class BuildTemplate {
	private final BuildId buildId;
	private final CharacterClass characterClass;
	private final int level;
	private final String talentLink;
	private final PveRole role;
	private final SpellId damagingSpell;
	private final List<SpellId> relevantSpells;
	private final PetType activePet;
	private Map<BuffSetId, List<String>> buffSets;

	public List<String> getBuffSet(BuffSetId buffSetId) {
		return buffSets.getOrDefault(buffSetId, List.of());
	}
}

package wow.scraper.repository;

import wow.commons.model.effect.Effect;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-10-07
 */
public interface ItemSpellRepository {
	List<ActivatedAbility> getActivatedAbilities();

	List<Effect> getItemEffects();

	Optional<ActivatedAbility> getActivatedAbility(GameVersionId gameVersion, String tooltip);

	Optional<Effect> getItemEffect(GameVersionId gameVersion, String tooltip);
}

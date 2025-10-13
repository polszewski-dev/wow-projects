package wow.commons.repository.spell;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface SpellRepository {
	List<Ability> getAvailableAbilities(CharacterClassId characterClassId, int level, PhaseId phaseId);

	Optional<Ability> getAbility(AbilityId abilityId, int rank, PhaseId phaseId);

	Optional<Ability> getAbility(String name, int rank, PhaseId phaseId);

	Optional<Ability> getAbility(SpellId spellId, PhaseId phaseId);

	Optional<Spell> getSpell(SpellId spellId, PhaseId phaseId);

	Optional<Effect> getEffect(EffectId effectId, PhaseId phaseId);

	Optional<Effect> getEffect(String name, PhaseId phaseId);

	List<RacialEffect> getRacialEffects(RaceId raceId, GameVersionId gameVersionId);
}

package wow.commons.repository.spell;

import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.effect.Effect;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public interface EffectRepository {
	List<Ability> getAvailableAbilities(CharacterClassId characterClassId, int level, PhaseId phaseId);

	Optional<Ability> getAbility(AbilityId abilityId, int rank, PhaseId phaseId);

	Optional<Spell> getSpell(int spellId, PhaseId phaseId);

	Optional<Effect> getEffect(int effectId, PhaseId phaseId);

	List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId);

	Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId);

	Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId);

	List<Buff> getAvailableBuffs(PhaseId phaseId);

	Optional<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId);
}

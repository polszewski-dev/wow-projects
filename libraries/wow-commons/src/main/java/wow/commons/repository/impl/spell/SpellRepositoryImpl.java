package wow.commons.repository.impl.spell;

import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.AbilityIdAndRank;
import wow.commons.model.spell.Spell;
import wow.commons.repository.impl.parser.spell.SpellExcelParser;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.GameVersionMap;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
public class SpellRepositoryImpl implements SpellRepository {
	private final PhaseMap<CharacterClassId, List<Ability>> abilitiesByClass = new PhaseMap<>();
	private final PhaseMap<AbilityIdAndRank, Ability> abilitiesByRankedId = new PhaseMap<>();
	private final PhaseMap<Integer, Spell> spellsById = new PhaseMap<>();
	private final PhaseMap<EffectId, Effect> effectById = new PhaseMap<>();
	private final GameVersionMap<RaceId, List<RacialEffect>> racialEffects = new GameVersionMap<>();

	public SpellRepositoryImpl(SpellExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getSpells().forEach(this::addSpell);
		parser.getEffects().forEach(this::addEffect);
	}

	@Override
	public List<Ability> getAvailableAbilities(CharacterClassId characterClassId, int level, PhaseId phaseId) {
		return abilitiesByClass.getOptional(phaseId, characterClassId).orElse(List.of()).stream()
				.filter(spell -> spell.getRequiredLevel() <= level)
				.toList();
	}

	@Override
	public Optional<Ability> getAbility(AbilityId abilityId, int rank, PhaseId phaseId) {
		return abilitiesByRankedId.getOptional(phaseId, new AbilityIdAndRank(abilityId, rank));
	}

	@Override
	public Optional<Spell> getSpell(int spellId, PhaseId phaseId) {
		return spellsById.getOptional(phaseId, spellId);
	}

	@Override
	public Optional<Effect> getEffect(EffectId effectId, PhaseId phaseId) {
		return effectById.getOptional(phaseId, effectId);
	}

	@Override
	public List<RacialEffect> getRacialEffects(RaceId raceId, GameVersionId gameVersionId) {
		return racialEffects.getOptional(gameVersionId, raceId).orElse(List.of());
	}

	private void addSpell(Spell spell) {
		if (spell instanceof Ability ability) {
			for (var characterClassId : ability.getRequiredCharacterClassIds()) {
				addEntryForEveryPhase(abilitiesByClass, characterClassId, ability);
			}

			putForEveryPhase(abilitiesByRankedId, ability.getRankedAbilityId(), ability);
		}

		putForEveryPhase(spellsById, spell.getId(), spell);
	}

	private void addEffect(Effect effect) {
		putForEveryPhase(effectById, effect.getId(), effect);

		var gameVersionId = effect.getGameVersionId();

		if (effect instanceof RacialEffect racial) {
			for (var raceId : racial.getCharacterRestriction().raceIds()) {
				racialEffects.computeIfAbsent(gameVersionId, raceId, x -> new ArrayList<>())
						.add(racial);
			}
		}
	}
}

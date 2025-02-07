package wow.commons.repository.impl.spell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.*;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.repository.impl.parser.spell.SpellExcelParser;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.CollectionUtil;
import wow.commons.util.GameVersionMap;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Component
@RequiredArgsConstructor
public class SpellRepositoryImpl implements SpellRepository {
	private final PhaseMap<CharacterClassId, List<Ability>> abilitiesByClass = new PhaseMap<>();
	private final PhaseMap<AbilityIdAndRank, Ability> abilitiesByRankedId = new PhaseMap<>();
	private final PhaseMap<Integer, Spell> spellsById = new PhaseMap<>();
	private final PhaseMap<Integer, Effect> effectById = new PhaseMap<>();
	private final GameVersionMap<RaceId, List<RacialEffect>> racialEffects = new GameVersionMap<>();

	@Value("${spells.xls.file.path}")
	private String xlsFilePath;

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
	public Optional<Effect> getEffect(int effectId, PhaseId phaseId) {
		return effectById.getOptional(phaseId, effectId);
	}

	@Override
	public List<RacialEffect> getRacialEffects(RaceId raceId, GameVersionId gameVersionId) {
		return racialEffects.getOptional(gameVersionId, raceId).orElse(List.of());
	}

	@PostConstruct
	public void init() throws IOException {
		var spellExcelParser = new SpellExcelParser(xlsFilePath, this);
		spellExcelParser.readFromXls();

		spellsById.allValues().forEach(this::replaceDummyEffects);
		effectById.allValues().forEach(this::replaceDummySpells);
		spellsById.allValues().forEach(this::setMissingSpellFields);
	}

	private void replaceDummyEffects(Spell spell) {
		var effectApplication = spell.getEffectApplication();

		if (effectApplication == null) {
			return;
		}

		var effectId = effectApplication.effect().getEffectId();
		var phaseId = spell.getEarliestPhaseId();
		var effect = getEffect(effectId, phaseId).orElseThrow();
		var newEffectApplication = effectApplication.setEffect(effect);

		((SpellImpl) spell).setEffectApplication(newEffectApplication);
	}

	private void replaceDummySpells(Effect effect) {
		var newEvents = effect.getEvents().stream()
				.map(event -> replaceDummySpell(event, effect.getTimeRestriction()))
				.toList();

		((EffectImpl) effect).setEvents(newEvents);
	}

	private Event replaceDummySpell(Event event, TimeRestriction timeRestriction) {
		if (event.triggeredSpell() == null) {
			return event;
		}

		var spellId = event.triggeredSpell().getId();
		var phaseId = timeRestriction.earliestPhaseId();
		var spell = getSpell(spellId, phaseId).orElseThrow();

		return event.setTriggeredSpell(spell);
	}

	private void setMissingSpellFields(Spell spell) {
		var school = getSpellSchool(spell).orElse(null);
		var componentTypes = getComponentTypes(spell);
		var spellImpl = (SpellImpl) spell;

		spellImpl.setSchool(school);
		spellImpl.setHasDamagingComponent(componentTypes.contains(ComponentType.DAMAGE));
		spellImpl.setHasHealingComponent(componentTypes.contains(ComponentType.HEAL));
	}

	private Set<ComponentType> getComponentTypes(Spell spell) {
		var result = new HashSet<ComponentType>();

		for (var directComponent : spell.getDirectComponents()) {
			result.add(directComponent.type());
		}

		var appliedEffect = spell.getAppliedEffect();

		if (appliedEffect != null && appliedEffect.hasPeriodicComponent()) {
			result.add(appliedEffect.getPeriodicComponent().type());
		}

		return result;
	}

	private Optional<SpellSchool> getSpellSchool(Spell spell) {
		var result = new HashSet<SpellSchool>();

		for (var directComponent : spell.getDirectComponents()) {
			var school = directComponent.school();
			result.add(school);
		}

		var appliedEffect = spell.getAppliedEffect();

		if (appliedEffect != null && appliedEffect.hasPeriodicComponent()) {
			var school = appliedEffect.getPeriodicComponent().school();
			result.add(school);
		}

		result.remove(null);

		return CollectionUtil.getUniqueResult(List.copyOf(result));
	}

	public void addSpell(Spell spell) {
		if (spell instanceof Ability ability) {
			for (var characterClassId : ability.getCharacterRestriction().characterClassIds()) {
				addEntryForEveryPhase(abilitiesByClass, characterClassId, ability);
			}

			putForEveryPhase(abilitiesByRankedId, ability.getRankedAbilityId(), ability);
		}
		putForEveryPhase(spellsById, spell.getId(), spell);
	}

	public void addEffect(Effect effect) {
		validateEffect(effect);
		putForEveryPhase(effectById, effect.getEffectId(), effect);

		var gameVersionId = effect.getTimeRestriction().getGameVersionId();

		if (effect instanceof RacialEffect racial) {
			for (var raceId : racial.getCharacterRestriction().raceIds()) {
				racialEffects.computeIfAbsent(gameVersionId, raceId, x -> new ArrayList<>())
						.add(racial);
			}
		}
	}

	private void validateEffect(Effect effect) {
		if (effect.hasAugmentedAbilities() &&
			(effect.hasPeriodicComponent() || effect.hasAbsorptionComponent() || effect.getTickInterval() != null)) {
			throw new IllegalArgumentException();
		}
		if (effect.hasPeriodicComponent()) {
			Objects.requireNonNull(effect.getTickInterval());
		}
	}
}

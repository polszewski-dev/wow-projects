package wow.commons.repository.impl.spell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.*;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.repository.impl.parser.spell.SpellExcelParser;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.CollectionUtil;
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
@Repository
@RequiredArgsConstructor
public class SpellRepositoryImpl implements SpellRepository {
	private final PhaseMap<CharacterClassId, List<Ability>> abilitiesByClass = new PhaseMap<>();
	private final PhaseMap<AbilityIdAndRank, Ability> abilitiesByRankedId = new PhaseMap<>();
	private final PhaseMap<Integer, Spell> spellsById = new PhaseMap<>();
	private final PhaseMap<Integer, Effect> effectById = new PhaseMap<>();

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

		if (appliedEffect != null && appliedEffect.getPeriodicComponent() != null) {
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

		if (appliedEffect != null && appliedEffect.getPeriodicComponent() != null) {
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
	}

	private void validateEffect(Effect effect) {
		if (effect.getAugmentedAbility() != null &&
			(effect.getPeriodicComponent() != null || effect.getAbsorptionComponent() != null || effect.getTickInterval() != null)) {
			throw new IllegalArgumentException();
		}
		if (effect.getPeriodicComponent() != null) {
			Objects.requireNonNull(effect.getTickInterval());
		}
	}
}
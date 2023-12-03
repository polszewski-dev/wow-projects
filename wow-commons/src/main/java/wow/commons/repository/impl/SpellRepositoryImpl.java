package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffIdAndRank;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.*;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.parser.spell.SpellExcelParser;
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
	private final PhaseMap<CharacterClassId, List<Talent>> talentsByClass = new PhaseMap<>();
	private final PhaseMap<String, Talent> talentByClassByIdByRank = new PhaseMap<>();
	private final PhaseMap<String, Talent> talentByClassByCalcPosByRank = new PhaseMap<>();
	private final PhaseMap<BuffIdAndRank, Buff> buffsById = new PhaseMap<>();

	@Value("${spell.xls.file.path}")
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
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentsByClass.getOptional(phaseId, characterClassId).orElse(List.of()).stream()
				.toList();
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentId, rank);

		return talentByClassByIdByRank.getOptional(phaseId, key);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentCalculatorPosition, rank);

		return talentByClassByCalcPosByRank.getOptional(phaseId, key);
	}

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId) {
		return buffsById.values(phaseId).stream().toList();
	}

	@Override
	public Optional<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId) {
		return buffsById.getOptional(phaseId, new BuffIdAndRank(buffId, rank));
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

	private String getTalentKey(CharacterClassId characterClassId, TalentId talentId, int rank) {
		return characterClassId + "#" + talentId + "#" + rank;
	}

	private String getTalentKey(CharacterClassId characterClassId, int talentCalculatorPosition, int rank) {
		return characterClassId + "#" + talentCalculatorPosition + "#" + rank;
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

	public void addTalent(Talent talent) {
		String key1 = getTalentKey(talent.getCharacterClass(), talent.getTalentId(), talent.getRank());
		String key2 = getTalentKey(talent.getCharacterClass(), talent.getTalentCalculatorPosition(), talent.getRank());

		addEntryForEveryPhase(talentsByClass, talent.getCharacterClass(), talent);
		putForEveryPhase(talentByClassByIdByRank, key1, talent);
		putForEveryPhase(talentByClassByCalcPosByRank, key2, talent);

		addEffect(talent.getEffect());
	}

	public void addBuff(Buff buff) {
		putForEveryPhase(buffsById, buff.getId(), buff);
	}
}

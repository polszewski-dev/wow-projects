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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Repository
@RequiredArgsConstructor
public class SpellRepositoryImpl extends ExcelRepository implements SpellRepository {
	private final Map<CharacterClassId, List<Ability>> abilitiesByClass = new LinkedHashMap<>();
	private final Map<AbilityIdAndRank, List<Ability>> abilitiesByRankedId = new LinkedHashMap<>();
	private final Map<Integer, List<Spell>> spellsById = new LinkedHashMap<>();
	private final Map<Integer, List<Effect>> effectById = new LinkedHashMap<>();
	private final Map<CharacterClassId, List<Talent>> talentsByClass = new LinkedHashMap<>();
	private final Map<String, List<Talent>> talentByClassByIdByRank = new LinkedHashMap<>();
	private final Map<String, List<Talent>> talentByClassByCalcPosByRank = new LinkedHashMap<>();
	private final Map<BuffIdAndRank, List<Buff>> buffsById = new LinkedHashMap<>();
	private final List<Buff> buffs = new ArrayList<>();

	@Value("${spell.xls.file.path}")
	private String xlsFilePath;

	@Override
	public List<Ability> getAvailableAbilities(CharacterClassId characterClassId, int level, PhaseId phaseId) {
		return abilitiesByClass.getOrDefault(characterClassId, List.of()).stream()
				.filter(spell -> spell.getRequiredLevel() <= level)
				.filter(spell -> spell.isAvailableDuring(phaseId))
				.toList();
	}

	@Override
	public Optional<Ability> getAbility(AbilityId abilityId, int rank, PhaseId phaseId) {
		return getUnique(abilitiesByRankedId, new AbilityIdAndRank(abilityId, rank), phaseId);
	}

	@Override
	public Optional<Spell> getSpell(int spellId, PhaseId phaseId) {
		return getUnique(spellsById, spellId, phaseId);
	}

	@Override
	public Optional<Effect> getEffect(int effectId, PhaseId phaseId) {
		return getUnique(effectById, effectId, phaseId);
	}

	@Override
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentsByClass.getOrDefault(characterClassId, List.of()).stream()
				.filter(spell -> spell.isAvailableDuring(phaseId))
				.toList();
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentId, rank);

		return getUnique(talentByClassByIdByRank, key, phaseId);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentCalculatorPosition, rank);

		return getUnique(talentByClassByCalcPosByRank, key, phaseId);
	}

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId) {
		return getList(buffs, phaseId);
	}

	@Override
	public Optional<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId) {
		return getUnique(buffsById, new BuffIdAndRank(buffId, rank), phaseId);
	}

	@PostConstruct
	public void init() throws IOException {
		var spellExcelParser = new SpellExcelParser(xlsFilePath, this);
		spellExcelParser.readFromXls();

		spellsById.values().stream().flatMap(Collection::stream).forEach(this::replaceDummyEffects);
		effectById.values().stream().flatMap(Collection::stream).forEach(this::replaceDummySpells);
		spellsById.values().stream().flatMap(Collection::stream).forEach(this::setMissingSpellFields);
	}

	private void replaceDummyEffects(Spell spell) {
		var effectApplication = spell.getEffectApplication();

		if (effectApplication == null) {
			return;
		}

		var effectId = effectApplication.effect().getEffectId();
		var phaseId = spell.getTimeRestriction().getUniqueVersion().getLastPhase();
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
		var phaseId = timeRestriction.getUniqueVersion().getLastPhase();
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
				abilitiesByClass.computeIfAbsent(characterClassId, x -> new ArrayList<>()).add(ability);
			}

			addEntry(abilitiesByRankedId, ability.getRankedAbilityId(), ability);
		}
		addEntry(spellsById, spell.getId(), spell);
	}

	public void addEffect(Effect effect) {
		validateEffect(effect);
		addEntry(effectById, effect.getEffectId(), effect);
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

		addEntry(talentsByClass, talent.getCharacterClass(), talent);
		addEntry(talentByClassByIdByRank, key1, talent);
		addEntry(talentByClassByCalcPosByRank, key2, talent);

		addEffect(talent.getEffect());
	}

	public void addBuff(Buff buff) {
		addEntry(buffsById, buff.getId(), buff);
		buffs.add(buff);
	}
}

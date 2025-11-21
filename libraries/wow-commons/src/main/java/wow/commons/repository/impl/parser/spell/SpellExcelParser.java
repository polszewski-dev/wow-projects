package wow.commons.repository.impl.parser.spell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.SpellTargetType.TARGET;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.*;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class SpellExcelParser extends ExcelParser {
	@Value("${spells.xls.file.path}")
	private final String xlsFilePath;

	private final PhaseMap<SpellId, Spell> spellsById = new PhaseMap<>();
	private final PhaseMap<EffectId, Effect> effectById = new PhaseMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new AbilitySheetParser(ABILITIES, this),
				new SpellSheetParser(ABILITY_SPELLS, this),
				new SpellEffectSheetParser(ABILITY_EFFECTS, this, MAX_ABILITY_EFFECT_MODIFIER_ATTRIBUTES, MAX_ABILITY_EFFECT_EVENTS),
				new SpellSheetParser(ITEM_SPELLS, this),
				new SpellEffectSheetParser(ITEM_EFFECTS, this, MAX_ITEM_EFFECT_MODIFIER_ATTRIBUTES, MAX_ITEM_EFFECT_EVENTS),
				new SpellSheetParser(TALENT_SPELLS, this),
				new SpellEffectSheetParser(TALENT_EFFECTS, this, MAX_TALENT_EFFECT_MODIFIER_ATTRIBUTES, MAX_TALENT_EFFECT_EVENTS),
				new RacialEffectSheetParser(RACIAL_EFFECTS, this, MAX_RACIAL_EFFECT_MODIFIER_ATTRIBUTES, MAX_RACIAL_EFFECT_EVENTS),
				new SpellEffectSheetParser(BUFF_EFFECTS, this, MAX_BUFF_EFFECT_MODIFIER_ATTRIBUTES, MAX_BUFF_EFFECT_EVENTS),
				new SpellSheetParser(CONSUME_SPELLS, this),
				new SpellEffectSheetParser(CONSUME_EFFECTS, this, MAX_CONSUME_EFFECT_MODIFIER_ATTRIBUTES, MAX_CONSUME_EFFECT_EVENTS)
		);
	}

	void addSpell(Spell spell) {
		validateSpell(spell);
		putForEveryPhase(spellsById, spell.getId(), spell);
	}

	void addEffect(Effect effect) {
		validateEffect(effect);
		putForEveryPhase(effectById, effect.getId(), effect);
	}

	private void validateSpell(Spell spell) {
		if (!(spell instanceof Ability ability)) {
			return;
		}

		if (ability.isChanneled() && !ability.getCastTime().isZero()) {
			throw new IllegalArgumentException("Channeled ability with non-zero cast time: " + ability);
		}

		var effectApplication = ability.getEffectApplication();

		if (effectApplication != null) {
			var effectTarget = effectApplication.target();
			var effect = effectApplication.effect();

			if (ability.isChanneled() && effectTarget.isAoE() && !effectTarget.hasType(GROUND)) {
				throw new IllegalArgumentException("Channeled ability with AoE effect target: " + ability);
			}

			if (effect.hasPeriodicComponent()) {
				var periodicComponentTarget = effect.getPeriodicComponent().target();

				if (effectTarget.hasType(GROUND) && periodicComponentTarget.hasType(TARGET)) {
					throw new IllegalArgumentException("Ground effect has no target specified");
				}
			}
		}
	}

	private void validateEffect(Effect effect) {
		if (effect.hasAugmentedAbilities() &&
				(effect.hasPeriodicComponent() || effect.hasAbsorptionComponent())) {
			throw new IllegalArgumentException();
		}

		if (effect.hasPeriodicComponent()) {
			Objects.requireNonNull(effect.getTickInterval());
		}
	}

	public List<Spell> getSpells() {
		spellsById.allValues().forEach(this::replaceDummyEffects);
		spellsById.allValues().forEach(this::setMissingSpellFields);
		return spellsById.allValues();
	}

	public List<Effect> getEffects() {
		effectById.allValues().forEach(this::replaceDummySpells);
		return effectById.allValues();
	}

	private void replaceDummyEffects(Spell spell) {
		var effectApplication = spell.getEffectApplication();

		if (effectApplication == null) {
			return;
		}

		var effectId = effectApplication.effect().getId();
		var phaseId = spell.getEarliestPhaseId();
		var effect = effectById.getOptional(phaseId, effectId).orElseThrow();
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
		var spell = spellsById.getOptional(phaseId, spellId).orElseThrow();

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
}

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
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.ComponentCommand;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.SpellTargetType.TARGET;
import static wow.commons.model.spell.component.ComponentCommand.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetConfigs.*;
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
				new AbilitySheetParser(ABILITIES, this, ABILITIES_CONFIG),
				new SpellSheetParser(ABILITY_SPELLS, this, ABILITY_SPELLS_CONFIG),
				new SpellEffectSheetParser(ABILITY_EFFECTS, this, ABILITY_EFFECTS_CONFIG),
				new SpellSheetParser(ITEM_SPELLS, this, ITEM_SPELLS_CONFIG),
				new SpellEffectSheetParser(ITEM_EFFECTS, this, ITEM_EFFECTS_CONFIG),
				new SpellSheetParser(TALENT_SPELLS, this, TALENT_SPELLS_CONFIG),
				new SpellEffectSheetParser(TALENT_EFFECTS, this, TALENT_EFFECTS_CONFIG),
				new RacialEffectSheetParser(RACIAL_EFFECTS, this, RACIAL_EFFECTS_CONFIG),
				new SpellEffectSheetParser(BUFF_EFFECTS, this, BUFF_EFFECTS_CONFIG),
				new SpellSheetParser(CONSUME_SPELLS, this, CONSUME_SPELLS_CONFIG),
				new SpellEffectSheetParser(CONSUME_EFFECTS, this, CONSUME_EFFECTS_CONFIG)
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

		for (var applyEffectCommand : ability.getApplyEffectCommands()) {
			var effectTarget = applyEffectCommand.target();
			var effect = applyEffectCommand.effect();

			if (ability.isChanneled() && effectTarget.isAoE() && !effectTarget.hasType(GROUND)) {
				throw new IllegalArgumentException("Channeled ability with AoE effect target: " + ability);
			}

			for (var periodicCommand : effect.getPeriodicCommands()) {
				var commandTarget = periodicCommand.target();

				if (effectTarget.hasType(GROUND) && commandTarget.hasType(TARGET)) {
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

		var newCommands = effectApplication.commands().stream()
				.map(command -> replaceDummyEffect(command, spell))
				.toList();

		var newEffectApplication = new EffectApplication(newCommands);

		((SpellImpl) spell).setEffectApplication(newEffectApplication);
	}

	private ApplyEffect replaceDummyEffect(ApplyEffect command, Spell spell) {
		var effectId = command.effect().getId();
		var phaseId = spell.getEarliestPhaseId();
		var effect = effectById.getOptional(phaseId, effectId).orElseThrow();

		return new ApplyEffect(
				command.target(),
				command.condition(),
				effect,
				command.duration(),
				command.numStacks(),
				command.numCharges(),
				command.replacementMode()
		);
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
		var hasDamagingComponent = hasDamagingComponent(spell);
		var hasHealingComponent = hasHealingComponent(spell);
		var spellImpl = (SpellImpl) spell;

		spellImpl.setSchool(school);
		spellImpl.setHasDamagingComponent(hasDamagingComponent);
		spellImpl.setHasHealingComponent(hasHealingComponent);
	}

	private boolean hasDamagingComponent(Spell spell) {
		var commands = getComponentCommands(spell);

		return commands.stream()
				.anyMatch(x -> x instanceof DealDamageDirectly || x instanceof DealDamagePeriodically);
	}

	private boolean hasHealingComponent(Spell spell) {
		var commands = getComponentCommands(spell);

		return commands.stream()
				.anyMatch(x -> x instanceof HealDirectly || x instanceof HealPeriodically);
	}

	private Optional<SpellSchool> getSpellSchool(Spell spell) {
		var commands = getComponentCommands(spell);

		var result = new HashSet<SpellSchool>();

		for (var command : commands) {
			switch (command) {
				case DirectCommand c ->
						result.add(c.school());

				case PeriodicCommand c ->
						result.add(c.school());

				default -> {
					// void
				}
			}
		}

		result.remove(null);

		return CollectionUtil.getUniqueResult(List.copyOf(result));
	}

	private List<ComponentCommand> getComponentCommands(Spell spell) {
		var result = new ArrayList<ComponentCommand>();

		result.addAll(spell.getDirectCommands());
		result.addAll(getPeriodicCommands(spell));
		return result;
	}

	private List<PeriodicCommand> getPeriodicCommands(Spell spell) {
		return spell.getApplyEffectCommands().stream()
				.map(ApplyEffect::effect)
				.map(Effect::getPeriodicCommands)
				.flatMap(List::stream)
				.toList();
	}
}

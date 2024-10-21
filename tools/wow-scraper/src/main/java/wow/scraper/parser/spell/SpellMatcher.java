package wow.scraper.parser.spell;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.*;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.spell.component.DirectComponentBonus;
import wow.commons.model.spell.impl.AbilityImpl;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.model.spell.impl.TriggeredSpellImpl;
import wow.commons.util.CollectionUtil;
import wow.scraper.parser.scraper.ScraperMatcher;
import wow.scraper.parser.scraper.ScraperPatternParams;
import wow.scraper.parser.spell.params.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public abstract class SpellMatcher<P extends SpellPattern<Q>, Q extends ScraperPatternParams, N extends SpellMatcherParams> extends ScraperMatcher<P, Q, N> {
	protected SpellMatcher(P pattern) {
		super(pattern);
	}

	protected <T extends SpellImpl> T getSpell(SpellPatternParams params, Supplier<T> constructor) {
		if (params == null) {
			return null;
		}

		var spell = constructor.get();
		initSpell(spell, params);
		return spell;
	}

	protected <T extends SpellImpl> void initSpell(T spell, SpellPatternParams params) {
		if (params == null) {
			return;
		}

		if (spell instanceof AbilityImpl ability) {
			ability.setCastInfo(getCastInfo(params));
			ability.setRequiredEffect(params.requiredEffect());
			ability.setEffectRemovedOnHit(params.effectRemovedOnHit());
		}
		spell.setDirectComponents(getDirectComponents(params));
		spell.setConversion(getConversion(params));
		spell.setEffectApplication(getEffectApplication(params));
	}

	private CastInfo getCastInfo(SpellPatternParams params) {
		var castParams = params.cast();

		if (castParams == null) {
			return null;
		}

		var castTime = getOptionalDuration(castParams.castTime()).orElseThrow();
		var channeled = castParams.channeled();
		boolean ignoresGcd = castParams.ignoresGcd();

		return new CastInfo(castTime, channeled, ignoresGcd);
	}

	private List<DirectComponent> getDirectComponents(SpellPatternParams params) {
		return params.directComponents().stream()
				.map(this::getDirectComponent)
				.toList();
	}

	private DirectComponent getDirectComponent(DirectComponentParams params) {
		var target = params.target();
		var type = params.type();
		var coefficient = params.coefficient();
		var min = getOptionalInteger(params.min()).orElseThrow();
		var max = getOptionalInteger(params.max()).orElseThrow();
		var bonus = getDirectComponentBonus(params.bonus());
		var bolt = params.bolt();

		return new DirectComponent(target, type, coefficient, min, max, bonus, bolt);
	}

	private DirectComponentBonus getDirectComponentBonus(DirectComponentBonusParams params) {
		if (params == null) {
			return null;
		}

		var min = getOptionalInteger(params.min()).orElse(0);
		var max = getOptionalInteger(params.max()).orElse(0);
		var requiredEffect = params.requiredEffect();

		return new DirectComponentBonus(min, max, requiredEffect);
	}

	protected Effect getEffect(EffectPatternParams params) {
		return getEffect(params, (eventParams, event) -> event);
	}

	protected Effect getEffect(EffectPatternParams params, BiFunction<EventParams, Event, Event> eventMapper) {
		if (params == null) {
			return null;
		}

		var augmentedAbility = params.augmentedAbility();
		var maxStacks = getOptionalInteger(params.maxStacks()).orElse(1);
		var periodicComponent = getPeriodicComponent(params);
		var tickInterval = getTickInterval(params);
		var modifierComponent = getModifierComponent(params);
		var absorptionComponent = getAbsorptionComponent(params);
		var conversion = getConversion(params);
		var statConversions = getStatConversions(params);
		var events = getEvents(params, eventMapper);

		var effect = new EffectImpl(augmentedAbility);

		effect.setMaxStacks(maxStacks);
		effect.setPeriodicComponent(periodicComponent);
		effect.setTickInterval(tickInterval);
		effect.setModifierComponent(modifierComponent);
		effect.setAbsorptionComponent(absorptionComponent);
		effect.setConversion(conversion);
		effect.setStatConversions(statConversions);
		effect.setEvents(events);

		return effect;
	}

	private PeriodicComponent getPeriodicComponent(EffectPatternParams params) {
		var periodicComponentParams = params.periodicComponent();

		if (periodicComponentParams == null) {
			return null;
		}

		var type = periodicComponentParams.type();
		var tickInterval = getTickInterval(params);
		var duration = getOptionalDuration(params.duration()).orElseThrow();
		var numTicks = getNumTicks(duration, tickInterval);
		var coefficient = periodicComponentParams.coefficient();
		var amount = getTotalAmount(periodicComponentParams, numTicks);
		var tickScheme = getTickScheme(periodicComponentParams.tickWeights());

		return new PeriodicComponent(type, coefficient, amount, numTicks, tickScheme);
	}

	private int getNumTicks(Duration duration, Duration tickInterval) {
		var numTicks = (int) duration.divideBy(tickInterval);

		if (duration.divideBy(tickInterval) % 1 != 0) {
			throw new IllegalArgumentException("Num ticks is not integer");
		}
		return numTicks;
	}

	private int getTotalAmount(PeriodicComponentParams params, int numTicks) {
		var optionalDotDmg = getOptionalInteger(params.totalAmount());
		var optionalTickDmg = getOptionalInteger(params.tickAmount());

		if (optionalDotDmg.isPresent() && optionalTickDmg.isEmpty()) {
			return optionalDotDmg.get();
		} else if (optionalDotDmg.isEmpty() && optionalTickDmg.isPresent()) {
			return optionalTickDmg.get() * numTicks;
		} else {
			throw new IllegalArgumentException();
		}
	}

	private TickScheme getTickScheme(String string) {
		if (string == null || string.isEmpty()) {
			return TickScheme.DEFAULT;
		}
		var tickWeights = Stream.of(string.split(",")).map(Double::parseDouble).toList();

		return new TickScheme(tickWeights);
	}

	private ModifierComponent getModifierComponent(EffectPatternParams params) {
		var modifierComponentParams = params.modifierComponent();

		if (modifierComponentParams == null) {
			return null;
		}

		var attributes = getAttributes(modifierComponentParams.attributes());

		return new ModifierComponent(attributes);
	}

	private AbsorptionComponent getAbsorptionComponent(EffectPatternParams params) {
		var absorptionComponentParams = params.absorptionComponent();

		if (absorptionComponentParams == null) {
			return null;
		}

		var coefficient = absorptionComponentParams.coefficient();
		var condition = absorptionComponentParams.condition();
		var min = getOptionalInteger(absorptionComponentParams.min()).orElseThrow();
		var max = getOptionalInteger(absorptionComponentParams.max()).orElseThrow();

		return new AbsorptionComponent(coefficient, condition, min, max);
	}

	private Duration getTickInterval(EffectPatternParams params) {
		var tickInterval = params.tickInterval();

		if (tickInterval == null) {
			return null;
		}

		return getOptionalDuration(tickInterval).orElseThrow();
	}

	private Conversion getConversion(SpellPatternParams params) {
		return getConversion(params.conversion());
	}

	private Conversion getConversion(EffectPatternParams params) {
		return getConversion(params.conversion());
	}

	private Conversion getConversion(ConversionParams conversionParams) {
		if (conversionParams == null) {
			return null;
		}

		var condition = conversionParams.condition();
		var from = conversionParams.from();
		var to = conversionParams.to();
		var ratio = getOptionalPercent(conversionParams.ratio()).orElse(Percent._100);

		return new Conversion(condition, from, to, ratio);
	}

	private List<StatConversion> getStatConversions(EffectPatternParams params) {
		return params.statConversions().stream()
				.map(this::getStatConversion)
				.toList();
	}

	private StatConversion getStatConversion(StatConversionParams params) {
		var from = params.from();
		var to = params.to();
		var toCondition = params.toCondition();
		var ratioPct = getOptionalPercent(params.ratioPct()).orElse(Percent._100);

		return new StatConversion(from, to, toCondition, ratioPct);
	}

	private List<Event> getEvents(EffectPatternParams params, BiFunction<EventParams, Event, Event> mapper) {
		return params.getEvents().stream()
				.map(eventParams -> mapper.apply(eventParams, getEvent(eventParams)))
				.toList();
	}

	private Event getEvent(EventParams params) {
		if (params == null) {
			return null;
		}

		var types = params.types();
		var condition = params.condition();
		var chance = getOptionalPercent(params.chancePct()).orElse(Percent._100);
		var actions = params.actions();
		var cooldown = getOptionalDuration(params.cooldown()).orElse(Duration.ZERO);
		var triggeredSpell = getSpell(params.triggeredSpell(), TriggeredSpellImpl::new);

		return new Event(types, condition, chance, actions, cooldown, triggeredSpell);
	}

	private EffectApplication getEffectApplication(SpellPatternParams params) {
		EffectApplicationParams effectApplication = params.effectApplication();

		if (effectApplication == null) {
			return null;
		}

		var target = getEffectTarget(effectApplication.effect()).orElseThrow();
		var effect = getEffect(effectApplication.effect());
		var duration = getOptionalDuration(effectApplication.duration()).orElseThrow();
		var numStacks = getOptionalInteger(effectApplication.numStacks()).orElse(1);
		var numCharges = getOptionalInteger(effectApplication.numCharges()).orElse(1);

		return new EffectApplication(
				target,
				effect,
				duration,
				numStacks,
				numCharges
		);
	}

	private Optional<SpellTarget> getEffectTarget(EffectPatternParams effect) {
		var result = new HashSet<SpellTarget>();

		if (effect.periodicComponent() != null) {
			result.add(effect.periodicComponent().target());
		}
		if (effect.modifierComponent() != null) {
			result.add(effect.modifierComponent().target());
		}
		if (effect.absorptionComponent() != null) {
			result.add(effect.absorptionComponent().target());
		}
		if (effect.conversion() != null) {
			result.add(effect.conversion().target());
		}
		effect.statConversions().forEach(statConversion -> result.add(statConversion.target()));
		effect.getEvents().forEach(event -> result.add(event.target()));
		return result.stream().collect(CollectionUtil.toOptionalSingleton());
	}
}

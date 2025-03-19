package wow.evaluator.util;

import wow.character.model.equipment.EquippableItem;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.config.Described;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ActivatedAbility;
import wow.evaluator.model.AttributesDiff;
import wow.evaluator.model.EffectList;
import wow.evaluator.model.Player;
import wow.evaluator.model.SpecialAbility;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public class AttributesDiffFinder {
	private final Player player;
	private final EffectList reference;
	private final EffectList equipped;

	private final Map<String, AttributeValueAccumulator> accumulators = new HashMap<>();

	private final List<Attribute> attributes = new ArrayList<>();
	private final List<SpecialAbility> addedAbilities = new ArrayList<>();
	private final List<SpecialAbility> removedAbilities = new ArrayList<>();

	public AttributesDiffFinder(Player referencePlayer, ItemSlotGroup slotGroup, List<EquippableItem> itemOption) {
		this.player = referencePlayer;

		var equippedCharacter = referencePlayer.copy();
		equippedCharacter.getEquipment().equip(slotGroup, itemOption.toArray(EquippableItem[]::new));

		this.reference = new EffectList(referencePlayer);
		this.equipped = new EffectList(equippedCharacter);

		referencePlayer.getEquipment().collectEffects(reference);
		equippedCharacter.getEquipment().collectEffects(equipped);

		reference.collectRest();
		equipped.collectRest();
	}

	public AttributesDiff getDiff() {
		modifierDiff();
		effectDiff();
		activatedAbilityDiff();
		sortResults();

		return new AttributesDiff(
				Attributes.of(attributes),
				addedAbilities,
				removedAbilities
		);
	}

	private void modifierDiff() {
		var equippedAttributes = getModifierAttributes(equipped);
		var referenceAttributes = getModifierAttributes(reference);

		for (var attribute : equippedAttributes) {
			getAccumulator(attribute).add(attribute, player);
		}

		for (var attribute : referenceAttributes) {
			getAccumulator(attribute).subtract(attribute, player);
		}

		for (var accumulator : accumulators.values()) {
			var attribute = accumulator.getResult();
			if (attribute.value() != 0) {
				attributes.add(attribute);
			}
		}
	}

	private List<Attribute> getModifierAttributes(EffectList list) {
		return list.getEffects().stream()
				.filter(Effect::hasModifierComponentOnly)
				.map(Effect::getModifierAttributeList)
				.flatMap(Collection::stream)
				.toList();
	}

	private void effectDiff() {
		var addedEffects = getEfectDiff(equipped, reference);
		var removedEffects = getEfectDiff(reference, equipped);

		addedEffects.stream()
				.map(SpecialAbility::of)
				.forEach(addedAbilities::add);

		removedEffects.stream()
				.map(SpecialAbility::of)
				.forEach(removedAbilities::add);
	}

	private void activatedAbilityDiff() {
		getActivatedAbilityDiff(equipped, reference).stream()
				.map(SpecialAbility::of)
				.forEach(addedAbilities::add);

		getActivatedAbilityDiff(reference, equipped).stream()
				.map(SpecialAbility::of).
				forEach(removedAbilities::add);
	}

	private Set<Effect> getEfectDiff(EffectList list1, EffectList list2) {
		var set = new HashSet<Effect>();

		list1.getEffects().stream().filter(x -> !x.hasModifierComponentOnly()).forEach(set::add);
		list2.getEffects().stream().filter(x -> !x.hasModifierComponentOnly()).forEach(set::remove);

		return set;
	}

	private Set<ActivatedAbility> getActivatedAbilityDiff(EffectList list1, EffectList list2) {
		var set = new HashSet<>(list1.getActivatedAbilities());

		for (var activatedAbility : list2.getActivatedAbilities()) {
			set.remove(activatedAbility);
		}

		return set;
	}

	private AttributeValueAccumulator getAccumulator(Attribute attribute) {
		String key = attribute.id() + "#" + attribute.condition();
		return accumulators.computeIfAbsent(key, x -> new AttributeValueAccumulator(attribute));
	}

	private static class AttributeValueAccumulator {
		private final AttributeId id;
		private final AttributeCondition condition;
		private double value;

		AttributeValueAccumulator(Attribute prototype) {
			this.id = prototype.id();
			this.condition = prototype.condition();
		}

		void add(Attribute attribute, Player player) {
			this.value += attribute.getScaledValue(player);
		}

		void subtract(Attribute attribute, Player player) {
			this.value -= attribute.getScaledValue(player);
		}

		Attribute getResult() {
			return Attribute.of(id, value, condition);
		}
	}

	private void sortResults() {
		attributes.sort(Comparator
								.comparing(Attribute::id)
								.thenComparing(x -> x.condition().toString())
		);
		addedAbilities.sort(Comparator.comparing(Described::getTooltip));
		removedAbilities.sort(Comparator.comparing(Described::getTooltip));
	}
}

package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatEquivalentProvider;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.ItemDataRepository;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.AttributesBuilder;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;
import wow.minmax.service.SpellService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.Map;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_DAMAGE;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class UpgradeServiceImpl implements UpgradeService {
	private final ItemDataRepository itemDataRepository;
	private final SpellService spellService;
	private final ItemService itemService;
	private final CalculationService calculationService;

	@Override
	public List<Comparison> findUpgrades(PlayerProfile playerProfile, ItemSlotGroup slotGroup, SpellId spellId) {
		PlayerProfile playerProfileCopy = playerProfile.copy();
		Spell spell = spellService.getSpell(spellId);
		double referenceDps = calculationService.getSpellStatistics(playerProfile, spell).getDps();

		for (ItemSlot slot : slotGroup.getSlots()) {
			playerProfileCopy.getEquipment().set(null, slot);
		}

		Attributes withoutSlotGroup = AttributeEvaluator.of(spell.getSpellInfo())
				.addAttributes(playerProfileCopy)
				.nothingToSolve()
				.getAttributes()
				;

		return new ItemVariantEnumerator(itemService) {
			@Override
			protected Comparison getItemScore(EquippableItem... itemOption) {
				AttributeEvaluator evaluator = AttributeEvaluator.of(spell.getSpellInfo());

				evaluator.addAttributes(withoutSlotGroup);

				for (EquippableItem item : itemOption) {
					item.collectAttributes(evaluator);
				}

				Attributes totalStats = evaluator
						.solveAllLeaveAbilities()
						.getAttributes();

				double dps = calculationService.getSpellStatistics(playerProfileCopy, spell, totalStats).getDps();
				double changePct = 100 * (dps / referenceDps - 1);

				if (changePct > 0) {
					for (int i = 0; i < itemOption.length; i++) {
						EquippableItem item = itemOption[i];
						ItemSlot slot = slotGroup.getSlots().get(i);
						playerProfileCopy.getEquipment().set(item, slot);
					}

					return new Comparison(playerProfileCopy.getEquipment().copy(), playerProfile.getEquipment(), Percent.of(changePct));
				}

				return null;
			}

			@Override
			protected Map<ItemType, List<Item>> getItemsByType(PlayerProfile playerProfile, Spell spell) {
				return itemDataRepository.getCasterItemsByType(
						playerProfile.getCharacterInfo(), playerProfile.getPhase(), spell.getSpellSchool());
			}
		}.run(slotGroup, playerProfile, spell)
		.getResult();
	}

	@Override
	public EquippableItem getBestItemVariant(PlayerProfile playerProfile, Item item, ItemSlot slot, SpellId spellId) {
		PlayerProfile playerProfileCopy = playerProfile.copy();
		Spell spell = spellService.getSpell(spellId);
		double referenceDps = calculationService.getSpellStatistics(playerProfile, spell).getDps();

		return new ItemVariantEnumerator(itemService) {
			@Override
			protected Comparison getItemScore(EquippableItem... itemOption) {
				EquippableItem itemVariant = itemOption[0];
				playerProfileCopy.getEquipment().set(itemVariant, slot);

				double dps = calculationService.getSpellStatistics(playerProfileCopy, spell).getDps();
				double changePct = 100 * (dps / referenceDps - 1);

				return new Comparison(playerProfileCopy.getEquipment().copy(), playerProfile.getEquipment(), Percent.of(changePct));
			}

			@Override
			protected Map<ItemType, List<Item>> getItemsByType(PlayerProfile playerProfile, Spell spell) {
				return Map.of(item.getItemType(), List.of(item));
			}
		}
		.run(ItemSlotGroup.valueOf(slot.name()), playerProfileCopy, spell)
		.getResult()
		.get(0)
		.getPossibleEquipment().get(slot);
	}

	@Override
	public double getItemScore(Item item, SpellSchool spellSchool) {
		Attributes statTotal = getStatTotal(item);

		return statTotal.getTotalSpellDamage(spellSchool) +
				statTotal.getSpellCritRating() +
				statTotal.getSpellHitRating() +
				statTotal.getSpellHasteRating();
	}

	private Attributes getStatTotal(Item item) {
		AttributesBuilder builder = new AttributesBuilder();

		builder.addAttributes(item.getAttributes());

		if (item.hasSockets()) {
			for (int i = 1; i <= item.getSocketCount(); ++i) {
				if (item.getSocketType(i) == SocketType.META) {
					builder.addAttribute(SPELL_DAMAGE, 50);
				} else {
					builder.addAttribute(SPELL_DAMAGE, 9);
				}
			}

			if (item.getSocketSpecification().getSocketTypes().stream().noneMatch(x -> x == SocketType.BLUE)) {
				builder.addAttributes(item.getSocketSpecification().getSocketBonus());
			}
		}

		for (ComplexAttribute attribute : item.getAttributes().getList(ComplexAttributeId.values())) {
			if (attribute instanceof StatEquivalentProvider) {
				StatEquivalentProvider statEquivalentProvider = (StatEquivalentProvider) attribute;
				Attributes statEquivalent = statEquivalentProvider.getStatEquivalent(getStatProviderForItemScore());
				builder.addAttributes(statEquivalent);
			}
		}

		return builder.toAttributes();
	}

	private static StatProvider getStatProviderForItemScore() {
		return StatProvider.fixedValues(0.99, 0.30, Duration.seconds(2.5));
	}
}

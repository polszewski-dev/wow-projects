package wow.character.model.character;

import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.Talent;

import java.util.ArrayList;
import java.util.List;

import static wow.character.model.character.TestEvent.*;

/**
 * User: POlszewski
 * Date: 2026-11-25
 */
public class TestPlayerCharacter extends PlayerCharacterImpl {
	public TestPlayerCharacter(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, race, level, baseStatInfo, combatRatingInfo, availableTalents, professions, exclusiveFactions);
	}

	public List<TestEvent> events = new ArrayList<>();
	
	private void addEvent(TestEvent event) {
		events.add(event);
	}

	@Override
	public void itemStatsAdded(Item item) {
		super.itemStatsAdded(item);
		addEvent(new ItemStatsAdded(item));
	}

	@Override
	public void itemStatsRemoved(Item item) {
		super.itemStatsRemoved(item);
		addEvent(new ItemStatsRemoved(item));
	}

	@Override
	public void itemSetBonusAdded(ItemSetBonus setBonus) {
		super.itemSetBonusAdded(setBonus);
		addEvent(new ItemSetBonusAdded(setBonus));
	}

	@Override
	public void itemSetBonusRemoved(ItemSetBonus setBonus) {
		super.itemSetBonusRemoved(setBonus);
		addEvent(new ItemSetBonusRemoved(setBonus));
	}

	@Override
	public void enchantStatsAdded(Enchant enchant) {
		super.enchantStatsAdded(enchant);
		addEvent(new EnchantStatsAdded(enchant));
	}

	@Override
	public void enchantStatsRemoved(Enchant enchant) {
		super.enchantStatsRemoved(enchant);
		addEvent(new EnchantStatsRemoved(enchant));
	}

	@Override
	public void gemStatsAdded(Gem gem) {
		super.gemStatsAdded(gem);
		addEvent(new GemStatsAdded(gem));
	}

	@Override
	public void gemStatsRemoved(Gem gem) {
		super.gemStatsRemoved(gem);
		addEvent(new GemStatsRemoved(gem));
	}

	@Override
	public void socketBonusAdded(Effect socketBonus) {
		super.socketBonusAdded(socketBonus);
		addEvent(new SocketBonusAdded(socketBonus));
	}

	@Override
	public void socketBonusRemoved(Effect socketBonus) {
		super.socketBonusRemoved(socketBonus);
		addEvent(new SocketBonusRemoved(socketBonus));
	}
}

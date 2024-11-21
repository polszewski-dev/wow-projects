package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.build.Build;
import wow.character.model.character.CharacterProfessions;
import wow.character.model.character.Consumables;
import wow.character.model.character.ExclusiveFactions;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.Race;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player {
	public PlayerImpl(String name, PlayerCharacter character) {
		super(name, character);
		this.resources.setHealth(1_000_000_000, 1_000_000_000);
	}

	private PlayerCharacter getCharacter() {
		return (PlayerCharacter) character;
	}

	@Override
	public Equipment getEquipment() {
		return getCharacter().getEquipment();
	}

	@Override
	public Race getRace() {
		return getCharacter().getRace();
	}

	@Override
	public CharacterProfessions getProfessions() {
		return getCharacter().getProfessions();
	}

	@Override
	public Build getBuild() {
		return getCharacter().getBuild();
	}

	@Override
	public ExclusiveFactions getExclusiveFactions() {
		return getCharacter().getExclusiveFactions();
	}

	@Override
	public Consumables getConsumables() {
		return getCharacter().getConsumables();
	}
}

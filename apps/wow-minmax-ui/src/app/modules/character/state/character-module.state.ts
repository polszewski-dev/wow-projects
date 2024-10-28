import { CharacterState } from "./character/character.reducer";
import { EquipmentOptionsState } from "./equipment-options/equipment-options.reducer";
import { UpgradesState } from "./upgrades/upgrades.reducer";

export interface CharacterModuleState {
	character: CharacterState;
	equipmentOptions: EquipmentOptionsState;
	upgrades: UpgradesState;
}

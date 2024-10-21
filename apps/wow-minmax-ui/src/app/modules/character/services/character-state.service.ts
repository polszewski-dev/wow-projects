import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, filter, map } from 'rxjs';
import { Character } from '../model/Character';
import { Buff } from '../model/buff/Buff';
import { BuffListType } from '../model/buff/BuffListType';
import { Enchant } from '../model/equipment/Enchant';
import { Equipment } from '../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../model/equipment/EquippableItem';
import { Gem } from '../model/equipment/Gem';
import { Item } from '../model/equipment/Item';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { ItemType } from '../model/equipment/ItemType';
import { ItemSlotGroup, getSlots } from '../model/upgrade/ItemSlotGroup';
import { BuffService } from './buff.service';
import { CharacterService } from './character.service';
import { EquipmentOptionsStateService } from './equipment-options-state.service';
import { EquipmentService } from './equipment.service';
import { UpgradeStateService } from './upgrade-state.service';

@Injectable({
	providedIn: 'root'
})
export class CharacterStateService {
	private readonly characterSubject = new BehaviorSubject<Character | undefined>(undefined);
	private readonly equipmentSubject = new BehaviorSubject<Equipment | undefined>(undefined);
	private readonly itemSlotSubjects = this.createItemSlotSubjects();
	private readonly socketStatusSubject = new BehaviorSubject<EquipmentSocketStatus | undefined>(undefined);
	private readonly buffListSubjects = this.createBuffListSubjects();

	readonly character$ = this.characterSubject.asObservable();
	readonly equipment$ = this.equipmentSubject.asObservable();
	readonly socketStatus$ = this.socketStatusSubject.asObservable();

	readonly characterStatChange$ = combineLatest([
		this.character$,
		this.equipment$,
		...Object.values(this.buffListSubjects)
	]).pipe(
		filter(all => all.every(x => !!x)),
		map(([character, _]) => character as Character)
	);

	itemSlotByType$(itemSlot: ItemSlot) {
		return this.itemSlotSubjects[itemSlot].asObservable();
	}

	buffListByType$(buffListType: BuffListType) {
		return this.buffListSubjects[buffListType].asObservable();
	}

	private characterId: string | undefined;
	private character: Character | undefined;
	private equipment: Equipment | undefined;

	get characterSnapshot() {
		return this.character;
	}

	get equipmentSnapshot() {
		return this.equipment;
	}

	constructor(
		private characterService: CharacterService,
		private equipmentService: EquipmentService,
		private buffService: BuffService,
		private equipmentOptionsStateService: EquipmentOptionsStateService,
		private upgradeStateService: UpgradeStateService
	) {}

	selectCharacter(characterId: string) {
		this.characterId = characterId;
		this.character = undefined;
		this.equipment = undefined;

		this.characterSubject.next(undefined);
		this.equipmentSubject.next(undefined);
		Object.values(this.buffListSubjects).forEach(x => x.next(undefined));

		this.equipmentOptionsStateService.clearOptions();
		this.upgradeStateService.clearUpgrades();

		this.characterService.getCharacter(characterId).subscribe(character => {
			this.character = character;
			this.characterSubject.next(character);
		});

		this.equipmentService.getEquipment(characterId).subscribe(equipment => {
			this.setEquipment(equipment);
		});

		this.updateSocketStatus();
		this.updateUpgrades();

		Object.entries(this.buffListSubjects).forEach(entry => {
			const buffListType = entry[0] as BuffListType;
			const subject = entry[1];
			this.buffService.getBuffs(characterId, buffListType).subscribe(buffs => {
				subject.next(buffs);
			});
		});

		this.equipmentOptionsStateService.loadOptions(characterId);
	}

	equipItemBestVariant(itemSlot: ItemSlot, item: Item) {
		this.equipmentService.equipItemBestVariant(this.characterId!, itemSlot, item).subscribe(item => {
			this.setEquippedItem(itemSlot, item);
			this.updateUpgrades();
			this.updateSocketStatus();
		});
	}

	equipEnchant(itemSlot: ItemSlot, enchant: Enchant) {
		const item = this.equipment!.itemsBySlot[itemSlot]!;
		const newItem: EquippableItem = {
			...item,
			enchant: enchant
		};

		this.equipItem(itemSlot, newItem, () => {
			this.updateUpgrades();
		});
	}

	equipGem(itemSlot: ItemSlot, socketNo: number, gem: Gem) {
		const item = this.equipment!.itemsBySlot[itemSlot]!;
		const newItem: EquippableItem = {
			...item,
			gems: [...item.gems]
		};

		newItem.gems[socketNo] = gem;

		this.equipItem(itemSlot, newItem, () => {
			this.updateSocketStatus();
			this.updateUpgrades();
		});
	}

	equipItemGroup(slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		this.equipmentService.equipItems(this.characterId!, slotGroup, items).subscribe(() => {
			const itemSlots = getSlots(slotGroup);

			for (let i = 0; i < itemSlots.length; ++i) {
				this.equipment!.itemsBySlot[itemSlots[i]] = items[i];
			}

			this.equipmentSubject.next(this.equipment);
			for (const itemSlot of itemSlots) {
				this.itemSlotSubjects[itemSlot].next(this.equipment!.itemsBySlot[itemSlot]);
			}

			this.updateSocketStatus();
			this.updateUpgrades();
		});
	}

	resetEquipment() {
		this.equipmentService.resetEquipment(this.characterId!).subscribe(() => {
			this.setEquipment({
				itemsBySlot: {}
			});
			this.socketStatusSubject.next(undefined);
			this.updateUpgrades();
		});
	}

	private setEquipment(equipment: Equipment) {
		this.equipment = equipment;
		this.equipmentSubject.next(equipment);
		Object.entries(this.itemSlotSubjects).forEach(entry => {
			const itemSlot = entry[0] as ItemSlot;
			const subject = entry[1];
			subject.next(equipment.itemsBySlot[itemSlot]);
		});
	}

	private equipItem(itemSlot: ItemSlot, item: EquippableItem | undefined, afterChange: () => void) {
		this.equipmentService.equipItem(this.characterId!, itemSlot, item).subscribe(() => {
			this.setEquippedItem(itemSlot, item);
			afterChange();
		});
	}

	private setEquippedItem(itemSlot: ItemSlot, item: EquippableItem | undefined) {
		const removingOffHand = itemSlot === ItemSlot.MAIN_HAND && item?.item.itemType === ItemType.TWO_HAND;

		this.equipment!.itemsBySlot[itemSlot] = item;
		if (removingOffHand) {
			this.equipment!.itemsBySlot[ItemSlot.OFF_HAND] = undefined;
		}

		this.equipmentSubject.next(this.equipment);

		this.itemSlotSubjects[itemSlot].next(item);
		if (removingOffHand) {
			this.itemSlotSubjects[ItemSlot.OFF_HAND].next(undefined);
		}
	}

	enableBuff(buffListType: BuffListType, buff: Buff) {
		this.buffService.enableBuff(this.characterId!, buffListType, buff).subscribe(buffs => {
			this.buffListSubjects[buffListType].next(buffs);
			this.updateUpgrades();
		});
	}

	private updateSocketStatus() {
		this.equipmentService.getSocketStatus(this.characterId!).subscribe(socketStatus => {
			this.socketStatusSubject.next(socketStatus);
		});
	}

	private updateUpgrades() {
		this.upgradeStateService.loadUpgrades(this.characterId!);
	}

	private createItemSlotSubjects() {
		return Object.fromEntries(
			Object.values(ItemSlot).map(k => [k as ItemSlot, new BehaviorSubject<EquippableItem | undefined>(undefined)])
		);
	}

	private createBuffListSubjects() {
		return Object.fromEntries(
			Object.values(BuffListType).map(k => [k as BuffListType, new BehaviorSubject<Buff[] | undefined>(undefined)])
		);
	}
}

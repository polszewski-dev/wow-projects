import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { catchError, filter, from, map, mergeMap, of, switchMap } from "rxjs";
import { BuffListType } from "../../model/buff/BuffListType";
import { BuffService } from '../../services/buff.service';
import { CharacterService } from '../../services/character.service';
import { ConsumableService } from "../../services/consumable.service";
import { EquipmentService } from '../../services/equipment.service';
import { loadEnchantOptions, loadEquipmentOptions, loadGemOptions, loadItemOptions } from "../equipment-options/equipment-options.actions";
import { CharacterModuleState } from './../character-module.state';
import { changeBuffStatus, changeBuffStatusFailure, changeBuffStatusSuccess, changeConsumableStatus, changeConsumableStatusFailure, changeConsumableStatusSuccess, changeProfession, changeProfessionFailure, changeProfessionSuccess, dpsChanged, equipEnchant, equipEnchantFailure, equipEnchantSuccess, equipGearSet, equipGearSetFailure, equipGearSetSuccess, equipGem, equipGemFailure, equipGemSuccess, equipItemBestVariant, equipItemBestVariantFailure, equipItemBestVariantSuccess, equipItemGroup, equipItemGroupFailure, equipItemGroupSuccess, equipPreviousPhase, equipPreviousPhaseFailure, equipPreviousPhaseSuccess, loadBuffListFailure, loadBuffListSuccess, loadBuffs, loadCharacter, loadCharacterFailure, loadCharacterSuccess, loadConsumableStatuses, loadConsumableStatusesFailure, loadConsumableStatusesSuccess, loadEquipment, loadEquipmentFailure, loadEquipmentSuccess, loadSocketStatus, loadSocketStatusFailure, loadSocketStatusSuccess, resetEquipment, resetEquipmentFailure, resetEquipmentSuccess, selectCharacter } from './character.actions';

@Injectable()
export class CharacterEffects {
	constructor(
		private actions$: Actions,
		private store: Store<CharacterModuleState>,
		private characterService: CharacterService,
		private equipmentService: EquipmentService,
		private buffService: BuffService,
		private consumableService: ConsumableService
	) {}

	selectCharacter$ = createEffect(() => this.actions$.pipe(
		ofType(selectCharacter),
		filter(({ characterId }) => !!characterId),
		switchMap(({ characterId }) => of(
			loadCharacter({ characterId: characterId! }),
			loadEquipment({ characterId: characterId! }),
			loadSocketStatus({ characterId: characterId! }),
			loadBuffs({ characterId: characterId! }),
			loadConsumableStatuses({ characterId: characterId! }),
			loadEquipmentOptions({ characterId: characterId! }),
			loadItemOptions({ characterId: characterId! }),
			loadEnchantOptions({ characterId: characterId! }),
			loadGemOptions({ characterId: characterId! })
		))
	));

	loadCharacter$ = createEffect(() => this.actions$.pipe(
		ofType(loadCharacter),
		switchMap(({ characterId }) => this.characterService.getCharacter(characterId).pipe(
			map(character => loadCharacterSuccess({ character })),
			catchError(error => of(loadCharacterFailure({ error })))
		))
	));

	loadEquipment$ = createEffect(() => this.actions$.pipe(
		ofType(loadEquipment),
		switchMap(({ characterId }) => this.equipmentService.getEquipment(characterId).pipe(
			map(equipment => loadEquipmentSuccess({ equipment })),
			catchError(error => of(loadEquipmentFailure({ error })))
		))
	));

	loadSocketStatus$ = createEffect(() => this.actions$.pipe(
		ofType(loadSocketStatus),
		switchMap(({ characterId }) => this.equipmentService.getSocketStatus(characterId).pipe(
			map(socketStatus => loadSocketStatusSuccess({ socketStatus })),
			catchError(error => of(loadSocketStatusFailure({ error })))
		))
	));

	loadBuffs$ = createEffect(() => this.actions$.pipe(
		ofType(loadBuffs),
		switchMap(({ characterId }) => from(Object.values(BuffListType)).pipe(
			mergeMap(buffListType => this.buffService.getBuffStatuses(characterId, buffListType).pipe(
				map(buffStatusList => loadBuffListSuccess({ buffListType, buffStatusList })),
				catchError(error => of(loadBuffListFailure({ buffListType, error })))
			))
		))
	));

	loadConsumables$ = createEffect(() => this.actions$.pipe(
		ofType(loadConsumableStatuses),
		switchMap(({ characterId }) => this.consumableService.getConsumableStatuses(characterId).pipe(
			map(consumableStatuses => loadConsumableStatusesSuccess({ consumableStatuses })),
			catchError(error => of(loadConsumableStatusesFailure({ error })))
		))
	));

	equipItemBestVariant$ = createEffect(() => this.actions$.pipe(
		ofType(equipItemBestVariant),
		switchMap(({ characterId, itemSlot, item }) => this.equipmentService.equipItemBestVariant(characterId, itemSlot, item).pipe(
			map(equippableItem => equipItemBestVariantSuccess({ characterId, itemSlot, equippableItem })),
			catchError(error => of(equipItemBestVariantFailure({ itemSlot, error })))
		))
	));

	equipItemGroup$ = createEffect(() => this.actions$.pipe(
		ofType(equipItemGroup),
		switchMap(({ characterId, slotGroup, items }) => this.equipmentService.equipItems(characterId, slotGroup, items).pipe(
			map(() => equipItemGroupSuccess({ characterId, slotGroup, items })),
			catchError(error => of(equipItemGroupFailure({ slotGroup, error })))
		))
	));

	equipEnchant$ = createEffect(() => this.actions$.pipe(
		ofType(equipEnchant),
		switchMap(({ characterId, equippedItem, itemSlot, enchant }) => this.equipmentService.equipEnchant(characterId, equippedItem, itemSlot, enchant).pipe(
			map(equippableItem => equipEnchantSuccess({ characterId, itemSlot, equippableItem })),
			catchError(error => of(equipEnchantFailure({ itemSlot, error })))
		))
	));

	equipGem$ = createEffect(() => this.actions$.pipe(
		ofType(equipGem),
		switchMap(({ characterId, equippedItem, itemSlot, socketNo, gem }) => this.equipmentService.equipGem(characterId, equippedItem, itemSlot, socketNo, gem).pipe(
			map(equippableItem => equipGemSuccess({ characterId, itemSlot, socketNo, equippableItem })),
			catchError(error => of(equipGemFailure({ itemSlot, socketNo, error })))
		))
	));

	resetEquipment$ = createEffect(() => this.actions$.pipe(
		ofType(resetEquipment),
		switchMap(({ characterId }) => this.equipmentService.resetEquipment(characterId).pipe(
			map(() => resetEquipmentSuccess({ characterId })),
			catchError(error => of(resetEquipmentFailure({ error })))
		))
	));

	equipGearSet$ = createEffect(() => this.actions$.pipe(
		ofType(equipGearSet),
		switchMap(({ characterId, gearSet }) => this.equipmentService.equipGearSet(characterId, gearSet).pipe(
			map(equipment => equipGearSetSuccess({ characterId, equipment })),
			catchError(error => of(equipGearSetFailure({ error })))
		))
	));

	equipPreviousPhase$ = createEffect(() => this.actions$.pipe(
		ofType(equipPreviousPhase),
		switchMap(({ characterId }) => this.equipmentService.equipPreviousPhase(characterId).pipe(
			map(equipment => equipPreviousPhaseSuccess({ characterId, equipment })),
			catchError(error => of(equipPreviousPhaseFailure({ error })))
		))
	));

	changeBuffStatus$ = createEffect(() => this.actions$.pipe(
		ofType(changeBuffStatus),
		switchMap(({ characterId, buffListType, buffStatus }) => this.buffService.changeBuffStatus(characterId, buffListType, buffStatus).pipe(
			map(buffStatusList => changeBuffStatusSuccess({ characterId, buffListType, buffStatusList })),
			catchError(error => of(changeBuffStatusFailure({ buffListType, error })))
		))
	));

	changeConsumableStatus$ = createEffect(() => this.actions$.pipe(
		ofType(changeConsumableStatus),
		switchMap(({ characterId, consumableStatus }) => this.consumableService.changeConsumableStatus(characterId, consumableStatus).pipe(
			map(consumableStatuses => changeConsumableStatusSuccess({ characterId, consumableStatuses })),
			catchError(error => of(changeConsumableStatusFailure({ error })))
		))
	));

	dpsChanged$ = createEffect(() => this.actions$.pipe(
		ofType(
			equipItemBestVariantSuccess,
			equipItemGroupSuccess,
			equipEnchantSuccess,
			equipGemSuccess,
			resetEquipmentSuccess,
			equipGearSetSuccess,
			equipPreviousPhaseSuccess,
			changeBuffStatusSuccess,
			changeConsumableStatusSuccess,
			changeProfessionSuccess,
		),
		map(({ characterId }) => dpsChanged({ characterId: characterId! })
	)));

	socketStatusNeedsUpdate$ = createEffect(() => this.actions$.pipe(
		ofType(
			equipItemBestVariantSuccess,
			equipItemGroupSuccess,
			equipGemSuccess,
			resetEquipmentSuccess,
			equipGearSetSuccess,
			equipPreviousPhaseSuccess
		),
		map(({ characterId }) => loadSocketStatus({ characterId }))
	));

	changeProfession$ = createEffect(() => this.actions$.pipe(
		ofType(changeProfession),
		switchMap(({ characterId, professionIdx, profession }) => this.characterService.changeProfession(characterId, professionIdx, profession).pipe(
			map(character => changeProfessionSuccess({ characterId, character })),
			catchError(error => of(changeProfessionFailure({ error })))
		))
	));

	itmeOptionsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
		),
		map(({ characterId }) => loadItemOptions( { characterId })),
	));

	enchantOptionsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
		),
		map(({ characterId }) => loadEnchantOptions( { characterId })),
	));

	gemOptionsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
		),
		map(({ characterId }) => loadGemOptions({ characterId })),
	));

	equipmentNeedsReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
		),
		map(({ characterId }) => loadEquipment({ characterId }))
	));

	// log$ = createEffect(() => this.actions$.pipe(
	// 	tap(x => console.log(x.type, x)),
	// ), { functional: true, dispatch: false });
}

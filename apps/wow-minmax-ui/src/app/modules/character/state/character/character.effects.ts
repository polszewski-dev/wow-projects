import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { catchError, filter, map, of, switchMap, tap } from "rxjs";
import { CharacterService } from '../../services/character.service';
import { EquipmentService } from '../../services/equipment.service';
import { OptionService } from '../../services/option.service';
import { loadEnchantOptions, loadEquipmentOptions, loadGemOptions, loadItemOptions } from "../equipment-options/equipment-options.actions";
import { changeAssetStatus, changeAssetStatusFailure, changeAssetStatusSuccess, changeBuffStatus, changeBuffStatusFailure, changeBuffStatusSuccess, changeConsumableStatus, changeConsumableStatusFailure, changeConsumableStatusSuccess, changeExclusiveFaction, changeExclusiveFactionFailure, changeExclusiveFactionSuccess, changeProfession, changeProfessionFailure, changeProfessionSuccess, changeScript, changeScriptFailure, changeScriptSuccess, changeTalentLink, changeTalentLinkFailure, changeTalentLinkSuccess, dpsChanged, equipEnchant, equipEnchantFailure, equipEnchantSuccess, equipGearSet, equipGearSetFailure, equipGearSetSuccess, equipGem, equipGemFailure, equipGemSuccess, equipItemBestVariant, equipItemBestVariantFailure, equipItemBestVariantSuccess, equipItemGroup, equipItemGroupFailure, equipItemGroupSuccess, equipPreviousPhase, equipPreviousPhaseFailure, equipPreviousPhaseSuccess, loadAssetStatuses, loadAssetStatusesFailure, loadAssetStatusesSuccess, loadBuffStatuses, loadBuffStatusesFailure, loadBuffStatusesSuccess, loadCharacter, loadCharacterFailure, loadCharacterSuccess, loadConsumableStatuses, loadConsumableStatusesFailure, loadConsumableStatusesSuccess, loadEquipment, loadEquipmentFailure, loadEquipmentSuccess, loadSocketStatus, loadSocketStatusFailure, loadSocketStatusSuccess, resetEquipment, resetEquipmentFailure, resetEquipmentSuccess, selectCharacter } from './character.actions';

@Injectable()
export class CharacterEffects {
	constructor(
		private actions$: Actions,
		private characterService: CharacterService,
		private equipmentService: EquipmentService,
		private optionService: OptionService,
	) {}

	selectCharacter$ = createEffect(() => this.actions$.pipe(
		ofType(selectCharacter),
		filter(({ playerId }) => !!playerId),
		switchMap(({ playerId }) => of(
			loadCharacter({ playerId: playerId! }),
			loadEquipment({ playerId: playerId! }),
			loadSocketStatus({ playerId: playerId! }),
			loadBuffStatuses({ playerId: playerId! }),
			loadConsumableStatuses({ playerId: playerId! }),
			loadAssetStatuses({ playerId: playerId! }),
			loadEquipmentOptions({ playerId: playerId! }),
			loadItemOptions({ playerId: playerId! }),
			loadEnchantOptions({ playerId: playerId! }),
			loadGemOptions({ playerId: playerId! })
		))
	));

	loadCharacter$ = createEffect(() => this.actions$.pipe(
		ofType(loadCharacter),
		switchMap(({ playerId }) => this.characterService.getCharacter(playerId).pipe(
			map(character => loadCharacterSuccess({ character })),
			catchError(error => of(loadCharacterFailure({ error })))
		))
	));

	loadEquipment$ = createEffect(() => this.actions$.pipe(
		ofType(loadEquipment),
		switchMap(({ playerId }) => this.equipmentService.getEquipment(playerId).pipe(
			map(equipment => loadEquipmentSuccess({ equipment })),
			catchError(error => of(loadEquipmentFailure({ error })))
		))
	));

	loadSocketStatus$ = createEffect(() => this.actions$.pipe(
		ofType(loadSocketStatus),
		switchMap(({ playerId }) => this.equipmentService.getSocketStatus(playerId).pipe(
			map(socketStatus => loadSocketStatusSuccess({ socketStatus })),
			catchError(error => of(loadSocketStatusFailure({ error })))
		))
	));

	loadBuffs$ = createEffect(() => this.actions$.pipe(
		ofType(loadBuffStatuses),
		switchMap(({ playerId }) => this.optionService.getBuffStatuses(playerId).pipe(
			map(buffStatuses => loadBuffStatusesSuccess({ buffStatuses })),
			catchError(error => of(loadBuffStatusesFailure({ error })))
		))
	));

	loadConsumables$ = createEffect(() => this.actions$.pipe(
		ofType(loadConsumableStatuses),
		switchMap(({ playerId }) => this.optionService.getConsumableStatuses(playerId).pipe(
			map(consumableStatuses => loadConsumableStatusesSuccess({ consumableStatuses })),
			catchError(error => of(loadConsumableStatusesFailure({ error })))
		))
	));

	loadAssets$ = createEffect(() => this.actions$.pipe(
		ofType(loadAssetStatuses),
		switchMap(({ playerId }) => this.optionService.getAssetStatuses(playerId).pipe(
			map(assetStatuses => loadAssetStatusesSuccess({ assetStatuses })),
			catchError(error => of(loadAssetStatusesFailure({ error })))
		))
	));

	equipItemBestVariant$ = createEffect(() => this.actions$.pipe(
		ofType(equipItemBestVariant),
		switchMap(({ playerId, itemSlot, item }) => this.equipmentService.equipItemBestVariant(playerId, itemSlot, item).pipe(
			map(equipmentDiff => equipItemBestVariantSuccess({ playerId, equipmentDiff })),
			catchError(error => of(equipItemBestVariantFailure({ itemSlot, error })))
		))
	));

	equipItemGroup$ = createEffect(() => this.actions$.pipe(
		ofType(equipItemGroup),
		switchMap(({ playerId, slotGroup, items }) => this.equipmentService.equipItems(playerId, slotGroup, items).pipe(
			map(equipmentDiff => equipItemGroupSuccess({ playerId, equipmentDiff })),
			catchError(error => of(equipItemGroupFailure({ slotGroup, error })))
		))
	));

	equipEnchant$ = createEffect(() => this.actions$.pipe(
		ofType(equipEnchant),
		switchMap(({ playerId, equippedItem, itemSlot, enchant }) => this.equipmentService.equipEnchant(playerId, equippedItem, itemSlot, enchant).pipe(
			map(equipmentDiff => equipEnchantSuccess({ playerId, equipmentDiff })),
			catchError(error => of(equipEnchantFailure({ itemSlot, error })))
		))
	));

	equipGem$ = createEffect(() => this.actions$.pipe(
		ofType(equipGem),
		switchMap(({ playerId, equippedItem, itemSlot, socketNo, gem }) => this.equipmentService.equipGem(playerId, equippedItem, itemSlot, socketNo, gem).pipe(
			map(equipmentDiff => equipGemSuccess({ playerId, equipmentDiff })),
			catchError(error => of(equipGemFailure({ itemSlot, socketNo, error })))
		))
	));

	resetEquipment$ = createEffect(() => this.actions$.pipe(
		ofType(resetEquipment),
		switchMap(({ playerId }) => this.equipmentService.resetEquipment(playerId).pipe(
			map(() => resetEquipmentSuccess({ playerId })),
			catchError(error => of(resetEquipmentFailure({ error })))
		))
	));

	equipGearSet$ = createEffect(() => this.actions$.pipe(
		ofType(equipGearSet),
		switchMap(({ playerId, gearSet }) => this.equipmentService.equipGearSet(playerId, gearSet).pipe(
			map(equipment => equipGearSetSuccess({ playerId, equipment })),
			catchError(error => of(equipGearSetFailure({ error })))
		))
	));

	equipPreviousPhase$ = createEffect(() => this.actions$.pipe(
		ofType(equipPreviousPhase),
		switchMap(({ playerId }) => this.equipmentService.equipPreviousPhase(playerId).pipe(
			map(equipment => equipPreviousPhaseSuccess({ playerId, equipment })),
			catchError(error => of(equipPreviousPhaseFailure({ error })))
		))
	));

	changeBuffStatus$ = createEffect(() => this.actions$.pipe(
		ofType(changeBuffStatus),
		switchMap(({ playerId, buffStatus }) => this.optionService.changeBuffStatus(playerId, buffStatus).pipe(
			map(() => changeBuffStatusSuccess({ playerId, buffStatus })),
			catchError(error => of(changeBuffStatusFailure({ error })))
		))
	));

	changeConsumableStatus$ = createEffect(() => this.actions$.pipe(
		ofType(changeConsumableStatus),
		switchMap(({ playerId, consumableStatus }) => this.optionService.changeConsumableStatus(playerId, consumableStatus).pipe(
			map(() => changeConsumableStatusSuccess({ playerId, consumableStatus })),
			catchError(error => of(changeConsumableStatusFailure({ error })))
		))
	));

	changeAssetStatus$ = createEffect(() => this.actions$.pipe(
		ofType(changeAssetStatus),
		switchMap(({ playerId, assetStatus }) => this.optionService.changeAssetStatus(playerId, assetStatus).pipe(
			map(() => changeAssetStatusSuccess({ playerId, assetStatus })),
			catchError(error => of(changeAssetStatusFailure({ error })))
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
			changeAssetStatusSuccess,
			changeProfessionSuccess,
			changeExclusiveFactionSuccess,
			changeTalentLinkSuccess,
			changeScriptSuccess,
		),
		map(({ playerId }) => dpsChanged({ playerId: playerId! })
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
		map(({ playerId }) => loadSocketStatus({ playerId }))
	));

	changeProfession$ = createEffect(() => this.actions$.pipe(
		ofType(changeProfession),
		switchMap(({ playerId, professionIdx, profession }) => this.characterService.changeProfession(playerId, professionIdx, profession).pipe(
			map(character => changeProfessionSuccess({ playerId, character })),
			catchError(error => of(changeProfessionFailure({ error })))
		))
	));

	changeExclusiveFaction$ = createEffect(() => this.actions$.pipe(
		ofType(changeExclusiveFaction),
		switchMap(({ playerId, exclusiveFaction }) => this.characterService.changeExclusiveFaction(playerId, exclusiveFaction).pipe(
			map(character => changeExclusiveFactionSuccess({ playerId })),
			catchError(error => of(changeExclusiveFactionFailure({ error })))
		))
	));

	changeTalentLink$ = createEffect(() => this.actions$.pipe(
		ofType(changeTalentLink),
		switchMap(({ playerId, talentLink }) => this.characterService.changeTalentLink(playerId, talentLink).pipe(
			map(character => changeTalentLinkSuccess({ playerId, character })),
			catchError(error => of(changeTalentLinkFailure({ error })))
		))
	));

	changeScript$ = createEffect(() => this.actions$.pipe(
		ofType(changeScript),
		switchMap(({ playerId, script }) => this.characterService.changeScript(playerId, script).pipe(
			map(character => changeScriptSuccess({ playerId, character })),
			catchError(error => of(changeScriptFailure({ error })))
		))
	));

	itmeOptionsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
			changeExclusiveFactionSuccess,
		),
		map(({ playerId }) => loadItemOptions( { playerId })),
	));

	enchantOptionsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
			changeExclusiveFactionSuccess,
		),
		map(({ playerId }) => loadEnchantOptions( { playerId })),
	));

	gemOptionsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
			changeExclusiveFactionSuccess,
		),
		map(({ playerId }) => loadGemOptions({ playerId })),
	));

	equipmentNeedsReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeProfessionSuccess,
			changeExclusiveFactionSuccess,
		),
		map(({ playerId }) => loadEquipment({ playerId }))
	));

	buffsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeTalentLinkSuccess,
		),
		map(({ playerId }) => loadBuffStatuses({ playerId }))
	));

	assetsNeedReload$ = createEffect(() => this.actions$.pipe(
		ofType(
			changeTalentLinkSuccess,
		),
		map(({ playerId }) => loadAssetStatuses({ playerId }))
	));

	changeTalentLinkFailure$ = createEffect(() => this.actions$.pipe(
		ofType(changeTalentLinkFailure),
		tap(({ error }) => alert('Incorrect talent link'))
	), { functional: true, dispatch: false });

	// log$ = createEffect(() => this.actions$.pipe(
	// 	tap(x => console.log(x.type, x)),
	// ), { functional: true, dispatch: false });
}

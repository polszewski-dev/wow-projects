"use strict";

const slots = [
	'Head',
	'Neck',
	'Shoulder',
	'Back',
	'Chest',
	'Wrist',
	'Hands',
	'Waist',
	'Legs',
	'Feet',
	'Finger1',
	'Finger2',
	'Trinket1',
	'Trinket2',
	'MainHand',
	'OffHand',
	'Ranged'
]

const slotToSlotGroup = {
	Head: 'Head',
	Neck: 'Neck',
	Shoulder: 'Shoulder',
	Back: 'Back',
	Chest: 'Chest',
	Wrist: 'Wrist',
	Hands: 'Hands',
	Waist: 'Waist',
	Legs: 'Legs',
	Feet: 'Feet',
	Finger1: 'Fingers',
	Finger2: 'Fingers',
	Trinket1: 'Trinkets',
	Trinket2: 'Trinkets',
	MainHand: 'Weapons',
	OffHand: 'Weapons',
	Ranged: 'Ranged'
}

class Api {
	static getItemsBySlot(phase) {
		return Api.sendRequest(`/api/v1/item/phase/${phase}/byslot`)
	}

	static getProfiles() {
		return Api.sendRequest(`/api/v1/profile/list`)
	}

	static createNewProfile(profileName, phase, copiedProfileId) {
		if (copiedProfileId) {
			return Api.sendRequest(`/api/v1/profile/copy/${encodeURIComponent(copiedProfileId)}/name/${encodeURIComponent(profileName)}/phase/${phase}`)
		} else {
			return Api.sendRequest(`/api/v1/profile/create/name/${encodeURIComponent(profileName)}/phase/${phase}`)
		}
	}

	static getProfile(profileId) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}`)
	}

	static getProfileWithItemOptions(profileId, addOptions) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}/add/options/${addOptions}`)
	}
	
	static changeItem(profileId, slot, itemId) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}/change/item/${encodeURIComponent(slot)}/${encodeURIComponent(itemId)}`)
	}
	
	static changeEnchant(profileId, slot, enchantId) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}/change/enchant/${encodeURIComponent(slot)}/${encodeURIComponent(enchantId)}`)
	}
	
	static changeGem(profileId, slot, socketNo, gemId) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}/change/gem/${encodeURIComponent(slot)}/${socketNo}/${encodeURIComponent(gemId)}`)
	}
	
	static getUpgrades(profileId, slot) {
		return Api.sendRequest(`/api/v1/upgrade/${encodeURIComponent(profileId)}/slot/${encodeURIComponent(slot)}`)
	}

	static getAvailableBuffs() {
		return Api.sendRequest('/api/v1/buff/list')
	}

	static enableBuff(profileId, buffId, enabled) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}/enable/buff/${encodeURIComponent(buffId)}/${enabled}`)
	}

	static getSpellStats(profileId) {
		return Api.sendRequest(`/api/v1/stats/spell/${encodeURIComponent(profileId)}`)
	}

	static getPlayerStats(profileId) {
		return Api.sendRequest(`/api/v1/stats/player/${encodeURIComponent(profileId)}`)
	}

	static getSpecialAbilityStats(profileId) {
		return Api.sendRequest(`/api/v1/stats/special/${encodeURIComponent(profileId)}`)
	}

	static resetProfile(profileId) {
		return Api.sendRequest(`/api/v1/profile/${encodeURIComponent(profileId)}/reset/equipment`)
	}

	static async sendRequest(url) {
		const response = await fetch(url, {
			method: 'GET',
			headers: {
				'Accept': 'application/json'
			}
		})
		if (response.ok) {
			return await response.json()
		}
		const message = `An error has occured: ${response.status}`;
		throw new Error(message);
	}
}

class DropdownSelect {
	constructor(id) {
		this._id = id
		this._mainElementId = `${this._id}-select`
		this._selectButtonId = `${this._id}-button`
		this._itemListId = `${this._id}-items`
		this._keyGenerator = item => item
		this._valueGenerator = item => item
	}

	get selectButton() {
		return $(`#${this._selectButtonId}`)
	}
	
	get itemList() {
		return $(`#${this._itemListId}`)
	}

	generateUI(parent) {
		const html = `
		<div class="dropdown" id="${this._mainElementId}">
			<button class="btn btn-light btn-sm dropdown-toggle select-box" type="button" id="${this._selectButtonId}" data-bs-toggle="dropdown" aria-expanded="false">
				${this._emptyValue || ''}
			</button>
			<ul class="dropdown-menu" aria-labelledby="${this._selectButtonId}" id="${this._itemListId}">
			</ul>
		</div>
		`
		parent.append(html)
	}

	set items(value) {
		if (!value || value.length == 0) {
			this.itemList.html('')
			this.refreshSelection(null, this._emptyValue)
			return
		}

		const itemList = this.itemList.html('')

		value.forEach(item => {
			const key = this._keyGenerator(item)
			const value = this._valueGenerator(item)

			$(`<li><span class="dropdown-item" href="#">${value}</span></li>`)
				.data('item-key', key)
				.click(e => {
					this.refreshSelection(key, value)
					this.triggerOnChange(key)
				})
				.appendTo(itemList)
		})

		this.refreshSelection(null, this._emptyValue)
	}

	set selected(value) {
		if (value) {
			this.refreshSelection(this._keyGenerator(value), this._valueGenerator(value))
		} else {
			this.refreshSelection(null, this._emptyValue)
		}
	}

	set enabled(value) {
		if (value) {
			this.selectButton.removeClass('disabled')
		} else {
			this.selectButton.addClass('disabled')
		}
	}

	refreshSelection(key, value) {
		this.selectButton.html(value || '')
	}

	change(handler) {
		this._onChange = handler
	}

	triggerOnChange(selectedItemKey) {
		console.log('change', this._id, selectedItemKey)
		if (this._onChange) {
			this._onChange(selectedItemKey)
		}
	}
}

class ItemSelect extends DropdownSelect {
	constructor(id) {
		super(id)
		this._emptyValue = '<i>--empty--</i>'
		this._keyGenerator = item => item.id
		this._valueGenerator = item =>
				`
				<img src="${getItemIcon(item)}"/>
				<span class="rarity-${item.rarity.toLowerCase()} item-header">&nbsp;${item.name}</span>
				<span class="item-descr">[${item.source}]</span>
				<span class="item-descr">(${item.attributes})</span>
				`
	}
}

class EnchantSelect extends DropdownSelect {
	constructor(id) {
		super(id)
		this._emptyValue = '<i>--empty--</i>'
		this._keyGenerator = enchant => enchant.id
		this._valueGenerator = enchant =>
				`
				<img src="${getEnchantIcon(enchant)}"/>&nbsp;
				<span class="rarity-common item-header">${enchant.name}</span>&nbsp;
				<span class="item-descr">(${enchant.attributes})</span>
				`
	}
}

class GemSelect extends DropdownSelect {
	constructor(id) {
		super(id)
		this._emptyValue = '<i>--empty--</i>'
		this._keyGenerator = gem => gem.id
		this._valueGenerator = gem =>
				`
				<img src="${getGemIcon(gem)}"/>&nbsp;
				<span class="rarity-${gem.rarity.toLowerCase()} item-header">${gem.name}</span>&nbsp;
				<span class="item-descr">(${gem.attributes})</span>
				`
	}
}

class ItemEditor {
	constructor(slot) {
		this._slot = slot

		this._itemSelect = new ItemSelect(`${this._slot}-item`)
		this._enchantSelect = new EnchantSelect(`${this._slot}-enchant`)
		this._gem1Select = new GemSelect(`${this._slot}-gem1`)
		this._gem2Select = new GemSelect(`${this._slot}-gem2`)
		this._gem3Select = new GemSelect(`${this._slot}-gem3`)

		this._enchantRowId = `${this._slot}-enchant-row`

		this._gem1RowId = `${this._slot}-gem1-row`
		this._gem2RowId = `${this._slot}-gem2-row`
		this._gem3RowId = `${this._slot}-gem3-row`

		this._socket1ColorId = `${this._slot}-socket1-color`
		this._socket2ColorId = `${this._slot}-socket2-color`
		this._socket3ColorId = `${this._slot}-socket3-color`

		this._socketBonusRowId = `${this._slot}-socket-bonus-row`
		this._socketBonusContainerId = `${this._slot}-socket-bonus-container`

		this._upgradeBoxId = `${this._slot}-upgrades`
		this._findUpgradesButtonId = `${this._slot}-find-upgrades-btn`
	}

	get slot() {
		return this._slot
	}
	
	get itemSelect() {
		return this._itemSelect
	}
	
	get enchantSelect() {
		return this._enchantSelect
	}
	
	get gem1Select() {
		return this._gem1Select
	}
	
	get gem2Select() {
		return this._gem2Select
	}
	
	get gem3Select() {
		return this._gem3Select
	}

	get socketBonusContainer() {
		return $(`#${this._socketBonusContainerId}`)
	}

	setSocketColor(socketNo, color) {
		const socketColor = $(`#${this._slot}-socket${socketNo}-color`)
		socketColor
			.removeClass('red-socket')
			.removeClass('yellow-socket')
			.removeClass('blue-socket')
			.removeClass('meta-socket')
		if (color) {
			socketColor.html(`[${color[0].toUpperCase()}]`).addClass(`${color.toLowerCase()}-socket`)
		} else {
			socketColor.html('')
		}
	}

	set socketBonus(value) {
		this.socketBonusContainer.html(value || '')
	}

	setSocketMatching(socketNo, value) {
		const socketColor = $(`#${this._slot}-socket${socketNo}-color`)

		if (value) {
			socketColor.removeClass('socket-notmatching').addClass('socket-matching')
		} else {
			socketColor.removeClass('socket-matching').addClass('socket-notmatching')
		}
	}

	set socketBonusEnabled(value) {
		if (value) {
			this.socketBonusContainer.removeClass('socket-bonus-disabled').addClass('socket-bonus-enabled')
		} else {
			this.socketBonusContainer.removeClass('socket-bonus-enabled').addClass('socket-bonus-disabled')
		}
	}

	set selectedItem(item) {
		this.itemSelect.selected = item ? item.item : null
		this.populateEnchantAndGems(item)
	}

	get upgradeBox() {
		return $(`#${this._upgradeBoxId}`)
	}
	
	get findUpgradesBtn() {
		return $(`#${this._findUpgradesButtonId}`)
	}
	
	generateUI(parent) {
		const html = 
		`
		<div class="accordion" id="accordion-${this._slot}">
			<div class="accordion-item">
				<h2 class="accordion-header" id="heading-${this._slot}">
					<button class="accordion-button py-2 px-3" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${this._slot}" aria-expanded="true" aria-controls="collapse${this._slot}">
						<strong>${this._slot}</strong>
					</button>
				</h2>
				<div id="collapse${this._slot}" class="accordion-collapse collapse show" aria-labelledby="heading-${this._slot}" data-bs-parent="#accordion-${this._slot}">
					<div class="accordion-body py-2 px-3">
						<div class="item-descr pb-2" id="${this._slot}-upgrades">
						</div>

						<div class="">
							<div class="row g-3" id="${this._slot}-item-row">
								<div class="col-md-2 pe-0">
									<span class="item-editor-header">Item</span>
									<button type="button" class="btn btn-sm btn-link px-0" id="${this._slot}-find-upgrades-btn" style="float:right;">Find Upgrades</button>
								</div>
								<div class="col-md-10">
									<div id="${this._slot}-item-select-container" style="float:left; display:inline;">
									</div>
								</div>
							</div>

							<div class="row g-3" id="${this._slot}-enchant-row">
								<div class="col-md-2">
									<span class="item-editor-header">Enchant</span>
								</div>
								<div class="col-md-10" id="${this._slot}-enchant-select-container">
								</div>
							</div>

							<div class="row g-3" id="${this._slot}-gem1-row">
								<div class="col-md-2 pe-0">
									<span class="item-editor-header">Gem #1</span>
									<span class="item-editor-header" style="float:right;" id="${this._socket1ColorId}"></span>
								</div>
								<div class="col-md-10" id="${this._slot}-gem1-select-container">
								</div>
							</div>

							<div class="row g-3" id="${this._slot}-gem2-row">
								<div class="col-md-2 pe-0">
									<span class="item-editor-header">Gem #2</span>
									<span class="item-editor-header" style="float:right;" id="${this._socket2ColorId}"></span>
								</div>
								<div class="col-md-10" id="${this._slot}-gem2-select-container">
								</div>
							</div>

							<div class="row g-3" id="${this._slot}-gem3-row">
								<div class="col-md-2 pe-0">
									<span class="item-editor-header">Gem #3</span>
									<span class="item-editor-header" style="float:right;" id="${this._socket3ColorId}"></span>
								</div>
								<div class="col-md-10" id="${this._slot}-gem3-select-container">
								</div>
							</div>

							<div class="row g-3" id="${this._slot}-socket-bonus-row">
								<div class="col-md-2">
									<span class="item-editor-header">Socket Bonus</span>
								</div>
								<div class="col-md-10" id="${this._socketBonusContainerId}">
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
		`

		parent.append(html)

		$(`#${this._enchantRowId}`).hide()
		$(`#${this._gem1RowId}`).hide()
		$(`#${this._gem2RowId}`).hide()
		$(`#${this._gem3RowId}`).hide()
		$(`#${this._socketBonusRowId}`).hide()

		this.upgradeBox.hide()

		this.itemSelect.generateUI($(`#${this._slot}-item-select-container`))
		this.enchantSelect.generateUI($(`#${this._slot}-enchant-select-container`))
		this.gem1Select.generateUI($(`#${this._slot}-gem1-select-container`))
		this.gem2Select.generateUI($(`#${this._slot}-gem2-select-container`))
		this.gem3Select.generateUI($(`#${this._slot}-gem3-select-container`))
	}

	populateEnchantAndGems(item) {
		let availableEnchants = null
		let availableGems1 = null
		let availableGems2 = null
		let availableGems3 = null

		let enchant = null
		let gem1 = null
		let gem2 = null
		let gem3 = null

		let socket1Color = null
		let socket2Color = null
		let socket3Color = null

		let socket1Matching = false
		let socket2Matching = false
		let socket3Matching = false

		let socketBonus = null
		let socketBonusEnabled = false

		let showEnchantSelect = false
		let showGem1Select = false
		let showGem2Select = false
		let showGem3Select = false
		let showSocketBonus = false

		if (item) {
			availableEnchants = item.itemOptions.availableEnchants
			availableGems1 = item.itemOptions.availableGems1
			availableGems2 = item.itemOptions.availableGems2
			availableGems3 = item.itemOptions.availableGems3

			enchant = item.enchant
			gem1 = item.gem1
			gem2 = item.gem2
			gem3 = item.gem3

			socket1Color = item.item.socket1Color
			socket2Color = item.item.socket2Color
			socket3Color = item.item.socket3Color

			socket1Matching = item.socket1Matching
			socket2Matching = item.socket2Matching
			socket3Matching = item.socket3Matching

			socketBonus = item.item.socketBonus
			socketBonusEnabled = item.socketBonusEnabled

			showEnchantSelect = (availableEnchants || []).length != 0
			showGem1Select = (availableGems1 || []).length != 0
			showGem2Select = (availableGems2 || []).length != 0
			showGem3Select = (availableGems3 || []).length != 0
			showSocketBonus = !!item.item.socketBonus
		}

		this.enchantSelect.items = availableEnchants
		this.gem1Select.items = availableGems1
		this.gem2Select.items = availableGems2
		this.gem3Select.items = availableGems3

		this.enchantSelect.selected = enchant
		this.gem1Select.selected = gem1
		this.gem2Select.selected = gem2
		this.gem3Select.selected = gem3

		this.setSocketColor(1, socket1Color)
		this.setSocketColor(2, socket2Color)
		this.setSocketColor(3, socket3Color)

		this.setSocketMatching(1, socket1Matching)
		this.setSocketMatching(2, socket2Matching)
		this.setSocketMatching(3, socket3Matching)

		this.socketBonus = socketBonus
		this.socketBonusEnabled = socketBonusEnabled

		$(`#${this._enchantRowId}`).toggle(showEnchantSelect)
		$(`#${this._gem1RowId}`).toggle(showGem1Select)
		$(`#${this._gem2RowId}`).toggle(showGem2Select)
		$(`#${this._gem3RowId}`).toggle(showGem3Select)
		$(`#${this._socketBonusRowId}`).toggle(showSocketBonus)
	}
}

class EquipmentEditor {
	constructor() {
		this._itemEditorsBySlot = {}
		this._itemsBySlot = null
	}

	set profile(value) {
		this._profile = value
    	Api.getItemsBySlot(this._profile.phase)
    	.then(data => {
    		this.itemsBySlot = data
    		slots.forEach(slot => this.selectItem(slot))
    	})
    	.catch(err => showError(err))
    }

    getEquippedItem(slot) {
    	const field = slot[0].toLowerCase() + slot.substr(1)
    	return this._profile.equipment[field]
    }

    setEquippedItem(item, slot) {
		const field = slot[0].toLowerCase() + slot.substr(1)
		this._profile.equipment[field] = item
	}

	getItemEditor(slot) {
		return this._itemEditorsBySlot[slot]
	}

	getItems(slot) {
		return this._itemsBySlot[slot] || []
	}

	set itemsBySlot(value) {
		this._itemsBySlot = value
		slots.forEach(slot => {
			const items = this.getItems(slot)
			const editor = this.getItemEditor(slot)
			editor.itemSelect.items = items
		})
	}

	selectItem(slot) {
		const editor = this.getItemEditor(slot)
		const item = this.getEquippedItem(slot)

		editor.selectedItem = item

		if (editor.slot == 'MainHand') {
			const offHandEditor = this.getItemEditor('OffHand')

			if (!item || item.item.itemType != 'TwoHand') {
				offHandEditor.itemSelect.enabled = true
			} else {
				offHandEditor.itemSelect.enabled = false
				offHandEditor.selectedItem = null
			}
		}
	}

	generateUI(parent) {
		parent.append(`
			<div class="border">
				<button type="button" class="btn btn-sm btn-link" id="find-all-upgrades-btn">Find All Upgrades</button>
				<button type="button" class="btn btn-sm btn-link" id="find-reset-btn">Reset</button>
			</div>
		`)
		slots.forEach(slot => {
			this._itemEditorsBySlot[slot] = new ItemEditor(slot)
			const editor = this.getItemEditor(slot)
			editor.generateUI(parent)
		})
		this.setupEvents()
	}

	setupEvents() {
		slots.forEach(slot => {
    		const editor = this.getItemEditor(slot)

    		editor.itemSelect.change(itemId => {
    			Api.changeItem(this._profile.profileId, slot, itemId)
    			.then(item => {
    				this.setEquippedItem(item, slot)
					this.selectItem(slot)
					this.triggerOnChange()
    			})
    			.catch(err => showError(err))
    		})

    		editor.enchantSelect.change(enchantId => {
    			Api.changeEnchant(this._profile.profileId, slot, enchantId)
    			.then(item => {
    				this.setEquippedItem(item, slot)
    				this.triggerOnChange()
    			})
    			.catch(err => showError(err))
    		})

			const handleGemSelection = (slot, socketNo, gemId) => {
				return Api.changeGem(this._profile.profileId, slot, socketNo, gemId)
				.then(item => {
					this.setEquippedItem(item, slot)
					editor.setSocketMatching(1, item.socket1Matching)
					editor.setSocketMatching(2, item.socket2Matching)
					editor.setSocketMatching(3, item.socket3Matching)
					editor.socketBonusEnabled = item.socketBonusEnabled
					this.triggerOnChange()
				})
				.catch(err => showError(err))
			}

			editor.gem1Select.change(gemId => {
				handleGemSelection(slot, 1, gemId)
			})

			editor.gem2Select.change(gemId => {
				handleGemSelection(slot, 2, gemId)
			})

			editor.gem3Select.change(gemId => {
				handleGemSelection(slot, 3, gemId)
			})

    		if (!(editor.slot == 'Finger2' || editor.slot == 'Trinket2' || editor.slot == 'OffHand')) {
    			editor.findUpgradesBtn.click(e => {
    				e.preventDefault()
    				$('#error-container').hide()
    				editor.upgradeBox.show().html('<i>waiting...</i>')
    				Api.getUpgrades(this._profile.profileId, slotToSlotGroup[slot])
    				.then(upgrades => {
    					let lines = []
    					upgrades.forEach(upgrade => {
    						lines.push(`<b>${upgrade.changePct}%:</b> ${this.itemDiffToHtml(upgrade.itemDifference)}, <b>diff:</b> ${upgrade.statDifference.join(",&nbsp;")}, ${upgrade.abilityDifference.join(",&nbsp;")}<br/>`)
    					})
    					editor.upgradeBox.show().html(lines.length > 0 ? lines : '<span style="color:green">No upgrades found</span>')
    				})
    				.catch(err => {
    					editor.upgradeBox.show().html('')
    					showError(err)
					})
    			})
    		} else {
    			editor.findUpgradesBtn.hide()
    		}
    	})

    	$('#find-all-upgrades-btn').click(e => {
    		e.preventDefault()
    		slots.forEach(slot => {
    			this.getItemEditor(slot).findUpgradesBtn.click()
    		})
    	})

    	$('#find-reset-btn').click(e => {
    		e.preventDefault()

			slots.forEach(slot => {
				this.getItemEditor(slot).upgradeBox.hide()
			})

    		Api.resetProfile(this._profile.profileId)
    		.then(data => {
    			this._profile = data
				slots.forEach(slot => this.selectItem(slot))
				this.triggerOnChange()
    		})
    		.catch(err => {
				showError(err)
			})
    	})
	}

	itemDiffToHtml(itemDifference) {
		return itemDifference.map(item => {
			const gems = [item.gem1, item.gem2, item.gem3]
				.filter(gem => !!gem)
				.map(gem => `<span class="${gem.color.toLowerCase()}-gem item-header">[${gem.shortName}]</span>`).join('')
			return `[${item.item.source}] <span class="rarity-${item.item.rarity.toLowerCase()} item-header">${item.item.name}</span>&nbsp;${gems}`
		}).join (' + ')
	}

	change(handler) {
		this._onChange = handler
	}

	triggerOnChange() {
		console.log('change equipment')
		if (this._onChange) {
			this._onChange()
		}
	}
}

class BuffEditor {
	constructor() {
		this.buffContainerId = 'buff-container'
	}

	set profile(value) {
		this._profile = value
    	Api.getAvailableBuffs()
    	.then(availableBuffs => {
    		this.availableBuffs = availableBuffs
    		this.selectedBuffs = this._profile.buffs
    	})
    	.catch(err => showError(err))
	}

	generateUI(parent) {
		const html =
		`
		<div class="mb-2 border p-4">
			<div class="row g-3">
				<div class="col-md-12">
					<div id="${this.buffContainerId}"></div>
				</div>
			</div>
		</div>
		`
		parent.append(html)
	}

	getBuffContainer() {
		return $(`#${this.buffContainerId}`)
	}

	set availableBuffs(value) {
		this.getBuffContainer().html('')
		value.forEach(buff => {
			this.getBuffContainer().append(`
				<div class="form-check">
					<input class="form-check-input" type="checkbox" value="" id="buff-select-${buff.id}">
					<label class="form-check-label" for="buff-select-${buff.id}">
						<span class="buff-name">
							<img src="${getBuffIcon(buff)}"/>&nbsp;${buff.name}
						</span>
						<span class="buff-stats">
							(${buff.attributes})
						</span>
					</label>
				</div>
			`)

			$(`#buff-select-${buff.id}`).change(e => {
				const profileId = this._profile.profileId
				const buffId = buff.id
				const enabled = $(`#buff-select-${buff.id}`).prop('checked')
				console.log('xxx', profileId, buffId, enabled)
				Api.enableBuff(profileId, buffId, enabled)
				.then(buffs => {
					value.forEach(buff => $(`#buff-select-${buff.id}`).prop('checked', false))
					buffs.forEach(buff => $(`#buff-select-${buff.id}`).prop('checked', true))
					this.triggerOnChange()
				})
				.catch(err => showError(err))
			})
		})
	}

	set selectedBuffs(value) {
		value.forEach(buff => {
			$(`#buff-select-${buff.id}`).prop("checked", true)
		})
	}

	change(handler) {
		this._onChange = handler
	}

	triggerOnChange() {
		console.log('change buffs')
		if (this._onChange) {
			this._onChange()
		}
	}
}

class SpellSummary {
	constructor() {
		this._containerId = `results-spells-body`
	}

	set profile(value) {
		this._profile = value
		this.refresh()
	}

	generateUI(parent) {
		const html =
		`
		<div id="${this._containerId}">
			<table class="table table-striped mb-0 spell-stats-table">
				<thead>
        			<tr>
        				<th scope="col">spell</th>
        				<th scope="col">dps</th>
        				<th scope="col">dmg</th>
        				<th scope="col">cast</th>
        				<th scope="col">mana</th>
						<th scope="col">dpm</th>
						<th scope="col">sp</th>
						<th scope="col">hit%</th>
						<th scope="col">crit%</th>
						<th scope="col">hast%</th>
						<th scope="col">coeff dir</th>
						<th scope="col">coeff dot</th>
						<th scope="col">crCo</th>
						<th scope="col">hit eqv</th>
						<th scope="col">crit eqv</th>
						<th scope="col">hast eqv</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		`

		parent.html(html)
	}

	refresh() {
		console.log('refresh spells')
		Api.getSpellStats(this._profile.profileId)
		.then(data => {
			const tbody = $(`#${this._containerId}`).find('tbody')
			const fmt = num => num.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})

			tbody.html('')

			data.forEach(spellStats => {
				const html = `
				<tr>
					<td><strong><img src="${getSpellIcon(spellStats.spellName)}"/>&nbsp;${spellStats.spellName}</strong></td>
					<td>${spellStats.dps}</td>
					<td>${spellStats.totalDamage}</td>
					<td>${fmt(spellStats.castTime)}</td>
					<td>${spellStats.manaCost}</td>
					<td>${spellStats.dpm}</td>
					<td>${spellStats.sp}</td>
					<td>${fmt(spellStats.totalHit)}</td>
					<td>${spellStats.totalCrit != 0 ? fmt(spellStats.totalCrit) : ""}</td>
					<td>${fmt(spellStats.totalHaste)}</td>
					<td>${spellStats.spellCoeffDirect != 0 ? fmt(spellStats.spellCoeffDirect) : ""}</td>
					<td>${spellStats.spellCoeffDoT != 0 ? fmt(spellStats.spellCoeffDoT) : ""}</td>
					<td>${spellStats.critCoeff != 0 ? fmt(spellStats.critCoeff) : ""}</td>
					<td>${spellStats.hitSpEqv != 0 ? fmt(spellStats.hitSpEqv) : ""}</td>
					<td>${spellStats.critSpEqv != 0 ? fmt(spellStats.critSpEqv) : ""}</td>
					<td>${spellStats.hasteSpEqv != 0 ? fmt(spellStats.hasteSpEqv) : ""}</td>
				</tr>
				`
				tbody.append(html)
			})

		})
		.catch(err => showError(err))
	}
}

class StatsSummary {
	constructor() {
		this._containerId = `results-stats-body`
	}

	set profile(value) {
		this._profile = value
		this.refresh()
	}

	generateUI(parent) {
		const html =
		`
		<div id="${this._containerId}">
			<table class="table table-striped mb-0 player-stats-table">
				<thead>
					<tr>
						<th scope="col">type</th>
						<th scope="col">sp</th>
                        <th scope="col">shadow sp</th>
                        <th scope="col">fire sp</th>
                        <th scope="col">hit%</th>
                        <th scope="col">crit%</th>
                        <th scope="col">haste%</th>
                        <th scope="col">hit rating</th>
                        <th scope="col">crit rating</th>
                        <th scope="col">haste rating</th>
                        <th scope="col">stamina</th>
                        <th scope="col">intellect</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		`

		parent.html(html)
	}

	refresh() {
		console.log('refresh stats')
		Api.getPlayerStats(this._profile.profileId)
		.then(data => {
			const tbody = $(`#${this._containerId}`).find('tbody')
			const fmt = num => num.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})//TODO

			tbody.html('')

			data.forEach(playerStats => {
				const html =
				`
				<tr>
					<td><strong>${playerStats.type}</strong></td>
					<td>${playerStats.sp}</td>
					<td>${playerStats.spShadow}</td>
					<td>${playerStats.spFire}</td>
					<td>${fmt(playerStats.hitPct)}</td>
					<td>${fmt(playerStats.critPct)}</td>
					<td>${fmt(playerStats.hastePct)}</td>
					<td>${playerStats.hitRating}</td>
					<td>${playerStats.critRating}</td>
					<td>${playerStats.hasteRating}</td>
					<td>${playerStats.stamina}</td>
					<td>${playerStats.intellect}</td>
				</tr>
				`
				tbody.append(html)
			})
		})
		.catch(err => showError(err))
	}
}

class SpecialAbilitiesSummary {
	constructor() {
		this._containerId = `results-special-body`
	}

	set profile(value) {
		this._profile = value
		this.refresh()
	}

	generateUI(parent) {
		const html =
		`
		<div id="${this._containerId}">
			<table class="table table-striped mb-0 special-stats-table">
				<thead>
					<tr>
						<th scope="col">description</th>
						<th scope="col">ability</th>
						<th scope="col">stat equivalent</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		`

		parent.html(html)
	}

	refresh() {
		console.log('refresh special')
		Api.getSpecialAbilityStats(this._profile.profileId)
		.then(data => {
			const tbody = $(`#${this._containerId}`).find('tbody')
			const fmt = num => num.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})//TODO

			tbody.html('')

			data.forEach(specialStats => {
				const html =
				`
				<tr>
					<td>${specialStats.description}</td>
					<td>${specialStats.ability}</td>
					<td>${specialStats.statEquivalent}</td>
				</tr>
				`
				tbody.append(html)
			})
		})
		.catch(err => showError(err))
	}
}

const showError = err => $('#error-container').text(err).show()

$(() => {
	const equipmentEditor = new EquipmentEditor()
	equipmentEditor.generateUI($('#equipment'))

	const buffEditor = new BuffEditor()
	buffEditor.generateUI($('#buffs'))

	const spellSummary = new SpellSummary()
	spellSummary.generateUI($('#results-spells-container'))

	const statsSummary = new StatsSummary()
	statsSummary.generateUI($('#results-stats-container'))

	const specialSummary = new SpecialAbilitiesSummary()
	specialSummary.generateUI($('#results-special-container'))

	const refreshStats = () => {
		spellSummary.refresh()
		statsSummary.refresh()
		specialSummary.refresh()
	}

	equipmentEditor.change(() => refreshStats())
	buffEditor.change(() => refreshStats())

	$('#profile-select').change(e => {
		const profileId = $(e.target).val()

		console.log('changed', profileId)

		Api.getProfileWithItemOptions(profileId, true)
		.then(profile => {
			equipmentEditor.profile = profile
			buffEditor.profile = profile
			spellSummary.profile = profile
			statsSummary.profile = profile
			specialSummary.profile = profile
		})
		.catch(err => showError(err))
	})

	const populateProfileSelect = (profiles, selectedProfileId) => {
		const select = $('#profile-select').html('')

		if (profiles.length > 0) {
			profiles
				.sort(profile => profile.profileName)
				.forEach(profile =>
					select.append(`<option value="${profile.profileId}">${profile.profileName} - P${profile.phase}</option>`)
				)
		}

		if (selectedProfileId) {
			select.val(selectedProfileId).change()
		}
	}

	$('#new-profile-btn').click(e => {
		e.preventDefault()

		let name
		let phase
		let copiedProfileId

		if (equipmentEditor._profile) {
			name = equipmentEditor._profile.profileName + ' - copy'
			phase = equipmentEditor._profile.phase
			copiedProfileId = equipmentEditor._profile.profileId
		} else {
			name = new Date().toLocaleString('pl-PL') // TODO
			phase = 5
			copiedProfileId = null
		}

		Api.createNewProfile(name, phase, copiedProfileId)
		.then(profile => {
			const profileId = profile.profileId

			console.log('created', profileId)

			Api.getProfiles()
			.then(profiles => {
				populateProfileSelect(profiles, profileId)
			})
			.catch(err => showError(err))
		})
		.catch(err => showError(err))
	})

	Api.getProfiles()
	.then(profiles => {
		let selectedProfileId = null

		if (profiles.length > 0) {
			selectedProfileId = profiles.sort(profile => profile.lastModified).reverse()[0].profileId
		}

		populateProfileSelect(profiles, selectedProfileId)
	})
	.catch(err => showError(err))
})
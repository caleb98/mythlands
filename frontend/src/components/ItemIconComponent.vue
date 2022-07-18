<template>
	<div class="item-icon-container p-2 m-1"
		:id="elementId"
		@mouseenter="hoverStart"
		@mouseleave="hoverEnd"
		@dragover="allowDrop"
		@drop="drop">


		<div draggable="true" style="background-color: #00000000" @dragstart="drag" @mouseup="click" @contextmenu="event => event.preventDefault()">
			<img :src="'/img' + icon" class="item-icon-image" draggable="false" @contextmenu="event => event.preventDefault()">

			<div class="item-overlay-wrapper" v-if="displayItem">
				<div class="item-count" v-if="displayItem.stackSize != 1">{{ displayItem.count }}</div>
				<div class="item-cooldown" v-if="onCooldown">{{ cooldownTime }}</div>
			</div>
		</div>

		<div class="recharge-overlay" :id="elementId + '-recharge-overlay'"
			:style="{ 'animation-duration': cooldownDuration + 's' }"
			:class="{ 'cooldown-active': onCooldown }">
		</div>

		<div :id="'item-tooltip-slot-' + itemSlot">
			<ItemTooltip v-if="hovering && displayItem"
				:itemInstance="displayItem" 
				:maxWidth="300" 
				:xPos="tooltipX"
				:yPos="tooltipY"/>
		</div>
	</div>
</template>

<script>
import ItemTooltip from './ItemTooltip.vue';
import $ from 'jquery';
import WS from '../services/wsclient';

export default {
	name: 'ItemIconComponent',
	components: {
		ItemTooltip,
	},
	props: {
		displayItem: Object,
		itemSlot: String
	},

	data() {
		return {
			hovering: false,
			tooltipX: 0,
			tooltipY: 0,
			onCooldown: false,
			cooldownTime: 0,
		}
	},

	computed: {
		rarityColor() {
			switch(this.displayItem.rarity) {
				case "JUNK": return'gray';
				case "COMMON": return 'white';
				case "UNCOMMON": return'#07d907';
				case "RARE": return 'dodgerblue'
				case "EXQUISITE": return '#c77aff';
				case "LEGENDARY": return 'orange';
				default: return 'hotpink';			
			}
		},

		itemTooltipStyle() {
			return {
				borderColor: this.rarityColor,
				color: this.rarityColor
			}
		},

		icon() {
			if(this.displayItem) {
				return this.displayItem.icon;
			}
			else {
				return "/item/empty.png";
			}
		},

		elementId() {
			return 'item-slot-' + this.itemSlot;
		},

		hasCooldown() {
			return this.displayItem ? this.displayItem.cooldownFinish : false;
		},

		cooldownDuration() {
			if(this.hasCooldown) {
				return (this.displayItem.cooldownFinish - this.displayItem.cooldownStart) / 1000;
			}
			else {
				return 0;
			}
		}
	},

	methods: {
		hoverStart() {
			if(this.displayItem) {
				this.hovering = true;
				let element = $("#" + this.elementId);
				this.tooltipX = element.offset().left + element.outerWidth() ;
				this.tooltipY = element.offset().top + element.outerHeight();

				// Move the item tooltip to the page body
				$("#item-tooltip-slot-" + this.itemSlot).appendTo($("#app"));
			}
		},

		hoverEnd() {
			if(this.displayItem) {
				this.hovering = false;

				// Move the item tooltip back into this element.
				$("#item-tooltip-slot-" + this.itemSlot).appendTo($("#" + this.elementId));
			}
		},

		allowDrop(event) {
			event.preventDefault();
		},

		drop(event) {
			event.preventDefault();
			var fromSlot = event.dataTransfer.getData("fromSlot");
			var fromItem = event.dataTransfer.getData("fromItem");
			var toSlot = this.itemSlot;
			var toItem = this.displayItem;

			// Ignore moving empty item
			if(fromItem == "null") return;

			// Choose the appropriate way to handle the drag and drop
			// depending on the type of slots we used.
			if(this.includesEquipSlot(fromSlot, toSlot)) {

				let equipSlot, invSlot;

				// Do nothing if we're trying to move items between slots
				// (This wont ever work because items can only be equipped to one slot.)
				if(isNaN(fromSlot) && isNaN(toSlot)) {
					return;
				}
				// Moving an item from an equip slot to an inventory slot
				else if(isNaN(fromSlot)) {
					equipSlot = this.getEquipSlot(fromSlot);
					invSlot = toSlot;
				}
				// Moving an item from an inventory slot to an equip slot
				else {
					equipSlot = this.getEquipSlot(toSlot);
					invSlot = fromSlot;
				}

				WS.publish({
					destination: "/game/character.equip",
					body: JSON.stringify({
						equipSlot: equipSlot,
						invSlot: invSlot
					})
				});

			}
			else {

				WS.publish({
					destination: "/game/character.moveinventory",
					body: JSON.stringify({
						fromSlot: fromSlot,
						toSlot: toSlot
					})
				});
				
			}
		},

		drag(event) {
			event.dataTransfer.setData("fromSlot", this.itemSlot);
			event.dataTransfer.setData("fromItem", this.displayItem ? this.displayItem : null);
			this.hovering = false;
		},

		click(event) {
			if(event.button == 2 && !this.onCooldown) {
				WS.publish({
					destination: "/game/character.useinventory",
					body: JSON.stringify({
						useSlot: this.itemSlot
					})
				});
			}

			event.preventDefault();
			return false;
		},

		includesEquipSlot(fromSlot, toSlot) {
			return fromSlot == "weapon-item" || fromSlot == "armor-item" || fromSlot == "trinket-item"
					|| toSlot == "weapon-item" || toSlot == "armor-item" || toSlot == "trinket-item"
		},

		getEquipSlot(slot) {
			switch(slot) {
				case "weapon-item": return "WEAPON";
				case "armor-item": return "ARMOR";
				case "trinket-item": return "TRINKET";
				default: return null;
			}
		},

		updateCooldownTimer() {
			this.cooldownTime = ((this.displayItem.cooldownFinish - Date.now()) / 1000).toFixed(1);
			if(this.onCooldown) {
				this.cooldownTimer = setTimeout(this.updateCooldownTimer, 10);
			}
			else {
				this.cooldownTimer = null;
			}
		}
	},

	watch: {
		displayItem() {
			if(this.hasCooldown && (Date.now() < this.displayItem.cooldownFinish)) {
				this.onCooldown = true;
				setTimeout(() => {
					this.onCooldown = false;
				}, this.displayItem.cooldownFinish - Date.now());
				
				this.cooldownTimer = setTimeout(this.updateCooldownTimer, 10);
			}
		}
	}
}
</script>

<style scoped>
.item-icon-container {
	position: relative;
	border: 1px solid black;
	border-radius: 3px;
	background-color: #bb9255;
	box-shadow: 1px 1px 5px #0000006b inset;
}

.item-icon-container:hover {
	border-color: #dda847;
}

.item-icon-image {
	width: 32px;
	height: 32px;
	image-rendering: pixelated;
	image-rendering: crisp-edges;
}

.item-overlay-wrapper {
	position: relative;
}

.item-count {
	position: absolute;
	bottom: -8px;
	right: -5px;
	color: yellow;
	user-select: none;
}

.item-cooldown {
	position: absolute;
	bottom: 18px;
	left: 0%;
	width: 100%;
	color: yellow;
	user-select: none;
	font-size: 14px;
	font-family: sans-serif;
	font-weight: bold;
}

.recharge-overlay {
	position: absolute;
	background-color: rgba(236, 58, 58, 0.575);
	width: 100%;
	height: 0%;
	top: 0%;
	left: 0%;
	border-radius: 3px;
}

.cooldown-active {
	animation-name: cooldown;
	animation-timing-function: linear;
}

@keyframes cooldown {
	from { 
		height: 100%;
		top: 0%;
	}
	to { 
		height: 0%;
		top: 100%;
	}
}
</style>

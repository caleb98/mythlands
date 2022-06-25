<template>
	<div class="item-icon-container p-2 m-1"
		:id="elementId"
		@mouseenter="hoverStart"
		@mouseleave="hoverEnd"
		@dragover="allowDrop"
		@drop="drop">


		<div draggable="true" style="background-color: #00000000" @dragstart="drag">
			<img :src="'/img' + icon" class="item-icon-image" draggable="false">

			<div class="item-count-wrapper" v-if="displayItem">
				<div class="item-count" v-if="displayItem.template.stackSize != 1">{{displayItem.count}}</div>
			</div>
		</div>

		<ItemTooltip v-if="hovering && displayItem" 
			:itemInstance="displayItem" 
			:maxWidth="300" 
			:xPos="tooltipX"
			:yPos="tooltipY"/>
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
		itemSlot: Number
	},

	data() {
		return {
			hovering: false,
			tooltipX: 0,
			tooltipY: 0
		}
	},

	computed: {
		rarityColor() {
			switch(this.displayItem.template.rarity) {
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
				return this.displayItem.template.icon;
			}
			else {
				return "/item/empty.png";
			}
		},

		elementId() {
			return 'item-slot-' + this.itemSlot;
		}
	},

	methods: {
		hoverStart() {
			if(this.displayItem) {
				this.hovering = true;
				let element = $("#" + this.elementId);
				this.tooltipX = element.offset().left+ element.outerWidth();
				this.tooltipY = element.offset().top + element.outerHeight();
			}
		},

		hoverEnd() {
			if(this.displayItem) {
				this.hovering = false;
			}
		},

		allowDrop(event) {
			event.preventDefault();
		},

		drop(event) {
			event.preventDefault();
			var fromSlot = event.dataTransfer.getData("fromSlot");
			var toSlot = this.itemSlot;

			WS.publish({
				destination: "/game/character.moveinventory",
				body: JSON.stringify({
					fromSlot: fromSlot,
					toSlot: toSlot
				})
			});
		},

		drag(event) {
			event.dataTransfer.setData("fromSlot", this.itemSlot);
			this.hovering = false;
		}


	}
}
</script>

<style scoped>
.item-icon-container {
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

.item-count-wrapper {
	position: relative;
}

.item-count {
	position: absolute;
	bottom: -8px;
	right: -5px;
	color: yellow;
	user-select: none;
}
</style>

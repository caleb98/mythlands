<template>
	<div class="item-icon-container p-2 m-2"
		:id="'item-icon-' + displayItem.id"
		@mouseenter="hoverStart"
		@mouseleave="hoverEnd">

		<img :src="'/img' + displayItem.template.icon" class="item-icon-image">
		<div class="item-count-wrapper">
			<div class="item-count" v-if="displayItem.template.stackSize != 1">{{displayItem.count}}</div>
		</div>
		<ItemTooltip v-if="hovering" 
			:itemInstance="displayItem" 
			:maxWidth="300" 
			:xPos="tooltipX"
			:yPos="tooltipY"/>
	</div>
</template>

<script>
import ItemTooltip from './ItemTooltip.vue';
import $ from 'jquery';

export default {
	name: 'ItemIconComponent',
	components: {
		ItemTooltip,
	},
	props: {
		displayItem: Object
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
		}
	},

	methods: {
		hoverStart() {
			this.hovering = true;
			let element = $("#item-icon-" + this.displayItem.id);
			this.tooltipX = element.offset().left+ element.outerWidth();
			this.tooltipY = element.offset().top + element.outerHeight();
		},

		hoverEnd() {
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

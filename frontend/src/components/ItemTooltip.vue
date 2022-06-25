<template>
	<div class="item-tooltip text-start justify-content-center"
		:id="'item-tooltip-' + itemInstance.id"
		:style="tooltipStyle">

		<!-- Item Name -->
		<div class="row">
			<div class="col-auto">{{itemInstance.template.name}}</div>
		</div>

		<!-- Item Desc -->
		<div class="row mt-2 clean-text fst-italic">
			<div class="col-auto">{{itemInstance.template.description}}</div>
		</div>

	</div>
</template>

<script>
import $ from 'jquery';

export default {
	name: "ItemTooltip",
	props: {
		xPos: Number,
		yPos: Number,
		maxWidth: Number,
		itemInstance: Object,
	},
	
	computed: {
		rarityColor() {
			switch(this.itemInstance.template.rarity) {
				case "JUNK": return'gray';
				case "COMMON": return 'white';
				case "UNCOMMON": return'#07d907';
				case "RARE": return 'dodgerblue'
				case "EXQUISITE": return '#c77aff';
				case "LEGENDARY": return 'orange';
				default: return 'hotpink';			
			}
		},

		tooltipStyle() {
			return {
				left: this.xPos + "px",
				top: this.yPos + "px",
				maxWidth: this.maxWidth + "px",
				borderColor: this.rarityColor,
				color: this.rarityColor
			}
		}
	},
}
</script>

<style>
.item-tooltip {
	position: absolute;
	z-index: 1;

	padding: 5px;
	background-color: black;
	border-style: solid;
	border-width: 3px;
	border-radius: 3px;
}
</style>
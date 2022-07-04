<template>
	<div class="item-tooltip text-start justify-content-center"
		:id="'item-tooltip-' + itemInstance.id"
		:style="tooltipStyle">

		<!-- Item Name -->
		<div class="row">
			<div class="col-auto">{{itemInstance.template.name}}</div>
		</div>
	
		<!-- Type/Slot -->
		<div class="row item-type clean-text">
			<div class="col-auto">{{itemType}}</div>
		</div>


		<!-- Item Affixes  -->
		<div class="row" v-if="itemInstance.affixes">
			<div class="col-auto">
				<ul class="affix-list clean-text m-0 p-1">
					<li v-for="affix in itemInstance.affixes" :key="affix.id">{{affix.description}}</li>
				</ul>
			</div>
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
		},

		itemType() {
			if(this.itemInstance.template.slot) {
				let slot = this.itemInstance.template.slot;
				return slot.charAt(0).toUpperCase() + slot.slice(1).toLowerCase();
			}
			else {
				return this.itemInstance.type;
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

.item-type {
	color: white;
	font-size: 14px;
}

.affix-list {
	color: white;
	list-style-type: none;
	list-style-position: inside;
}
</style>
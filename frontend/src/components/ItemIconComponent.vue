<template>
	<div class="item-icon-container p-2 m-2">
		<img :src="'/img' + displayItem.template.icon" class="item-icon-image">
		<div class="item-count" v-if="displayItem.template.stackSize != 1">{{displayItem.count}}</div>
	</div>
</template>

<script>
export default {
	name: 'ItemIconComponent',
	props: {
		displayItem: Object
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

.item-count {
	position: absolute;
	bottom: 0%;
	right: 5%;
	color: yellow;
	user-select: none;
}
</style>

<template>
	<transition name="fade">
		<div v-if="show" class="context-menu clean-text text-start"
			:id="menuId"
			:style="{ left: xPos + 'px', top: yPos + 'px' }">

			<div v-for="(button, index) in options" :key="button.text" @click="button.onClick" 
				class="context-menu-item px-3 py-2" 
				:class="{ 'top-element': index == 0, 'bottom-element': index == options.length - 1 }">

				{{ button.text }}
			</div>
		</div>
	</transition>
</template>

<script>
import $ from 'jquery';

export default {
	props: {
		menuId: String,
		show: Boolean,
		xPos: Number,
		yPos: Number,
		options: Array
	},
	emits: ["contextMenuClose"],
	methods: {
		clickDetect(event) {
			var target = $(event.target);
			if(!target.closest($("#" + this.menuId)).length) {
				this.$emit("contextMenuClose");
			}
		}
	},
	watch: {
		show() {
			// Register the event handler for detecting mouse clicks
			// outside of the menu.
			if(this.show) {
				$(document).on(
					"click",
					this.clickDetect
				);
			}

			// Remove the click detect callback.
			else {
				$(document).off(
					"click",
					null,
					this.clickDetect
				);
			}
		}
	}
}
</script>

<style>
@import '../assets/css/main.css';

.context-menu {
	position: absolute;
	font-size: 14px;
	user-select: none;
	border-radius: 10px;
	box-shadow: 1px 1px 5px #00000030;
}

.top-element {
	border-top-left-radius: 10px;
	border-top-right-radius: 10px;
}

.bottom-element {
	border-bottom-left-radius: 10px;
	border-bottom-right-radius: 10px;
}

.context-menu-item {
	background-color: #e2f8f9;
	transition: background-color 0.2s ease-in-out;
}

.context-menu-item:hover {
	background-color: rgb(196, 232, 233);
}

.context-menu-item:active {
	background-color: hsl(199, 43%, 60%);
}

.fade-enter-active,
.fade-leave-active {
	transition: opacity 0.25s ease-out;
}

.fade-enter-from,
.fade-leave-to {
	opacity: 0;
}

</style>
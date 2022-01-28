<template>
	<div v-if="show" class="container-fluid dialog-container m-0 p-0">
		<div class="row dialog-container-row align-items-center justify-content-center">
			<div class="col-4 text-center framed ">
				<div class="container-fluid clean-text dialog-frame">
					<div class="row">
						<div class="col">
							<h4>{{ title }}</h4>
							<p v-html="innerHtml"></p>
						</div>
					</div>
					<div class="row">
						<div class="col">
							<button v-for="button in buttons" :key="button.text" 
								type="button" class="btn me-1" :class="[button.type]" @click="button.onClick">
								{{ button.text }}
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: {
		show: Boolean,
		title: String,
		innerHtml: String,
		buttons: Array
	}
}
</script>

<style>
.dialog-container {
	position: absolute;
	top: 0;
	left: 0;
	height: 100%;
	width: 100%;
}

.dialog-container-row {
	height: 100%;
	width: 100%;
}

.dialog-frame {
	padding: 10px;
}

</style>
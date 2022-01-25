<template>
	<div class="framed">
		<form>
			<div class="row">
				<div class="col-12">
					<h6 class="m-1">Chat</h6>
					<!-- Chat Box Div -->
					<div class="m-1 chat-box clean-text text-start px-1">
						<p v-for="message in messages" :key="message.id">
							<b :style="{color: message.color}">[{{message.username}}]:</b> {{message.text}}
						</p>
					</div>
				</div>
			</div>
			<div class="row mx-1 mb-1">
				<div class="col-9 px-0">
					<input class="form-control chat-entry clean-text" type="text" placeholder="enter your message...">
				</div>
				<div class="col-3 d-grid ps-1 pe-0">
					<button type="button" class="btn btn-primary">Send</button>
				</div>
			</div>
		</form>
	</div>
</template>

<script>
export default {
	name: 'ChatComponent',
	props: {
		isLoggedIn: Boolean
	},
	data() {
		return {
			messages: []
		}
	},
	mounted() {

	}
}
</script>

<style scoped>


.chat-box,
.chat-box[read-only] {
	background-color: white !important;
	font-size: 14px;
	height: 300px;
	resize: vertical;
	overflow: hidden;
}

.chat-box p {
	margin: 0px;
	padding: 0px;
}

.chat-entry {

}

</style>
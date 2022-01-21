<template>
	<div>
		<h4>New Character</h4>
		<form id="character-creation-form">

			<div class="mb-3">
				<input type="text" class="form-control mb-1 text-center" id="character-creation-firstname" v-model="firstName" placeholder="First Name">
				<input type="text" class="form-control text-center" id="character-creation-lastname" v-model="lastName" placeholder="Last Name">
				<div v-if="helpMessage" class="form-text text-danger">{{helpMessage}}</div>
			</div>

			<button @click="createCharacter()" class="btn btn-primary">Create</button>


		</form>
	</div>
</template>

<script>
import $ from 'jquery';

export default {
	name: 'CharacterCreationComponent',
	props: {
		
	},
	emits: ["characterCreationSuccess"],
	data() {
		return {
			firstName: "",
			lastName: "",
			helpMessage: null
		}
	},
	methods: {
		createCharacter() {
			const self = this;

			$.post("/character/create", {
				firstName: this.firstName,
				lastName: this.lastName,
			})
			.done(function(data) {
				if(data.isError) {
					self.helpMessage = data.message;
				}
				else {
					//TODO: handle this properly
					self.helpMessage = data.message;
					self.$emit("characterCreationSuccess");
				}
			});
		}
	},
	mounted() {
		$("#character-creation-form").submit(function(e) {
			e.preventDefault();
		})
	}
}
</script>

<style scoped>

</style>
<template>
	<div class="framed p-3">
		<h4>Login</h4>
		<form id="login-form">

			<div class="mb-3">
				<label for="login-username-input" class="form-label">Username</label>
				<input type="text" class="form-control" id="login-username-input" v-model="username" placeholder="username" autocomplete="off">
			</div>

			<div class="mb-3">
				<label for="login-password-input" class="form-label">Password</label>
				<input type="password" class="form-control" id="login-password-input" v-model="password" placeholder="password" autocomplete="off">
				<div v-if="helpMessage" class="form-text text-danger">{{helpMessage}}</div>
			</div>

			<button @click="login()" class="btn btn-primary">
				<span v-if="formLoading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
				Login
			</button>
			<div class="form-text clickable" @click="$emit('showRegistration')">No account? Click here to register.</div>
			
		</form>
	</div>
</template>

<script>
import $ from 'jquery';

export default {
	name: 'LoginComponent',
	props: {
	},
	emits: ["loginSuccess", "showRegistration"],
	data() {
		return {
			username: "",
			password: "",
			helpMessage: null,
			formLoading: false,
		}
	},
	methods: {
		login() {
			const self = this;

			if(this.username.trim() === "") {
				this.helpMessage = "Please enter your username.";
				return;
			}
			else if(this.password.trim() === "") {
				this.helpMessage = "Please enter your password.";
				return;
			}

			this.helpMessage = null;
			this.formLoading = true;
			$.post(
				"/",
				{
					username: self.username,
					password: self.password
				},
				(data, status, xhr) => {
					var hasError = xhr.getResponseHeader("login-error") === 'true';
					if(hasError) {
						this.helpMessage = "Invalid login credentials.";
					}
					else {
						//TODO: fire login event
						this.$emit("loginSuccess");
						this.helpMessage = "Login successful!";
					}
				}
			)
			.always(function() {
				self.formLoading = false;
			});
		}
	},
	mounted() {
		$("#login-form").submit(function(e) {
			e.preventDefault();
		})
	}
}
</script>

<style scoped>
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
	-webkit-box-shadow: 0 0 0 30px white inset !important;
}
</style>

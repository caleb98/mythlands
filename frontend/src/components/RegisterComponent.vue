<template>
	<div class="framed p-3">
		<h4>Register</h4>
		<form id="registration-form">

			<div class="mb-3">
				<label for="register-username-input" class="form-label">Username</label>
				<input type="text" class="form-control" id="register-username-input" v-model="username" placeholder="username" autocomplete="off">
			</div>

			<div class="mb-3">
				<label for="register-password-input" class="form-label">Password</label>
				<input type="password" class="form-control mb-1" id="register-password-input" v-model="password" placeholder="password" autocomplete="off">
				<input type="password" class="form-control mb-1" id="register-password-confirm-input" v-model="passwordConfirm" placeholder="confirm password" autocomplete="off">
				<div v-if="helpMessage" class="form-text text-danger">{{helpMessage}}</div>
			</div>

			<button @click="register()" class="btn btn-primary mb-2">
				<span v-if="formLoading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
				Register
			</button>
			<div class="form-text clickable" @click="$emit('showLogin');">Already have an account? Click here to login.</div>

		</form>
	</div>
</template>

<script>
import $ from 'jquery';

export default {
	name: 'LoginComponent',
	props: {
	},
	emits: ["registrationSuccess", "showLogin"],
	data() {
		return {
			username: "",
			password: "",
			passwordConfirm: "",
			helpMessage: null,
			formLoading: false,
		}
	},
	methods: {
		register() {
			const self = this;

			// Check that registration info is good to go
			if(!this.validateInput()) {
				return;
			}

			this.helpMessage = null;
			this.formLoading = true;
			// Send registration request
			$.post(
				"/user/register",
				{
					username: self.username,
					password: self.password,
					passwordConfirm: self.passwordConfirm
				}
			)
			.done(function(data) {
				console.log("REGISTER POST DATA:");
				console.log(data);
				if(data.isError) {
					self.helpMessage = data.message;
				}
				else {
					self.$emit("registrationSuccess");
					self.helpMessage = null;
				}
			})
			.always(function(data) {
				self.formLoading = false;
			});
		},

		validateInput() {
			// Validate username length
			if(this.username.length < 8 || this.username.length > 25) {
				this.helpMessage = 'Username must be 8 to 25 characters long.';
				return false;
			}

			// Validate username characters
			var usernameRegex = /^[A-Za-z0-9]*$/;
			if(!usernameRegex.test(this.username)) {
				this.helpMessage = 'Username contains an invalid character. Only letters and numbers may be used.';
				return false;
			}

			// Validate email
			// var emailRegex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			// if(!emailRegex.test(this.email)) {
			// 	this.error = 'Invalid email.';
			// 	return false;
			// }

			// Validate password
			if(this.password.length < 10 || this.password.length > 75) {
				this.helpMessage = 'Password must be 10 to 75 characters in length.';
				return false;
			}

			if(this.password !== this.passwordConfirm) {
				this.helpMessage = 'Passwords do not match.';
				return false;
			}

			this.helpMessage = null;
			return true;
		}
	},
	mounted() {
		$("#registration-form").submit(function(e) {
			e.preventDefault();
		})
	}
}
</script>

<style scoped>
.clickable {
	cursor: pointer;
}

input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
	-webkit-box-shadow: 0 0 0 30px white inset !important;
}
</style>

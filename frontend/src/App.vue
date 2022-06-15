<template>
	<div class="container mb-3">
		<div class="row">
			<h1 class="text-center" style="font-family: 'Mochiy Pop P One', sans-serif;">Mythlands</h1>
		</div>
	
		<!-- Boss Frame -->
		<div v-if="bossInfo.name" class="row justify-content-center">
			<div class="col-12 col-lg-6">
				<h4 class="text-center">{{bossInfo.name}}</h4>
				<div class="progress" style="height: 40px">
					<div class="progress-bar bg-health" role="progressbar" :style="{ width: bossHealthWidth + '%'}"></div>
				</div>
				<p class="text-center">{{bossInfo.currentHealth}} / {{bossInfo.maxHealth}}</p>
			</div>
		</div>

		<!-- Main Content -->
		<div class="row justify-content-center">

			<!-- Login/Profile -->
			<div class="col-12 col-lg-6 mb-2">
				<transition name="component-fade" mode="out-in">
					<component v-if="activeView" :is="activeView" v-on="viewData.listeners" v-bind="viewData.properties"></component>
				</transition>
			</div>

			<!-- Chat -->
			<div class="col-12 col-lg-6 mb-2" v-if="isLoggedIn">
				<ChatComponent class="mb-2"/>
			</div>

			<!-- Leaderboard -->
			<div class="col-12 col-lg-6 mb-2">
				<Leaderboard page-size="10"/>
			</div>

			<!-- System Messages -->
			<div class="col-12 col-lg-6">
				<div class="framed pt-1 px-0">
					<table class="table table-striped mb-0">
						<thead>
							<tr>
								<th scope="col">Messages</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="message in gameMessages" :key="message.bossName">
								<td>{{message.bossName}} was slain by {{message.killedBy}}!</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<!-- Messages -->


	</div>
</template>

<script>
import { map } from 'rxjs/operators'
import $ from 'jquery';
import Cookies from 'js-cookie';
import WS from './services/wsclient';
import LoginComponent from './components/LoginComponent.vue';
import RegisterComponent from './components/RegisterComponent.vue';
import PlayerDashboard from './components/PlayerDashboard.vue';
import ChatComponent from './components/ChatComponent.vue';
import Leaderboard from './components/Leaderboard.vue';

// Setup csrf token
$(function () {
	// eslint-disable-next-line no-unused-vars
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader("X-XSRF-TOKEN", Cookies.get("XSRF-TOKEN"));
	});
});

export default {
	name: 'App',
	components: {
    LoginComponent,
    RegisterComponent,
    PlayerDashboard,
    ChatComponent,
	Leaderboard
},
	data() {
		return {
			isLoggedIn: false,
			activeView: null,

			// Game data
			bossInfo: {
				name: "",
				maxHealth: 0,
				currentHealth: 0
			},
			gameMessages: []
		}
	},
	methods: {
		showDashboard() {
			this.activeView = "PlayerDashboard";
		},

		showLogin() {
			this.activeView = "LoginComponent";
		},

		showRegistration() {
			this.activeView = "RegisterComponent";
		},

		onLogin() {
			this.isLoggedIn = true;
			this.showDashboard();
			WS.reconnect();
		},

		onLogout() {
			this.isLoggedIn = false;
			this.showLogin();
			WS.reconnect();
		},

		doLoginCheck() {
			const self = this;

			$.get("/user/info", function(data) {
				// Not logged in
				if(data.isError) {
					self.showLogin();
				}
				// Logged in
				else {
					self.isLoggedIn = true;
					self.showDashboard();
				}
			});
		}
	},
	computed: {
		bossHealthWidth() {
			if(this.bossInfo.maxHealth == 0) {
				return 100;
			}
			else {
				return Math.round(this.bossInfo.currentHealth / this.bossInfo.maxHealth * 100);
			}
		},

		viewData() {
			if(this.activeView === "LoginComponent") {
				return {
					listeners: {
						loginSuccess: this.onLogin,
						showRegistration: this.showRegistration
					},
					properties: {},
				};
			}
			else if(this.activeView === "RegisterComponent") {
				return {
					listeners: {
						registrationSuccess: this.onLogin,
						showLogin: this.showLogin
					},
					properties: {},
				};
			}
			else if(this.activeView === "PlayerDashboard") {
				return {
					listeners: {
						loggedOut: this.onLogout
					},
					properties: {
						bossId: this.bossInfo.id
					}
				}
			}
			else {
				return null;
			}
		}
	},
	mounted() {
		const self = this;	
		
		// Check the login status and show appropriate components
		this.doLoginCheck();

		// Grab initial boss info
		$.get("/game/bossinfo", function(data) {
			self.bossInfo = data
		})

		// Setup websocket callbacks
		this.bossStatusSub = WS.watch("/global/boss.status")
			.pipe(map(message => {
				return JSON.parse(message.body);
			}))
			.subscribe(data => {
				self.bossInfo = data;
			});
		
		this.bossDeathSub = WS.watch("/global/boss.died")
			.pipe(map(message => {
				return JSON.parse(message.body);
			}))
			.subscribe(data => {
				self.gameMessages.unshift(data);
			});
	}
}
</script>

<style>
#app {
	font-family: 'Mochiy Pop P One', Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	margin-top: 20px;
}

.component-fade-enter-active,
.component-fade-leave-active {
	transition: opacity 0.2s ease;
}

.component-fade-enter-from,
.component-fade-leave-to {
	opacity: 0;
}


</style>

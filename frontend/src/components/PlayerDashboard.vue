<template>
	<div>
		<div class="container-fluid pt-3 pb-3 framed mb-2" v-if="activeCharacter == null">
			<div class="row justify-content-center">
				<div class="col-6">
					<CharacterCreationComponent @characterCreationSuccess="loadUserCharacters()"/>
				</div>
			</div>
		</div>
		<div class="container-fluid p-2 framed mb-2" style="position: relative;" v-if="activeCharacter != null">
			<div class="row">
				<div class="col">
					<button type="button" class="btn btn-danger" @click="attack()" :disabled="cooldownTime > 0">Attack</button>
				</div>
			</div>
			<div id="recharge-box" :style="{ 'animation-duration': cooldownTime + 's' }"></div>
		</div>
		<div class="container-fluid pt-3 pb-3 framed mb-2" v-if="activeCharacter != null">
			<div class="row justify-content-center mb-4 character-name">
				<div class="col text-center">
					<b class="fs-4">{{fullCharacterName}}</b>
				</div>
				<i class="username">{{userInfo.username}}</i>
			</div>
			<div class="row mb-3 align-items-center">
				<div class="col-4 p-0">
					<div class="mb-2"><b>Level {{activeCharacter.level}}</b></div>
					<img src="../assets/hero.png" width="64" height="64" class="pixel-image">
				</div>
				<div class="col-4 p-0">
					<div class="container-fluid p-0">
						<div class="row justify-content-between">
							<div class="col-6"><b>STR</b></div>
							<div class="col-6">{{activeCharacter.strength}}</div>
						</div>
						<div class="row justify-content-between">
							<div class="col-6"><b>DEX</b></div>
							<div class="col-6">{{activeCharacter.dexterity}}</div>
						</div>
						<div class="row justify-content-between">
							<div class="col-6"><b>ATT</b></div>
							<div class="col-6">{{activeCharacter.attunement}}</div>
						</div>
					</div>
				</div>
				<div class="col-4 p-0">
					<div class="container-fluid  p-0">
						<div class="row justify-content-between">
							<div class="col-6"><b>TGH</b></div>
							<div class="col-6">{{activeCharacter.toughness}}</div>
						</div>
						<div class="row justify-content-between">
							<div class="col-6"><b>AVD</b></div>
							<div class="col-6">{{activeCharacter.avoidance}}</div>
						</div>
						<div class="row justify-content-between">
							<div class="col-6"><b>RES</b></div>
							<div class="col-6">{{activeCharacter.resistance}}</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row" v-if="activeCharacter != null">
				<div class="col-12">
					<div class="progress resource-bar border-health mb-1">
						<div class="progress-bar progress-bar-striped progress-bar-animated bg-health" :class="{'bg-health-cap': showHealthCap}" role="progressbar" :style="{width: healthPercent + '%'}"></div>
						<div class="progress-bar-title">{{healthString}}</div>
					</div>
					<div class="progress resource-bar border-mana mb-1">
						<div class="progress-bar progress-bar-striped progress-bar-animated bg-mana" :class="{'bg-mana-cap': showManaCap}" role="progressbar" :style="{width: manaPercent + '%'}"></div>
						<div class="progress-bar-title">{{manaString}}</div>
					</div>
					<div class="progress resource-bar border-xp">
						<div class="progress-bar progress-bar-striped progress-bar-animated bg-xp" :class="{'bg-xp-cap': showXpCap}" role="progressbar" :style="{width: xpPercent + '%'}"></div>
						<div class="progress-bar-title">{{xpString}}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="container framed p-2">
			<div class="row">
				<div class="col-auto">
					<button @click="logout()" class="btn btn-primary">Logout</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { map, filter } from 'rxjs/operators'
import $ from 'jquery';
import WS from '../services/wsclient';
import CharacterCreationComponent from './CharacterCreationComponent.vue';

export default {
	name: 'PlayerDashboard',
	components: {
		CharacterCreationComponent
	},
	data() {
		return {
			characters: [],
			userInfo: null,
			cooldownTime: 0
		}
	},
	emits: ["loggedOut"],
	methods: {
		loadUserCharacters() {
			const self = this;
			$.get("/character/list", function(data) {
				if(data.isError) {
					console.log("Error retrieving character list: " + data.message);
					return;
				}
				
				self.characters = data.data;
			});
		},

		loadUserInfo() {
			const self = this;
			$.get("/user/info", function(data) {
				if(data.isError) {
					console.log("Error retrieving user info: " + data.message);
					return;
				}

				self.userInfo = data.data;
			});
		},

		logout() {
			const self = this;
			$.post("/logout", function(data) {
				self.$emit("loggedOut");
			});
		},

		attack() {
			WS.publish({
				destination: "/game/attack"
			});
		}
	},
	computed: {
		activeCharacter() {
			if(this.characters == null) {
				return null;
			}
			else {
				return this.characters.filter(character => !character.isDeceased)[0];
			}
		},

		xpToLevel() {
			return 9 + this.activeCharacter.level;
		},

		fullCharacterName() {
			return this.activeCharacter.firstName + " " + this.activeCharacter.lastName;
		},

		healthString() {
			return this.activeCharacter.currentHealth + " / " + this.activeCharacter.maxHealth;
		},

		manaString() {
			return this.activeCharacter.currentMana + " / " + this.activeCharacter.maxMana;
		},

		xpString() {
			return this.activeCharacter.xp + " / " + this.xpToLevel;
		},

		healthPercent() {
			return Math.round(this.activeCharacter.currentHealth / this.activeCharacter.maxHealth * 100);
		},

		manaPercent() {
			return Math.round(this.activeCharacter.currentMana / this.activeCharacter.maxMana * 100);
		},

		xpPercent() {
			return Math.round(this.activeCharacter.xp / this.xpToLevel * 100);
		},

		showHealthCap() {
			return this.healthPercent < 100 && this.healthPercent > 0;
		},

		showManaCap() {
			return this.manaPercent < 100 && this.manaPercent > 0;
		},

		showXpCap() {
			return this.xpPercent < 100 && this.xpPercent > 0;
		},
	},

	watch: {
		cooldownTime(val) {
			if(val > 0) {
				$("#recharge-box").addClass("cooldown-active");
				setTimeout(() => {
					$("#recharge-box").removeClass("cooldown-active");
					this.cooldownTime = 0;
				}, this.cooldownTime * 1000);
			}
		}
	},

	mounted() {
		this.loadUserInfo();
		this.loadUserCharacters();

		this.characterStatusSub = WS.watch("/user/local/character")
			.pipe(map(message => {
				return JSON.parse(message.body);
			}))
			.subscribe(body => {
				var character = this.characters.filter(c => !c.isDeceased)[0];
				Object.assign(character, ...body.updates);
			});

		this.cooldownSub = WS.watch("/user/local/cooldown")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(body => {
				this.cooldownTime = body.attackCooldown;
			});
	},

	unmounted() {
		this.characterStatusSub.unsubscribe();
		this.cooldownSub.unsubscribe();
	}
}
</script>

<style scoped>
.framed {
	border-radius: 5px;
	background-color: #f0f0f0;
}

.resource-bar {
	height: 20px;
	background-color: #a5a5a5;
}

.border-health {
	border: 2px hsl(354, 70%, 30%) solid;
}

.bg-health {
	background-color: rgb(220, 53, 69);
}

.bg-health-cap {
	border-right: 1px hsl(354, 70%, 30%) solid;
}

.border-mana {
	border: 2px hsl(221, 100%, 33%) solid;
}

.bg-mana {
	background-color: rgb(41, 109, 255);
}

.bg-mana-cap {
	border-right: 1px hsl(221, 100%, 33%) solid;
}

.border-xp {
	border: 2px hsl(278, 70%, 30%) solid;
}

.bg-xp {
	background-color: rgb(156, 48, 219);
}

.bg-xp-cap {
	border-right: 1px hsl(278, 70%, 30%) solid;
}

.character-name {
	position: relative;
}

.username {
	font-size: 10pt;
	position: absolute;
	top: 80%;
}

.pixel-image {
	image-rendering: pixelated;
	image-rendering: crisp-edges;
}

.progress {
	position: relative;
}

.progress-bar-title {
	position: absolute;
	text-align: center;
	line-height: 20px;
	overflow: hidden;
	right: 0;
	left: 0;
	top: 0;
	color: black;
	font-size: 16px;
	font-weight: bold;
}

#recharge-box {
	position: absolute;
	background-color: rgba(236, 58, 58, 0.575);
	width: 0%;
	height: 100%;
	top: 0px;
	left: 0px;
	border-radius: 5px;
}

.cooldown-active {
	animation-name: cooldown;
	animation-timing-function: linear;
}

@keyframes cooldown {
	from { width: 100%; }
	to { width: 0%; }
}
</style>

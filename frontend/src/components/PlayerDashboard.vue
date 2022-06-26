<template>
	<div>
		<!-- Attack Buttons -->
		<div class="container-fluid p-2 framed mb-2 position-relative" v-if="activeCharacter != null">
			<div class="row">
				<div class="col">
					<button type="button" class="btn btn-danger" @click="attack()" :disabled="cooldownTime > 0 || isCharacterDeceased">Attack</button>
				</div>
			</div>
			<div id="recharge-box" :style="{ 'animation-duration': cooldownTime + 's' }"></div>
		</div>

		<!-- Character Creation -->
		<div class="container-fluid pt-3 pb-3 framed mb-2" v-if="showCharacterCreator">
			<div class="row justify-content-center">
				<div class="col-6">
					<CharacterCreationComponent @characterCreationSuccess="loadUserCharacters()"/>
				</div>
			</div>
		</div>

		<!-- Active Character Display -->
		<div class="container-fluid pt-3 pb-3 framed mb-2 position-relative" v-if="activeCharacter != null">

			<!-- Death Overlay -->
			<div id="death-overlay" class="container-fluid" v-if="activeCharacter.isDeceased">
				<div class="row justify-content-center align-items-center" style="height: 100%">
					<div class="col">
						<h3 class="text-center"><b>Dead</b></h3>
						<p><b><i>I was one day from retirement...</i><br> - {{fullCharacterName}}</b></p>
					</div>
				</div>
			</div>

			<!-- Character Name and Username -->
			<div class="row justify-content-center mb-4 character-name">
				<div class="col text-center">
					<b class="fs-4">{{fullCharacterName}}</b>
				</div>
				<i class="username">{{userInfo.username}}</i>
			</div>

			<!-- Stats & Image -->
			<div class="row mb-3 align-items-center mx-0">
				<div class="col-4 p-0">
					<div class="mb-2"><b>Level {{activeCharacter.level}}</b></div>
					<img src="/img/hero.png" width="64" height="64" class="pixel-image">
				</div>
				<div class="col-8 p-0">
					<table class="table align-middle stats-table mb-0 clean-text">
						<tbody>
							<tr>
								<td class="text-start"><b>Stamina</b></td>
								<td class="text-end">{{activeCharacter.stamina}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('STAMINA')">add</span>
								</td>

								<td class="text-start ps-4"><b>Spirit</b></td>
								<td class="text-end">{{activeCharacter.spirit}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('SPIRIT')">add</span>
								</td>
							</tr>
							<tr>
								<td class="text-start"><b>Strength</b></td>
								<td class="text-end">{{activeCharacter.strength}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('STRENGTH')">add</span>
								</td>

								<td class="text-start ps-4"><b>Toughness</b></td>
								<td class="text-end">{{activeCharacter.toughness}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('TOUGHNESS')">add</span>
								</td>
							</tr>
							<tr>
								<td class="text-start"><b>Dexterity</b></td>
								<td class="text-end">{{activeCharacter.dexterity}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('DEXTERITY')">add</span>
								</td>

								<td class="text-start ps-4"><b>Avoidance</b></td>
								<td class="text-end">{{activeCharacter.avoidance}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('AVOIDANCE')">add</span>
								</td>
							</tr>
							<tr>
								<td class="text-start"><b>Attunement</b></td>
								<td class="text-end">{{activeCharacter.attunement}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('ATTUNEMENT')">add</span>
								</td>

								<td class="text-start ps-4"><b>Resistance</b></td>
								<td class="text-end">{{activeCharacter.resistance}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('RESISTANCE')">add</span>
								</td>
							</tr>
							<tr v-if="hasSkillPoints">
								<td colspan="6" class="pb-0">
									<span class="text-skill-points ps-4 pe-4 pt-1 pb-1">{{activeCharacter.skillPoints}} Skill Point{{activeCharacter.skillPoints > 1 ? 's' : ''}}</span>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>

			<!-- Bars -->
			<div class="row">
				<div class="col-12">
					<div class="progress resource-bar border-health mb-1">
						<div class="progress-bar progress-bar-striped bg-health" 
							:class="{'bg-health-cap': showHealthCap, 'progress-bar-animated': !activeCharacter.isDeceased}" role="progressbar" :style="{width: healthPercent + '%'}">
						</div>
						<div class="progress-bar-title">{{healthString}}</div>
					</div>
					<div class="progress resource-bar border-mana mb-1">
						<div class="progress-bar progress-bar-striped bg-mana" 
							:class="{'bg-mana-cap': showManaCap, 'progress-bar-animated': !activeCharacter.isDeceased}" role="progressbar" :style="{width: manaPercent + '%'}">
						</div>
						<div class="progress-bar-title">{{manaString}}</div>
					</div>
					<div class="progress resource-bar border-xp">
						<div class="progress-bar progress-bar-striped bg-xp" 
							:class="{'bg-xp-cap': showXpCap, 'progress-bar-animated': !activeCharacter.isDeceased}" role="progressbar" :style="{width: xpPercent + '%'}">
						</div>
						<div class="progress-bar-title">{{xpString}}</div>
					</div>
				</div>
			</div>

			<!-- Inventory -->
			<div class="row inventory-frame mx-0 mt-2">
				<div class="col-12">
					<div class="row">
						<div class="col-auto p-0" v-for="(n, slot) in activeCharacter.inventoryCapacity" :key="slot">
							<ItemIconComponent :displayItem="activeCharacterInventory[slot]" :itemSlot="slot"/>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid framed p-2">
			<div class="row">
				<div class="col-auto" v-if="isCharacterDeceased && !showCharacterCreator">
					<button @click="showCharacterCreator = true" class="btn btn-primary">New Character</button>
				</div>
				<div class="col-auto ms-auto">
					<button @click="logout()" class="btn btn-primary">Logout</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { map } from 'rxjs/operators'
import $ from 'jquery';
import WS from '../services/wsclient';
import CharacterCreationComponent from './CharacterCreationComponent.vue';
import ItemIconComponent from './ItemIconComponent.vue';

export default {
	name: 'PlayerDashboard',
	components: {
		CharacterCreationComponent,
		ItemIconComponent
	},
	data() {
		return {
			characters: [],
			activeCharacterId: -1,
			activeCharacterInventory: [],
			userInfo: null,
			cooldownTime: 0,
			showCharacterCreator: false,
		}
	},
	props: {
		bossId: Number
	},
	emits: ["loggedOut"],
	methods: {
		loadUserCharacters() {
			$.get("/character/list", response => {
				if(response.isError) {
					console.log("Error retrieving character list: " + response.message);
					return;
				}
				
				this.characters = response.data.characters;
				this.activeCharacterId = response.data.activeCharacterId;
				
				if(this.activeCharacterId == -1) {
					this.showCharacterCreator = true;
				}
				else {
					this.showCharacterCreator = false;
				}
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

		loadInventory() {
			const self = this;
			$.get("/character/inventory", function(data) {
				if(data.isError) {
					console.log("Error retrieving character inventory: " + data.message);
					return;
				}

				self.activeCharacterInventory = data.data;
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
				destination: "/game/attack",
				body: JSON.stringify({
					bossId: this.bossId
				})
			});
		},

		spendSkillPoint(skill) {
			WS.publish({
				destination: "/game/character.skillup",
				body: JSON.stringify({
					skill: skill
				})
			})
			// This is done server side, but we locally subtract just to
			// make the UI snappier and prevent accidental message sends.
			this.activeCharacter.skillPoints -= 1;
		}
	},

	computed: {
		activeCharacter() {
			if(this.activeCharacterId == -1) {
				return null;
			}
			else {
				return this.characters.filter(character => character.id == this.activeCharacterId)[0];
			}
		},

		isCharacterDeceased() {
			return this.activeCharacter == null ? false : this.activeCharacter.isDeceased;
		},

		xpToLevel() {
			return 10 + (10 * (this.activeCharacter.level - 1));
		},

		fullCharacterName() {
			return this.activeCharacter.firstName + " " + this.activeCharacter.lastName;
		},

		healthString() {
			return parseFloat(this.activeCharacter.currentHealth.toFixed(2)) + " / " + this.activeCharacter.maxHealth;
		},

		manaString() {
			return parseFloat(this.activeCharacter.currentMana.toFixed(2)) + " / " + this.activeCharacter.maxMana;
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

		hasSkillPoints() {
			return this.activeCharacter.skillPoints > 0;
		}
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
		this.loadInventory();

		this.characterStatusSub = WS.watch("/user/local/character")
			.pipe(map(message => {
				return JSON.parse(message.body);
			}))
			.subscribe(body => {
				var character = this.activeCharacter;
				Object.assign(character, ...body.updates);
			});

		this.cooldownSub = WS.watch("/user/local/cooldown")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(body => {
				this.cooldownTime = body.attackCooldown;
			});

		this.inventorySub = WS.watch("/user/local/inventory")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(changes => {
				for(var slot in changes) {
					if(changes[slot] == null) {
						delete this.activeCharacterInventory[slot];
					}
					else {
						this.activeCharacterInventory[slot] = changes[slot];
					}
				}
			});
	},

	unmounted() {
		this.characterStatusSub.unsubscribe();
		this.cooldownSub.unsubscribe();
		this.inventorySub.unsubscribe();
	}
}
</script>

<style scoped>
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
	overflow: hidden;
	right: 0;
	left: 0;
	top: -2px;
	color: black;
	font-size: 12px;
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

.stats-table td {
	border-bottom-width: 0px;
	padding: 2px 4px 2px 4px;
}

.text-skill-points {
	background-color: orange;
	border-radius: 3px;
}

.btn-skill {
	background-color: orange;
	padding: 4px;
	border-radius: 5px;
	animation-name: btn-skill-glow;
	animation-duration: 2s;
	animation-iteration-count: infinite;
	animation-direction: alternate-reverse;
	animation-timing-function: ease-in-out;
	will-change: transform, opacity;
}

@keyframes btn-skill-glow {
	from { box-shadow: 0px 0px 7px 2px orange; }
	to { box-shadow: none; }
}

#death-overlay {
	position: absolute;
	z-index: 99;
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	border-radius: 5px;
	backdrop-filter: blur(2px) grayscale(100%);
	background-color: rgba(255, 0, 0, 0.30);
}

.inventory-frame {
	border: 1px solid black;
	border-radius: 5px;
	box-shadow: 1px 1px 1px black;
	background-color: #977749;
	height: 200px;
	overflow-y: auto;
}

.inventory-frame::-webkit-scrollbar {
	width: 10px;
}

.inventory-frame::-webkit-scrollbar-track {
	background: #00000000;
}

.inventory-frame::-webkit-scrollbar-thumb {
	transition: background 2s ease-in-out;
	background: #c7a679;
	border-radius: 5px;
}

.inventory-frame::-webkit-scrollbar-thumb:hover {
	background: #ac9069;
}
</style>
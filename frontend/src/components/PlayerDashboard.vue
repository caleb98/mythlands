<template>
	<div>
		<!-- Attack Buttons -->
		<div class="container-fluid p-2 framed mb-2 position-relative" v-if="hasActiveCharacter">
			<div class="row">
				<div class="col">
					<button type="button" class="btn btn-danger" @click="attack()" :disabled="cooldownTime > 0 || characterInfo.isDeceased">Attack</button>
				</div>
			</div>
			<div id="recharge-box" :style="{ 'animation-duration': cooldownTime + 's' }"></div>
		</div>

		<!-- Character Creation -->
		<div class="container-fluid pt-3 pb-3 framed mb-2" v-if="showCharacterCreator">
			<div class="row justify-content-center">
				<div class="col-6">
					<CharacterCreationComponent @characterCreationSuccess="loadActiveCharacter()"/>
				</div>
			</div>
		</div>

		<!-- Active Character Display -->
		<div class="container-fluid pt-3 pb-3 framed mb-2 position-relative" v-if="hasActiveCharacter && !showCharacterCreator">

			<!-- Death Overlay -->
			<div id="death-overlay" class="container-fluid" v-if="characterInfo.isDeceased">
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
			<div class="row mb-3 mx-0 align-items-center justify-content-evenly">
				<div class="col-auto p-0">
					<div class="row">
						<div class="col-auto">
							<ItemIconComponent :displayItem="characterEquipment.weaponItem" itemSlot="weapon-item" />
						</div>
					</div>
					<div class="row">
						<div class="col-auto">
							<ItemIconComponent :displayItem="characterEquipment.armorItem" itemSlot="armor-item" />
						</div>
					</div>
					<div class="row">
						<div class="col-auto">
							<ItemIconComponent :displayItem="characterEquipment.trinketItem" itemSlot="trinket-item" />
						</div>
					</div>
				</div>
				<div class="col-auto p-0">
					<div class="mb-2"><b>Level {{characterStats.level}}</b></div>
					<img src="/img/hero.png" width="64" height="64" class="pixel-image">
				</div>
				<div class="col-auto p-0">
					<table class="table align-middle stats-table mb-0 clean-text">
						<tbody>
							<tr>
								<td class="text-start"><b>Stamina</b></td>
								<td class="text-end">{{characterStats.stamina}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('STAMINA')">add</span>
								</td>

								<td class="text-start ps-4"><b>Spirit</b></td>
								<td class="text-end">{{characterStats.spirit}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('SPIRIT')">add</span>
								</td>
							</tr>
							<tr>
								<td class="text-start"><b>Strength</b></td>
								<td class="text-end">{{characterStats.strength}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('STRENGTH')">add</span>
								</td>

								<td class="text-start ps-4"><b>Toughness</b></td>
								<td class="text-end">{{characterStats.toughness}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('TOUGHNESS')">add</span>
								</td>
							</tr>
							<tr>
								<td class="text-start"><b>Dexterity</b></td>
								<td class="text-end">{{characterStats.dexterity}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('DEXTERITY')">add</span>
								</td>

								<td class="text-start ps-4"><b>Avoidance</b></td>
								<td class="text-end">{{characterStats.avoidance}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('AVOIDANCE')">add</span>
								</td>
							</tr>
							<tr>
								<td class="text-start"><b>Attunement</b></td>
								<td class="text-end">{{characterStats.attunement}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('ATTUNEMENT')">add</span>
								</td>

								<td class="text-start ps-4"><b>Resistance</b></td>
								<td class="text-end">{{characterStats.resistance}}</td>
								<td v-if="hasSkillPoints" class="m-0 p-0">
									<span class="material-icons-round md-18 btn-skill clickable" @click="spendSkillPoint('RESISTANCE')">add</span>
								</td>
							</tr>
							<tr v-if="hasSkillPoints">
								<td colspan="6" class="pb-0">
									<span class="text-skill-points ps-4 pe-4 pt-1 pb-1">{{characterStats.skillPoints}} Skill Point{{characterStats.skillPoints > 1 ? 's' : ''}}</span>
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
							:class="{'bg-health-cap': showHealthCap, 'progress-bar-animated': !characterInfo.isDeceased}" role="progressbar" :style="{width: healthPercent + '%'}">
						</div>
						<div class="progress-bar-title">{{healthString}}</div>
					</div>
					<div class="progress resource-bar border-mana mb-1">
						<div class="progress-bar progress-bar-striped bg-mana" 
							:class="{'bg-mana-cap': showManaCap, 'progress-bar-animated': !characterInfo.isDeceased}" role="progressbar" :style="{width: manaPercent + '%'}">
						</div>
						<div class="progress-bar-title">{{manaString}}</div>
					</div>
					<div class="progress resource-bar border-xp">
						<div class="progress-bar progress-bar-striped bg-xp" 
							:class="{'bg-xp-cap': showXpCap, 'progress-bar-animated': !characterInfo.isDeceased}" role="progressbar" :style="{width: xpPercent + '%'}">
						</div>
						<div class="progress-bar-title">{{xpString}}</div>
					</div>
				</div>
			</div>

			<!-- Inventory -->
			<div class="row inventory-frame mx-0 mt-2">
				<div class="col-12">
					<div class="row">
						<div class="col-auto p-0" v-for="(n, slot) in characterInventory.capacity" :key="slot">
							<ItemIconComponent :displayItem="characterInventory.items[slot]" :itemSlot="'' + slot"/>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- Bottom Menu Buttons -->
		<div class="container-fluid framed p-2">
			<div class="row">
				<div class="col-auto" v-if="characterInfo.isDeceased && !showCharacterCreator">
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
			// Currently logged-in user info
			userInfo: null,
			hasActiveCharacter: false,
			showCharacterCreator: false,

			// Primary Character information
			characterInfo: { 
				timestamp: 0,

				firstName: "",
				lastName: "",
				isDeceased: false,
				ownerName: "",
			},

			// Character Inventory
			characterInventory: {
				capacity: 0,
				items: new Map(),
			},

			// Character Equipment
			characterEquipment: {
				timestamp: 0,

				weaponItem: null,
				armorItem: null,
				trinketItem: null,
			},

			// Character Stats
			characterStats: { 
				timestamp: 0,
				
				attackReady: 0,
				skillPoints: 0,

				level: 0,
				xp: 0,
				
				maxHealth: 0,
				currentHealth: 0,
				maxMana: 0,
				currentMana: 0,

				stamina: 0,
				spirit: 0,
				strength: 0,
				dexterity: 0,
				attunement: 0,
				toughness: 0,
				avoidance: 0,
				resistance: 0,

				goldGain: 0,
				xpGain: 0,
				attackCooldown: 0,
			},

			// Character effects
			characterEffects: { 
				timestamp: 0,
				effects: []
			},

		}
	},

	props: {
		bossId: Number
	},

	emits: ["loggedOut"],

	methods: {
		loadUserInfo() {
			const self = this;
			$.get("/user", function(data) {
				if(data.isError) {
					console.log("Error retrieving user info: " + data.message);
					return;
				}

				self.userInfo = data.data;
			});
		},

		loadActiveCharacter() {
			const self = this;
			$.get("/user/character", function(data) {
				if(data.isError) {
					self.showCharacterCreator = true;
				}
				else {
					let character = data.data.data; // .data.data.data.data.....
					self.hasActiveCharacter = true;
					self.showCharacterCreator = false;

					// Update timestamps
					self.characterInfo.timestamp = data.data.timestamp;
					self.characterEquipment.timestamp = data.data.timestamp;
					self.characterStats.timestamp = data.data.timestamp;
					self.characterEffects.timestamp = data.data.timestamp;

					// Update object data
					Object.assign(self.characterInfo, character.info);
					Object.assign(self.characterEquipment, character.equipment);
					Object.assign(self.characterStats, character.stats);
					Object.assign(self.characterEffects, character.effects);
					Object.assign(self.characterInventory, character.inventory);
				}
			})
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
			this.characterStats.skillPoints -= 1;
		}
	},

	computed: {
		xpToLevel() {
			return 10 + (10 * (this.characterStats.level - 1));
		},

		fullCharacterName() {
			return this.characterInfo.firstName + " " + this.characterInfo.lastName;
		},

		healthString() {
			return parseFloat(this.characterStats.currentHealth.toFixed(2)) + " / " + this.characterStats.maxHealth;
		},

		manaString() {
			return parseFloat(this.characterStats.currentMana.toFixed(2)) + " / " + this.characterStats.maxMana;
		},

		xpString() {
			return this.characterStats.xp + " / " + this.xpToLevel;
		},

		healthPercent() {
			return Math.round(this.characterStats.currentHealth / this.characterStats.maxHealth * 100);
		},

		manaPercent() {
			return Math.round(this.characterStats.currentMana / this.characterStats.maxMana * 100);
		},

		xpPercent() {
			return Math.round(this.characterStats.xp / this.xpToLevel * 100);
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
			return this.characterStats.skillPoints > 0;
		},

		cooldownTime() {
			return (this.characterStats.attackReady - Date.now()) / 1000;
		}
	},

	watch: {
		cooldownTime(val) {
			if(val > 0) {
				$("#recharge-box").addClass("cooldown-active");
				setTimeout(() => {
					$("#recharge-box").removeClass("cooldown-active");
					this.characterStats.attackReady = 0;
				}, this.cooldownTime * 1000);
			}
		},

		activeCharacterId() {
			this.loadInventory();
		}
	},

	mounted() {
		this.loadUserInfo();
		this.loadActiveCharacter();

		this.characterInfoSub = WS.watch("/user/local/character.info")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(body => {
				if(body.timestamp > this.characterInfo.timestamp) {
					this.characterInfo.timestamp = body.timestamp;
					Object.assign(this.characterInfo, body.data);
				}
			});

		this.characterStatsSub = WS.watch("/user/local/character.stats")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(body => {
				console.log(body.timestamp + ":" + JSON.stringify(body.data, null, 2))
				if(body.timestamp > this.characterStats.timestamp) {
					this.characterStats.timestamp = body.timestamp;
					Object.assign(this.characterStats, body.data);
				}
			});

		this.characterEffectsSub = WS.watch("/user/local/character.effects")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(body => {
				if(body.timestamp > this.characterEffects.timestamp) {
					this.characterEffects.timestamp = body.timestamp;
					Object.assign(this.characterEffects, body.data);
				}
			});

		this.inventorySub = WS.watch("/user/local/character.inventory")
			.pipe(map(message => {
				return JSON.parse(message.body)
			}))
			.subscribe(changes => {
				for(var slot in changes) {
					if(changes[slot] == null) {
						delete this.characterInventory.items[slot];
					}
					else {
						this.characterInventory.items[slot] = changes[slot];
					}
				}
			});

		this.equipmentSub = WS.watch("/user/local/character.equipment")
			.pipe(map(message => {
				return JSON.parse(message.body);
			}))
			.subscribe(body => {
				if(body.timestamp > this.characterEquipment.timestamp) {
					this.characterEquipment.timestamp = body.timestamp;
					Object.assign(this.characterEquipment, body.data);
				}
			});
	},

	unmounted() {
		this.characterInfoSub.unsubscribe();
		this.characterStatsSub.unsubscribe();
		this.characterEffectsSub.unsubscribe();
		this.inventorySub.unsubscribe();
		this.equipmentSub.unsubscribe();
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
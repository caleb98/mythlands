<template>
	<div class="framed">
		<form>
			<div class="row">
				<div class="col-12">
					<h6 class="m-1">Chat {{scrollPos}}</h6>
					<!-- Chat Box Div -->
					<div id="chat-frame" class="m-1 chat-box clean-text text-start px-1">
						<div id="chat-container">
							<p v-for="message in messages" :key="message.id">
								<b>{{message.timestamp}} [{{message.username}}]:</b> {{message.message}}
							</p>
						</div>
					</div>
				</div>
			</div>
			<div class="row mx-1 mb-1">
				<div class="col-9 px-0">
					<input id="message-input" class="form-control chat-entry clean-text" type="text" @keydown.enter.prevent="sendMessage" v-model="messageText" placeholder="enter your message..." autocomplete="off">
				</div>
				<div class="col-3 d-grid ps-1 pe-0">
					<button @click="sendMessage" type="button" class="btn btn-primary">Send</button>
				</div>
			</div>
		</form>
	</div>
</template>

<script>
import $ from 'jquery';
import WS from '../services/wsclient';
import { map } from 'rxjs/operators';

export default {
	name: 'ChatComponent',

	props: {
		isLoggedIn: Boolean
	},

	data() {
		return {
			messages: [],
			messageText: "",
			timeFormatter: new Intl.DateTimeFormat([], {
				hour12: true,
				hour: "numeric",
				minute: "numeric",
				second: "numeric"
			}),
			scrollTop: 0,
		}
	},

	methods: {
		sendMessage() {
			WS.publish({
				destination: "/game/chat",
				body: JSON.stringify({
					groupId: 1,
					message: this.messageText
				})
			})

			this.messageText = "";
		},

		getScrollPercentage() {
			return 100 * $("#chat-frame").scrollTop() / ($("#chat-container").height() - $("#chat-frame").height());
		},

		hasScrollBar(element) {
			return element.get(0).scrollHeight > element.height();
		}
	},

	mounted() {
		$("#message-input").submit(function(e) {
			e.preventDefault();
		})

		this.chatSub = WS.watch("/user/local/chat")
			.pipe(map(message => {
				return JSON.parse(message.body);
			}))
			.subscribe(message => {
				// Format the chat timestamp
				var date = new Date(message.timestamp);
				message.timestamp = this.timeFormatter.format(date);
				var scrollToBottom = this.getScrollPercentage() == 100 || !this.hasScrollBar($("#chat-frame"));
				this.messages.push(message);
				if(scrollToBottom) {
					this.$nextTick(() => {
						$("#chat-frame").scrollTop($("#chat-frame")[0].scrollHeight);
					});
				}
			});
	},

	unmounted() {
		this.chatSub.unsubscribe();
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
	overflow-y: auto;
	overflow-x: hidden;
}

.chat-box p {
	margin: 0px;
	padding: 0px;
}

.chat-box::-webkit-scrollbar {
	width: 10px;
}
.chat-box::-webkit-scrollbar-track {
	background: #00000000;
}

.chat-box::-webkit-scrollbar-thumb {
	transition: background 2s ease-in-out;
	background: #cccccc;
	border-radius: 5px;
}

.chat-box::-webkit-scrollbar-thumb:hover {
	background: #838383;
}

</style>
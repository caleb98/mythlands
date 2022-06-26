<template>
	<div class="framed">
		<Dialog :show="showReportDialog" :title="reportDialogTitle" :inner-html="reportDialogText" :buttons="reportButtons" />
		<ContextMenu :show="showMenu" :menu-id="menuId" 
			:x-pos="xPos" :y-pos="yPos" :options="menuOptions" @context-menu-close="showMenu = false" />
		<form>
			<div class="row">
				<div class="col-12">
					<h6 class="m-1">Chat</h6>
					<!-- Chat Box Div -->
					<div id="chat-frame" class="m-1 chat-box clean-text text-start px-1">
						<div id="chat-container">
							<p v-for="message in messages" :key="message.id" class="chat-message" :data-username="message.username" :data-message-id="message.id">
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
import ContextMenu from './ContextMenu.vue';
import Dialog from './Dialog.vue';

export default {
    name: "ChatComponent",
    props: {
        isLoggedIn: Boolean
    },
    data() {
        return {
			// Chat box data
            messages: [],
            messageText: "",
            timeFormatter: new Intl.DateTimeFormat([], {
                hour12: true,
                hour: "numeric",
                minute: "numeric",
                second: "numeric"
            }),
            scrollTop: 0,

			// Context menu data
			menuId: "chat-context-menu",
			showMenu: false,
			xPos: 0,
			yPos: 0,
			menuOptions: null, // Set up menu options in mounted() when we have access to 'this'
			selectedMessageId: -1,
			selectedMessageUser: "",

			// Report menu data
			showReportDialog: false,
			reportButtons: null // Setup in mounted()

        };
    },

    methods: {
        sendMessage() {
            WS.publish({
                destination: "/game/chat",
                body: JSON.stringify({
                    groupId: 1,
                    message: this.messageText
                })
            });
            this.messageText = "";
        },
		
        getScrollPercentage() {
            return 100 * $("#chat-frame").scrollTop() / ($("#chat-container").height() - $("#chat-frame").height());
        },

        hasScrollBar(element) {
            return element.get(0).scrollHeight > element.height();
        },

        messageRightClickHandler(event, element) {
            var message = $(element);
			this.selectedMessageId = message.data("messageId");
			this.selectedMessageUser = message.data("username");
			this.xPos = event.pageX;
			this.yPos = event.pageY;
			this.showMenu = true;
            event.preventDefault();
        }
    },

	computed: {
		reportDialogTitle() {
			return "Report " + this.selectedMessageUser;
		},

		reportDialogText() {
			var filtered = this.messages.filter(m => m.id == this.selectedMessageId);
			if(filtered.length == 0) {
				return "";
			}
			var text = filtered[0].message;
			return `You are about to report <b>${this.selectedMessageUser}</b> for the following
					message:<br><i>${text}</i><br>Are you sure?`;
		}
	},

    mounted() {
		var self = this;
		this.menuOptions = [
			{ text: "Report Message", onClick() { 
				self.showReportDialog = true;
				self.showMenu = false;
			}}
		];

		this.reportButtons = [
			{ text: "Yes", type: "btn-primary", onClick() {
				WS.publish({
					destination: "/game/chat.report",
					body: JSON.stringify({
						messageId: self.selectedMessageId
					})
				});
				self.showReportDialog = false;
			}},
			{ text: "Cancel", type: "btn-secondary", onClick() {
				self.showReportDialog = false;
			}}
		];
		
        $("#message-input").submit(function (e) {
            e.preventDefault();
        });
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
            if (scrollToBottom) {
                this.$nextTick(() => {
                    $("#chat-frame").scrollTop($("#chat-frame")[0].scrollHeight);
                    var self = this;
                    // Refresh the context menu listeners for the chats
                    $(".chat-message")
                        .off("contextmenu")
                        .on("contextmenu", function (event) {
                        self.messageRightClickHandler(event, this);
                    });
                });
            }
        });
    },
    unmounted() {
        this.chatSub.unsubscribe();
    },
    components: { ContextMenu, Dialog }
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
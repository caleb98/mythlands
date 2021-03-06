import Cookies from 'js-cookie';
import { RxStomp } from '@stomp/rx-stomp';
import { windowTime } from 'rxjs';

// Setup the default client
const WS = new RxStomp();

var wsClientConfig = {
	brokerURL: process.env.VUE_APP_WEBSOCKET_HOST + "/connect",
	beforeConnect() {
		WS.configure({
			connectHeaders: {
				"X-XSRF-TOKEN": Cookies.get("XSRF-TOKEN")
			}
		});
	},
	debug(str) {
		// console.log("STOMP: " + str);
	},
	reconnectDelay: 5000,
};

// Configure and activate
WS.configure(wsClientConfig);
WS.activate();

WS.reconnect = function() {
	WS.deactivate().then(
		() => {
			WS.activate();
		},
		() => {
			// TODO: handle this error.
		}
	);
}

export default WS;
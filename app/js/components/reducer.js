import {
    START_SUBMIT_AOI,
    COMPLETE_SUBMIT_AOI,
    FAIL_SUBMIT_AOI,
    CLEAR_AOI,
    CHANGE_API_ENDPOINT,
    START_PING_API,
    COMPLETE_PING_API,
    FAIL_PING_API,
} from './actions';

const initAppPageState = {
    fetching: false,
    data: null,
    selectedApiEndpoint: '/nlcdcount',
    pong: true,
    sendingPing: false,
};

export default function appPage(state = initAppPageState, { type, payload }) {
    switch (type) {
        case START_SUBMIT_AOI:
            return Object.assign({}, state, {
                data: null,
                fetching: true,
            });
        case COMPLETE_SUBMIT_AOI:
            return Object.assign({}, state, {
                data: payload,
                fetching: false,
            });
        case FAIL_SUBMIT_AOI:
            return Object.assign({}, state, {
                fetching: false,
            });
        case CLEAR_AOI:
            return Object.assign({}, state, {
                data: null,
            });
        case CHANGE_API_ENDPOINT:
            return Object.assign({}, state, {
                selectedApiEndpoint: payload,
                data: null,
            });
        case START_PING_API:
            return Object.assign({}, state, {
                sendingPing: true,
            });
        case COMPLETE_PING_API:
            return Object.assign({}, state, {
                pong: true,
                sendingPing: false,
            });
        case FAIL_PING_API:
            return Object.assign({}, state, {
                pong: false,
                sendingPing: false,
            });
        default:
            return state;
    }
}

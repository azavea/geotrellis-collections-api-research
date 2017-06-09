import {
    START_SUBMIT_AOI,
    COMPLETE_SUBMIT_AOI,
    FAIL_SUBMIT_AOI,
    CLEAR_AOI,
    CHANGE_API_ENDPOINT,
} from './actions';

const initAppPageState = {
    aoi: null,
    fetching: false,
    data: null,
    selectedApiEndpoint: '/pngtile',
};

export default function appPage(state = initAppPageState, { type, payload }) {
    switch (type) {
        case START_SUBMIT_AOI:
            return Object.assign({}, state, {
                aoi: payload,
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
                aoi: null,
            });
        case CHANGE_API_ENDPOINT:
            return Object.assign({}, state, {
                selectedApiEndpoint: payload,
            });
        default:
            return state;
    }
}

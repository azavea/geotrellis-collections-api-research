import R from 'ramda';

import {
    START_SUBMIT_AOI,
    COMPLETE_SUBMIT_AOI,
    FAIL_SUBMIT_AOI,
    CLEAR_AOI,
    START_PING_API,
    COMPLETE_PING_API,
    FAIL_PING_API,
    CLEAR_API_ERROR,
    CLEAR_DATA,
    START_DRAWING,
    STOP_DRAWING,
} from './actions';

import {
    apiEndpoints,
} from '../constants';

const initAppPageState = {
    fetching: false,
    data: null,
    error: false,
    errorMessage: null,
    selectedApiEndpoint: R.head(apiEndpoints),
    pong: true,
    sendingPing: false,
    areaOfInterest: null,
    drawingActive: false,
};

export default function appPage(state = initAppPageState, { type, payload }) {
    switch (type) {
        case START_SUBMIT_AOI:
            return Object.assign({}, state, {
                data: null,
                fetching: true,
                error: false,
                errorMessage: null,
                drawingActive: false,
                areaOfInterest: payload,
            });
        case COMPLETE_SUBMIT_AOI:
            return Object.assign({}, state, {
                data: payload,
                fetching: false,
                error: false,
                errorMessage: null,
            });
        case FAIL_SUBMIT_AOI:
            return Object.assign({}, state, {
                fetching: false,
                error: true,
                errorMessage: payload,
            });
        case CLEAR_API_ERROR:
            return Object.assign({}, state, {
                error: false,
            });
        case CLEAR_DATA:
            return Object.assign({}, state, {
                data: null,
            });
        case CLEAR_AOI:
            return Object.assign({}, state, {
                data: null,
                error: false,
                areaOfInterest: null,
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
        case START_DRAWING:
            return Object.assign({}, state, {
                drawingActive: true,
            });
        case STOP_DRAWING:
            return Object.assign({}, state, {
                drawingActive: false,
            });
        default:
            return state;
    }
}

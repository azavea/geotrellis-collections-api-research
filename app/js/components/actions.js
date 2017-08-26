import axios, { CancelToken } from 'axios';

import {
    apiServerURL,
} from '../constants';

import {
    nlcdSoilCountMockData,
    slopeCountMockData,
    soilSlopeKFactorMockData,
    soilSlopeCountMockData,
} from './mockData';

export const START_SUBMIT_AOI = 'START_SUBMIT_AOI';
export const COMPLETE_SUBMIT_AOI = 'COMPLETE_SUBMIT_AOI';
export const FAIL_SUBMIT_AOI = 'FAIL_SUBMIT_AOI';
export const CLEAR_AOI = 'CLEAR_AOI';
export const CHANGE_API_ENDPOINT = 'CHANGE_API_ENDPOINT';
export const START_PING_API = 'START_PING_API';
export const COMPLETE_PING_API = 'COMPLETE_PING_API';
export const FAIL_PING_API = 'FAIL_PING_API';
export const CLEAR_API_ERROR = 'CLEAR_API_ERROR';
export const CLEAR_DATA = 'CLEAR_DATA';

let cancelAxiosRequest = null;

function cancelPriorRequest() {
    if (cancelAxiosRequest) {
        cancelAxiosRequest('Canceling prior POST request');
        cancelAxiosRequest = null;
    }
}

export function clearAreaOfInterest() {
    return {
        type: CLEAR_AOI,
    };
}

function startSubmitAreaOfInterest() {
    return {
        type: START_SUBMIT_AOI,
    };
}

function completeSubmitAreaOfInterest({ response }) {
    return {
        type: COMPLETE_SUBMIT_AOI,
        payload: response,
    };
}

function failSubmitAreaOfInterest() {
    return {
        type: FAIL_SUBMIT_AOI,
    };
}

export function clearAPIError() {
    return {
        type: CLEAR_API_ERROR,
    };
}

export function clearData() {
    return {
        type: CLEAR_DATA,
    };
}

export function submitAreaOfInterest({ geometry }) {
    cancelPriorRequest();
    return (dispatch, getState) => {
        dispatch(startSubmitAreaOfInterest());
        const { appPage: { selectedApiEndpoint } } = getState();
        switch (selectedApiEndpoint) {
            case '/slopepercentagecount':
                return dispatch(completeSubmitAreaOfInterest({
                    response: JSON.parse(slopeCountMockData),
                }));
            case '/soilgroupslopecount':
                return dispatch(completeSubmitAreaOfInterest({
                    response: JSON.parse(soilSlopeCountMockData),
                }));
            case '/nlcdsoilgroupcount':
                return dispatch(completeSubmitAreaOfInterest({
                    response: JSON.parse(nlcdSoilCountMockData),
                }));
            case '/soilslopekfactor':
                return dispatch(completeSubmitAreaOfInterest({
                    response: JSON.parse(soilSlopeKFactorMockData),
                }));
            default:
                break;
        }

        axios.post(`${apiServerURL}${selectedApiEndpoint}`,
            JSON.stringify({ geometry: JSON.stringify(geometry) }),
            {
                headers: {
                    'Content-Type': 'application/json',
                },
                cancelToken: new CancelToken((c) => { cancelAxiosRequest = c; }),
            })
             .then(({ data }) => dispatch(completeSubmitAreaOfInterest(data)))
             .catch(() => dispatch(failSubmitAreaOfInterest()));

        return () => {};
    };
}

export function changeApiEndpoint(payload) {
    return {
        type: CHANGE_API_ENDPOINT,
        payload,
    };
}

function startPingApi() {
    return {
        type: START_PING_API,
    };
}

function completePingApi() {
    return {
        type: COMPLETE_PING_API,
    };
}

function failPingApi() {
    return {
        type: FAIL_PING_API,
    };
}

export function pingApiEndpoint() {
    return (dispatch) => {
        dispatch(startPingApi());
        axios.get(`${apiServerURL}/ping`)
            .then(() => dispatch(completePingApi()))
            .catch(() => dispatch(failPingApi()));
    };
}

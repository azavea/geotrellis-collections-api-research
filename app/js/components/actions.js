import axios, { CancelToken } from 'axios';

import {
    apiServerURL,
} from '../constants';

export const START_SUBMIT_AOI = 'START_SUBMIT_AOI';
export const COMPLETE_SUBMIT_AOI = 'COMPLETE_SUBMIT_AOI';
export const FAIL_SUBMIT_AOI = 'FAIL_SUBMIT_AOI';
export const CLEAR_AOI = 'CLEAR_AOI';
export const CHANGE_API_ENDPOINT = 'CHANGE_API_ENDPOINT';

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

function startSubmitAreaOfInterest(aoi) {
    return {
        type: START_SUBMIT_AOI,
        payload: aoi,
    };
}

function completeSubmitAreaOfInterest(data) {
    return {
        type: COMPLETE_SUBMIT_AOI,
        payload: data,
    };
}

function failSubmitAreaOfInterest() {
    return {
        type: FAIL_SUBMIT_AOI,
    };
}

export function submitAreaOfInterest(aoi) {
    cancelPriorRequest();
    return (dispatch, getState) => {
        dispatch(startSubmitAreaOfInterest(aoi));
        const { appPage: { selectedApiEndpoint } } = getState();
        axios.post(`${apiServerURL}${selectedApiEndpoint}`, JSON.stringify(aoi), {
            headers: {
                'Content-Type': 'application/json',
            },
            cancelToken: new CancelToken((c) => { cancelAxiosRequest = c; }),
        })
             .then(({ data }) => dispatch(completeSubmitAreaOfInterest(data)))
             .catch(() => dispatch(failSubmitAreaOfInterest()));
    };
}

export function changeApiEndpoint(payload) {
    return {
        type: CHANGE_API_ENDPOINT,
        payload,
    };
}

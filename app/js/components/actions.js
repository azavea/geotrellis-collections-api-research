import axios from 'axios';

import {
    apiServerURL,
    postRequestOptions,
} from '../constants';

export const START_SUBMIT_AOI = 'START_SUBMIT_AOI';
export const COMPLETE_SUBMIT_AOI = 'COMPLETE_SUBMIT_AOI';
export const FAIL_SUBMIT_AOI = 'FAIL_SUBMIT_AOI';
export const CLEAR_AOI = 'CLEAR_AOI';

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
    return (dispatch) => {
        dispatch(startSubmitAreaOfInterest(aoi));
        axios.post(apiServerURL, JSON.stringify(aoi), postRequestOptions)
             .then(({ data }) => dispatch(completeSubmitAreaOfInterest(data)))
             .catch(() => dispatch(failSubmitAreaOfInterest()));
    };
}

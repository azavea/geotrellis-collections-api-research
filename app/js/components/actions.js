import axios from 'axios';

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
        // TODO: Make real request
        axios.get('http://localhost:9555')
             .then(({ headers }) => dispatch(completeSubmitAreaOfInterest(headers.date)))
             .catch(() => dispatch(failSubmitAreaOfInterest()));
    };
}

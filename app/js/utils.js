import R from 'ramda';

export function roundToTens(x) {
    return x > 100 ? 100 : Math.round(x / 10) * 10;
}

export function coalesceData(acc, [key, value]) {
    return Object.assign({}, acc, {
        [key]: acc[key] ? acc[key] + value : value,
    });
}

export function splitKey(key) {
    return R.map(x => parseInt(x, 10), R.split(',',
        R.replace(/List/, '', key).slice(1, -1)));
}

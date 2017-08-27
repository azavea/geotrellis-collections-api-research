import React, { PropTypes } from 'react';
import { VictoryAxis, VictoryBar, VictoryChart, VictoryTheme } from 'victory';
import R from 'ramda';

import {
    soilGroupsMap,
} from '../constants';

function splitSoilGroupSlopeKey(key) {
    return R.map(x => parseInt(x, 10), R.split(',',
        R.replace(/List/, '', key).slice(1, -1)));
}

function roundToTens(x) {
    return x > 100 ? 100 : Math.round(x / 10) * 10;
}

function coalesceData(acc, [key, value]) {
    return Object.assign({}, acc, {
        [key]: acc[key] ? acc[key] + value : value,
    });
}

export default function SoilGroupsSlopeChart({
    data,
}) {
    const chartData =
        R.map(([x, y]) => ({ x, y, width: 20 }),
        R.toPairs(
        R.reduce(coalesceData, {},
        R.map(([count, [soil, slope]]) =>
            [[soilGroupsMap[soil], roundToTens(slope)], count],
        R.map(([k, y]) => ([y, splitSoilGroupSlopeKey(k)]),
        R.toPairs(data))))));

    return (
        <VictoryChart
            theme={VictoryTheme.grayscale}
            width={800}
        >
            <VictoryBar
                style={{ data: { fill: 'tomato' } }}
                data={chartData}
                labels={({ y }) => `${Math.round(y / 1000)}k`}
            />
            <VictoryAxis />
        </VictoryChart>
    );
}

SoilGroupsSlopeChart.propTypes = {
    data: PropTypes.object,
};

import React, { PropTypes } from 'react';
import { VictoryAxis, VictoryBar, VictoryChart, VictoryTheme } from 'victory';
import R from 'ramda';

import {
    nlcdMap,
} from '../constants';

import {
    coalesceData,
    roundToTens,
    splitKey,
} from '../utils';

export default function NLCDSlopeCountChart({
    data,
}) {
    const chartData =
        R.map(([x, y]) => ({ x, y, width: 20 }),
        R.toPairs(
        R.reduce(coalesceData, {},
        R.map(([count, [nlcdID, slope]]) =>
            [[nlcdMap[nlcdID], roundToTens(slope)], count],
        R.map(([k, y]) => ([y, splitKey(k)]),
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

NLCDSlopeCountChart.propTypes = {
    data: PropTypes.object,
};
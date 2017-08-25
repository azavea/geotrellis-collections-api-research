import React, { PropTypes } from 'react';
import { VictoryBar, VictoryChart, VictoryTheme } from 'victory';
import R from 'ramda';

import {
    nlcdMap,
} from '../constants';

export default function NLCDChart({
    data,
}) {
    const chartData = R.map(([x, y]) => ({ y, x }), R.toPairs(data));

    return (
        <VictoryChart
            height={300}
            width={700}
            theme={VictoryTheme.grayscale}
            domainPadding={{ y: 10, x: 140 }}
        >
            <VictoryBar
                style={{ data: { fill: 'tomato' } }}
                horizontal
                data={chartData}
                labels={({ x }) => nlcdMap[parseInt(x, 10)] || 'Unknown'}
            />
        </VictoryChart>
    );
}

NLCDChart.propTypes = {
    data: PropTypes.object,
};

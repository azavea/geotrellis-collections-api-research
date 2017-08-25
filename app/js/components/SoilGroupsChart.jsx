import React, { PropTypes } from 'react';
import { VictoryPie } from 'victory';
import R from 'ramda';

import {
    soilGroupsMap,
} from '../constants';

export default function SoilGroupsChart({
    data,
}) {
    const chartData = R.map(([x, y]) => ({ y, x }), R.toPairs(data));

    return (
        <VictoryPie
            height={300}
            width={300}
            colorScale="heatmap"
            data={chartData}
            labels={({ x }) => soilGroupsMap[parseInt(x, 10)] || 'Unknown'}
        />
    );
}

SoilGroupsChart.propTypes = {
    data: PropTypes.object,
};

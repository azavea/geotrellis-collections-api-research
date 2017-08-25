import React, { PropTypes } from 'react';
import { VictoryPie } from 'victory';
import R from 'ramda';

const soilGroupsMap = {
    1: 'A',
    2: 'B',
    3: 'C',
    4: 'D',
    5: 'A/D',
    6: 'B/D',
    7: 'C/D',
};

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

import React, { PropTypes } from 'react';
import { VictoryBar, VictoryChart, VictoryTheme } from 'victory';
import R from 'ramda';

const nlcdMap = {
    11: 'Open water',
    12: 'Perennial ice & snow',
    21: 'Developed open',
    22: 'Developed low',
    23: 'Developed medium',
    24: 'Developed high',
    31: 'Barren land',
    41: 'Deciduous forest',
    42: 'Evergreen forest',
    43: 'Mixed forest',
    52: 'Scrub',
    71: 'Grassland',
    81: 'Pasture',
    82: 'Cultivated crops',
    90: 'Woody wetlands',
    95: 'Herbacious wetlands',
};

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

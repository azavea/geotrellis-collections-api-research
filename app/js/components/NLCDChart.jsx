import React, { PropTypes } from 'react';
import { VictoryPie } from 'victory';
import R from 'ramda';

export default function NLCDChart({
    data,
}) {
    const chartData =
        R.reject(({ y }) => y === 0,
        R.map(([x, y]) => ({ y, x }),
        R.toPairs(data)));

    const totalCells = R.sum(R.map(({ y }) => y, chartData));

    const renderLabelIfSignificant = ((label, count) => {
        if (count / totalCells > 0.025) {
            return label;
        }
        return '';
    });

    return (
        <VictoryPie
            data={chartData}
            colorScale="qualitative"
            labels={({ x, y }) => renderLabelIfSignificant(x, y)}
            height={200}
            width={200}
        />
    );
}

NLCDChart.propTypes = {
    data: PropTypes.object,
};

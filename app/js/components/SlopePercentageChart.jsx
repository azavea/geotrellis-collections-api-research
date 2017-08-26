import React, { PropTypes } from 'react';
import { VictoryAxis, VictoryBar, VictoryChart, VictoryTheme } from 'victory';
import R from 'ramda';

function roundToTen([x, y]) {
    return x < 10 ? [1, y] : [Math.ceil(x / 10) * 10, y];
}

export default function SlopePercentageChart({
    data,
}) {
    const chartData =
        R.map(([x, y]) => ({ x, y }),
        R.toPairs(R.mapObjIndexed((element) =>
        R.map(pair => R.last(pair), element).reduce((x, y) => x + y),
        R.groupBy((step) => R.head(step),
        R.map(roundToTen, R.toPairs(data))))));

    return (
        <VictoryChart
            height={300}
            width={500}
            theme={VictoryTheme.grayscale}
            domainPadding={{ y: 10, x: 10 }}
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

SlopePercentageChart.propTypes = {
    data: PropTypes.object,
};

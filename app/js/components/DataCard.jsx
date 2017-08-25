import React, { PropTypes } from 'react';

import NLCDChart from './NLCDChart';
import SoilGroupsChart from './SoilGroupsChart';

export default function DataCard({
    data,
    selectedApiEndpoint,
}) {
    if (selectedApiEndpoint === '/nlcdcount') {
        return (
            <div className="pt-card pt-elevation-0 data-card">
                <h4>
                    NLCD cell counts
                </h4>
                <NLCDChart data={data} />
            </div>
        );
    }

    if (selectedApiEndpoint === '/soilgroupcount') {
        return (
            <div className="pt-card pt-elevation-0 data-card">
                <h4>
                    Soil groups composition
                </h4>
                <SoilGroupsChart data={data} />
            </div>
        );
    }

    return (
        <div
            className="pt-card pt-elevation-0 data-card"
            style={{ maxWidth: '500px', wordBreak: 'break-all' }}
        >
            <div>
                <h5>
                    Data for your area of interest:
                </h5>
                <span>
                    {JSON.stringify(data)}
                </span>
            </div>
        </div>
    );
}

DataCard.propTypes = {
    data: PropTypes.object.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
};

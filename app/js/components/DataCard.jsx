import React, { PropTypes } from 'react';

import NLCDChart from './NLCDChart';
import SoilGroupsChart from './SoilGroupsChart';
import SlopePercentageChart from './SlopePercentageChart';
import SoilGroupsSlopeChart from './SoilGroupsSlopeChart';
import NLCDSlopeCountChart from './NLCDSlopeCountChart';

export default function DataCard({
    data,
    error,
    errorMessage,
    selectedApiEndpoint,
}) {
    if (error || !data) {
        return (
            <div className="pt-card pt-elevation-0 data-card">
                <div>
                    <span id="error-card-message">
                        {errorMessage || 'API Error'}
                    </span>
                    <span id="error-card-icon" className="pt-icon-standard pt-icon-error" />
                </div>
            </div>
        );
    }

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

    if (selectedApiEndpoint === '/slopepercentagecount') {
        return (
            <div className="pt-card pt-elevation-0 data-card">
                <h4>
                     Rounded slope percentage cell counts
                </h4>
                <SlopePercentageChart data={data} />
            </div>
        );
    }

    if (selectedApiEndpoint === '/soilgroupslopecount') {
        return (
            <div className="pt-card pt-elevation-0 data-card">
                <h4>
                    Soil group slope percentages
                </h4>
                <SoilGroupsSlopeChart data={data} />
            </div>
        );
    }

    if (selectedApiEndpoint === '/nlcdslopecount') {
        return (
            <div className="pt-card pt-elevation-0 data-card">
                <h4>
                    NLCD category slope percentages
                </h4>
                <NLCDSlopeCountChart data={data} />
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
    data: PropTypes.object,
    error: PropTypes.bool,
    errorMessage: PropTypes.string,
    selectedApiEndpoint: PropTypes.string.isRequired,
};

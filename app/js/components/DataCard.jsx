import React, { PropTypes } from 'react';
import Control from 'react-leaflet-control';

import NLCDChart from './NLCDChart';

export default function DataCard({
    data,
    selectedApiEndpoint,
}) {
    if (selectedApiEndpoint === '/nlcdcount') {
        return (
            <Control position="bottomleft">
                <div className="pt-card pt-elevation-0">
                    <h4>
                        NLCD cell counts
                    </h4>
                    <NLCDChart data={data} />
                </div>
            </Control>
        );
    }

    return (
        <Control position="bottomleft">
            <div className="pt-card pt-elevation-0">
                <div>
                    <h5>
                        Data for your area of interest:
                    </h5>
                    <span>
                        {JSON.stringify(data)}
                    </span>
                </div>
            </div>
        </Control>
    );
}

DataCard.propTypes = {
    data: PropTypes.object.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
};

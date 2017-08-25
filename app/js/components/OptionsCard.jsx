import React, { PropTypes } from 'react';
import Control from 'react-leaflet-control';

import RequestOptionsMenu from './RequestOptionsMenu';

export default function OptionsCard({
    dispatch,
    selectedApiEndpoint,
}) {
    return (
        <Control position="bottomright">
            <div
                id="options-control"
                className="pt-card pt-elevation-0 api-endpoints-control"
            >
                <h5 style={{ color: '#394b59' }}>
                    Geoprocessing ops
                </h5>
                <RequestOptionsMenu
                    dispatch={dispatch}
                    selectedApiEndpoint={selectedApiEndpoint}
                />
            </div>
        </Control>
    );
}

OptionsCard.propTypes = {
    dispatch: PropTypes.func.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
};

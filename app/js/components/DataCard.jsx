import React, { PropTypes } from 'react';

import NLCDChart from './NLCDChart';

export default function DataCard({
    data,
    error,
    errorMessage,
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

    return (
        <div className="pt-card pt-elevation-0 data-card">
            <h4>
                NLCD cell counts
            </h4>
            <NLCDChart data={data} />
        </div>
    );
}

DataCard.propTypes = {
    data: PropTypes.object,
    error: PropTypes.bool,
    errorMessage: PropTypes.string,
};

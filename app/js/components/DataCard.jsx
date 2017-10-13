import React from 'react';
import { bool, number, object, string } from 'prop-types';
import NLCDChart from './NLCDChart';

const metersPerSqKm = 10000000;

function displayAoiSize(size) {
    return (size < metersPerSqKm) ?
        `${Math.round(size).toLocaleString()} square meters` :
        `${Math.round(size / metersPerSqKm).toLocaleString()} square kilometers`;
}

export default function DataCard({
    data,
    error,
    errorMessage,
    aoiSize,
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

    const areaDesciption = aoiSize ? displayAoiSize(aoiSize) : null;

    return (
        <div className="pt-card pt-elevation-0 data-card">
            <div>
                <h4>
                    <span>
                        NLCD cell counts
                        <a
                            href="http://www.pasda.psu.edu/uci/FullMetadataDisplay.aspx?file=nlcd_pa_tiff_alb.xml#Entity_and_Attribute_Information"
                            target="_blank"
                            title="Land cover ids"
                            id="piechart-legend-link"
                        >
                            <span className="pt-icon-standard pt-icon-info-sign" />
                        </a>
                    </span>
                </h4>
                {areaDesciption}
            </div>
            <NLCDChart data={data} />
        </div>
    );
}

DataCard.propTypes = {
    data: object,
    error: bool,
    errorMessage: string,
    aoiSize: number,
};

import React from 'react';
import { bool, func } from 'prop-types';

export default function Header({
    pingSuccessful,
    pingApi,
    drawingActive,
    drawButtonAction,
    layerActive,
    toggleLayer,
}) {
    const pingIconCSS = pingSuccessful ? 'pt-icon-feed' : 'pt-icon-offline';

    const {
        drawIconCSS,
        drawButtonTitle,
    } = (() => {
        if (drawingActive) {
            return {
                drawIconCSS: 'pt-icon-cross',
                drawButtonTitle: 'Cancel drawing',
            };
        }
        return {
            drawIconCSS: 'pt-icon-edit',
            drawButtonTitle: 'Draw area of interest',
        };
    })();

    const layerActiveCSS = layerActive ? 'active' : '';

    return (
        <nav className="pt-navbar pt-dark pt-fixed-top">
            <div className="pt-navbar-group pt-align-left">
                <div className="pt-navbar-heading">
                    <a
                        href="https://github.com/azavea/geotrellis-collections-api-research"
                        target="_blank"
                        id="header-link"
                    >
                        GeoTrellis Collections API Research Project
                    </a>
                </div>
            </div>
            <div className="pt-navbar-group pt-align-right">
                <button
                    className={`pt-button pt-minimal pt-icon-layer ${layerActiveCSS}`}
                    onClick={toggleLayer}
                    title="Enable Painted Layer"
                />
                <button
                    className={`pt-button pt-minimal ${drawIconCSS}`}
                    onClick={drawButtonAction}
                    title={drawButtonTitle}
                />
                <button
                    className={`pt-button pt-minimal ${pingIconCSS}`}
                    onClick={pingApi}
                    title="Ping API"
                />
            </div>
        </nav>
    );
}

Header.propTypes = {
    pingSuccessful: bool.isRequired,
    pingApi: func.isRequired,
    drawingActive: bool.isRequired,
    drawButtonAction: func.isRequired,
    layerActive: bool.isRequired,
    toggleLayer: func.isRequired,
};

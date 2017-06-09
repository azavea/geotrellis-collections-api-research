import React, { PropTypes } from 'react';

export default function Header({
    panelVisible,
    togglePanel,
}) {
    const buttonIconCSS = panelVisible ? 'pt-icon-map' :
        'pt-icon-timeline-area-chart';

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
                    className={`pt-button pt-minimal ${buttonIconCSS}`}
                    onClick={togglePanel}
                />
            </div>
        </nav>
    );
}

Header.propTypes = {
    panelVisible: PropTypes.bool.isRequired,
    togglePanel: PropTypes.func.isRequired,
};

import React, { PropTypes } from 'react';

import RequestOptionsMenu from './RequestOptionsMenu';

export default function Panel({
    dispatch,
    selectedApiEndpoint,
}) {
    return (
        <div id="data-viz-panel">
            <RequestOptionsMenu
                dispatch={dispatch}
                selectedApiEndpoint={selectedApiEndpoint}
            />
        </div>
    );
}

Panel.propTypes = {
    dispatch: PropTypes.func.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
};

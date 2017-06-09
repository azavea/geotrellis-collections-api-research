import React, { PropTypes } from 'react';
import { Radio, RadioGroup } from '@blueprintjs/core';
import { map } from 'lodash';

import {
    changeApiEndpoint,
} from './actions';

import {
    apiEndpoints,
} from '../constants';

export default function RequestOptionsMenu({
    dispatch,
    selectedApiEndpoint,
}) {
    const radioButtons = map(apiEndpoints, name => (
        <Radio
            className="pt-align-right"
            label={name}
            value={name}
            key={name}
        />));

    const selectEndpoint = v => dispatch(changeApiEndpoint(v));

    return (
        <div className="pt-card">
            <RadioGroup
                label="API endpoint"
                onChange={({ target: { value } }) => selectEndpoint(value)}
                selectedValue={selectedApiEndpoint}
            >
                {radioButtons}
            </RadioGroup>
        </div>
    );
}

RequestOptionsMenu.propTypes = {
    dispatch: PropTypes.func.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
};


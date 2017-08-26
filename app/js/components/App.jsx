import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import {
    pingApiEndpoint,
} from './actions';

import Map from './Map';
import Header from './Header';

class App extends Component {
    componentDidMount() {
        this.props.dispatch(pingApiEndpoint());
    }

    render() {
        const {
            data,
            dispatch,
            selectedApiEndpoint,
            pong,
            error,
        } = this.props;

        return (
            <div>
                <Header
                    pingSuccessful={pong}
                    pingApi={() => dispatch(pingApiEndpoint())}
                />
                <div id="full-screen-map">
                    <Map
                        data={data}
                        dispatch={dispatch}
                        selectedApiEndpoint={selectedApiEndpoint}
                        error={error}
                    />
                </div>
            </div>
        );
    }
}

App.propTypes = {
    dispatch: PropTypes.func.isRequired,
    data: PropTypes.object,
    fetching: PropTypes.bool,
    error: PropTypes.bool,
    selectedApiEndpoint: PropTypes.string.isRequired,
    pong: PropTypes.bool.isRequired,
};

function mapStateToProps({
    appPage: {
        data,
        fetching,
        error,
        selectedApiEndpoint,
        pong,
    },
}) {
    return {
        data,
        fetching,
        error,
        selectedApiEndpoint,
        pong,
    };
}

export default connect(mapStateToProps)(App);

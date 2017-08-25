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
    selectedApiEndpoint: PropTypes.string.isRequired,
    pong: PropTypes.bool.isRequired,
};

function mapStateToProps({
    appPage: {
        data,
        fetching,
        selectedApiEndpoint,
        pong,
    },
}) {
    return {
        data,
        fetching,
        selectedApiEndpoint,
        pong,
    };
}

export default connect(mapStateToProps)(App);

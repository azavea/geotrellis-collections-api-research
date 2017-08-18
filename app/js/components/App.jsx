import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import {
    pingApiEndpoint,
} from './actions';

import Map from './Map';
import Header from './Header';
import Panel from './Panel';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = { panelVisible: false };
        this.togglePanel = this.togglePanel.bind(this);
    }

    componentDidMount() {
        this.props.dispatch(pingApiEndpoint());
    }

    togglePanel() {
        this.setState({
            panelVisible: !this.state.panelVisible,
        });
    }

    render() {
        const {
            togglePanel,
            props: {
                data,
                dispatch,
                selectedApiEndpoint,
                pong,
            },
            state: {
                panelVisible,
            },
        } = this;

        const panel = panelVisible ? (
            <Panel
                dispatch={dispatch}
                selectedApiEndpoint={selectedApiEndpoint}
            />) : null;

        const mapViewCSS = panelVisible ? 'map-with-panel' : 'full-screen-map';

        return (
            <div>
                <Header
                    togglePanel={togglePanel}
                    panelVisible={panelVisible}
                    pingSuccessful={pong}
                    pingApi={() => dispatch(pingApiEndpoint())}
                />
                <div id={mapViewCSS}>
                    <Map
                        data={data}
                        dispatch={dispatch}
                    />
                </div>
                {panel}
            </div>
        );
    }
}

App.propTypes = {
    dispatch: PropTypes.func.isRequired,
    data: PropTypes.string,
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

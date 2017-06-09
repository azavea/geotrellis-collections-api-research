import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import Map from './Map';
import Header from './Header';
import Panel from './Panel';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = { panelVisible: false };
        this.togglePanel = this.togglePanel.bind(this);
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
            },
            state: {
                panelVisible,
            },
        } = this;

        const panel = panelVisible ? <Panel /> : null;

        const mapViewCSS = panelVisible ? 'map-with-panel' : 'full-screen-map';

        return (
            <div>
                <Header
                    togglePanel={togglePanel}
                    panelVisible={panelVisible}
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
    aoi: PropTypes.object,
    data: PropTypes.string,
    fetching: PropTypes.bool,
};

function mapStateToProps({ appPage: { aoi, data, fetching } }) {
    return {
        aoi,
        data,
        fetching,
    };
}

export default connect(mapStateToProps)(App);

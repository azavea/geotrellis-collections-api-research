import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import Map from './Map.jsx';

class App extends Component {
    componentWillReceiveProps({ data }) {
        window.console.log(data);
    }

    render() {
        return (
            <div>
                <Map
                    data={this.props.data}
                    dispatch={this.props.dispatch}
                />
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

import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import Map from './Map.jsx';

class App extends Component {
    componentWillReceiveProps(nextProps) {
        window.console.log(nextProps.appPage);
    }

    render() {
        return (
            <div>
                <Map dispatch={this.props.dispatch} />
            </div>
        );
    }
}

App.propTypes = {
    dispatch: PropTypes.func.isRequired,
    appPage: PropTypes.object.isRequired,
};

function mapStateToProps({ appPage }) {
    return {
        appPage,
    };
}

export default connect(mapStateToProps)(App);

import React, { Component, PropTypes } from 'react';
import { connect } from 'react-redux';

import {
    pingApiEndpoint,
    startDrawing,
    stopDrawing,
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
            pong,
            error,
            errorMessage,
            drawingActive,
            areaOfInterest,
        } = this.props;

        const drawButtonAction = drawingActive ?
            () => dispatch(stopDrawing()) :
            () => dispatch(startDrawing());

        return (
            <div>
                <Header
                    pingSuccessful={pong}
                    pingApi={() => dispatch(pingApiEndpoint())}
                    drawingActive={drawingActive}
                    drawButtonAction={drawButtonAction}
                />
                <div id="full-screen-map">
                    <Map
                        data={data}
                        dispatch={dispatch}
                        error={error}
                        errorMessage={errorMessage}
                        drawingActive={drawingActive}
                        areaOfInterest={areaOfInterest}
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
    errorMessage: PropTypes.string,
    pong: PropTypes.bool.isRequired,
    areaOfInterest: PropTypes.object,
    drawingActive: PropTypes.bool,
};

function mapStateToProps({
    appPage: {
        data,
        fetching,
        error,
        errorMessage,
        pong,
        drawingActive,
        areaOfInterest,
    },
}) {
    return {
        data,
        fetching,
        error,
        pong,
        errorMessage,
        drawingActive,
        areaOfInterest,
    };
}

export default connect(mapStateToProps)(App);

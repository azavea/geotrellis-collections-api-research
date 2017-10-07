import React, { Component } from 'react';
import { bool, func, object, string } from 'prop-types';
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
    dispatch: func.isRequired,
    data: object,
    fetching: bool,
    error: bool,
    errorMessage: string,
    pong: bool.isRequired,
    areaOfInterest: object,
    drawingActive: bool,
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

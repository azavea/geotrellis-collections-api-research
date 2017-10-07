import React, { Component, PropTypes } from 'react';
import {
    Map as ReactLeafletMap,
    ZoomControl,
    GeoJSON,
} from 'react-leaflet';
import Control from 'react-leaflet-control';
import L from 'leaflet';
import esri from 'esri-leaflet';
import 'leaflet-draw';

import {
    submitAreaOfInterest,
    clearAPIError,
    clearData,
} from './actions';

import {
    defaultMapCenter,
    defaultZoomLevel,
} from '../constants';

import pennsylvaniaBoundaries from '../pennsylvaniaBoundaries';

import DataCard from './DataCard';

export default class Map extends Component {
    constructor(props) {
        super(props);
        this.onCreate = this.onCreate.bind(this);
    }

    componentDidMount() {
        const {
            map: {
                leafletElement: leafletMap,
            },
            props: {
                dispatch,
            },
        } = this;

        esri.basemapLayer('Imagery').addTo(leafletMap);

        leafletMap.on('draw:drawstart', () => {
            dispatch(clearData());
            dispatch(clearAPIError());
        });

        leafletMap.on('draw:created', this.onCreate);

        this.polygonDrawHandler = new L.Draw.Polygon(leafletMap);
    }

    componentWillReceiveProps({ drawingActive }) {
        if (drawingActive) {
            this.polygonDrawHandler.enable();
        } else {
            this.polygonDrawHandler.disable();
        }
    }

    onCreate({ layer }) {
        this.props.dispatch(submitAreaOfInterest(layer.toGeoJSON()));
    }

    render() {
        const {
            data,
            error,
            errorMessage,
            areaOfInterest,
        } = this.props;

        const dataCard = data || error ? (
            <DataCard
                data={data}
                error={error}
                errorMessage={errorMessage}
            />) : <div />;

        const paBoundariesLayer = (
            <GeoJSON
                data={pennsylvaniaBoundaries}
                style={{ fill: false, color: '#FF5733' }}
            />
        );

        const areaOfInterestLayer = areaOfInterest ? (
            <GeoJSON
                data={areaOfInterest}
                style={{ fill: false, color: '#1E90FF' }}
            />) : null;

        return (
            <ReactLeafletMap
                center={defaultMapCenter}
                zoom={defaultZoomLevel}
                zoomControl={false}
                ref={l => { this.map = l; }}
            >
                {paBoundariesLayer}
                {areaOfInterestLayer}
                <Control position="bottomleft">
                    {dataCard}
                </Control>
                <ZoomControl position="bottomright" />
            </ReactLeafletMap>
        );
    }
}

Map.propTypes = {
    data: PropTypes.object,
    dispatch: PropTypes.func.isRequired,
    error: PropTypes.bool,
    errorMessage: PropTypes.string,
    drawingActive: PropTypes.bool.isRequired,
    areaOfInterest: PropTypes.object,
};

import React, { Component, PropTypes } from 'react';
import {
    Map as ReactLeafletMap,
    FeatureGroup,
    ZoomControl,
    GeoJSON,
} from 'react-leaflet';
import Control from 'react-leaflet-control';
import { EditControl } from 'react-leaflet-draw';
import R from 'ramda';
import esri from 'esri-leaflet';

import {
    submitAreaOfInterest,
    clearAreaOfInterest,
    clearAPIError,
    clearData,
} from './actions';

import {
    defaultMapCenter,
    defaultZoomLevel,
} from '../constants';

import pennsylvaniaBoundaries from '../pennsylvaniaBoundaries';

import DataCard from './DataCard';
import OptionsCard from './OptionsCard';

export default class Map extends Component {
    constructor(props) {
        super(props);
        this.onCreate = this.onCreate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.clearShapes = this.clearShapes.bind(this);
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
            this.clearShapes();
            dispatch(clearData());
            dispatch(clearAPIError());
        });
    }

    componentWillReceiveProps({ selectedApiEndpoint }) {
        if (selectedApiEndpoint !== this.props.selectedApiEndpoint) {
            this.clearShapes();
        }
    }

    onCreate({ layer }) {
        this.props.dispatch(submitAreaOfInterest(layer.toGeoJSON()));
    }

    onDelete() {
        this.props.dispatch(clearAreaOfInterest());
    }

    clearShapes() {
        if (this.drawnShapes) {
            R.forEach(l => { this.map.leafletElement.removeLayer(l); },
                this.drawnShapes.leafletElement.getLayers());
        }
    }

    render() {
        const {
            data,
            dispatch,
            selectedApiEndpoint,
            error,
            errorMessage,
        } = this.props;

        const dataCard = data || error ? (
            <DataCard
                data={data}
                error={error}
                errorMessage={errorMessage}
                selectedApiEndpoint={selectedApiEndpoint}
            />) : <div />;

        const optionsCard = (
            <OptionsCard
                dispatch={dispatch}
                selectedApiEndpoint={selectedApiEndpoint}
            />);

        const paBoundariesLayer = (
            <GeoJSON
                data={pennsylvaniaBoundaries}
                style={{ fill: false, color: '#FF5733' }}
            />
        );

        return (
            <ReactLeafletMap
                center={defaultMapCenter}
                zoom={defaultZoomLevel}
                zoomControl={false}
                ref={l => { this.map = l; }}
            >
                {paBoundariesLayer}
                <ZoomControl position="topright" />
                <FeatureGroup
                    ref={f => { this.drawnShapes = f; }}
                >
                    <EditControl
                        position="topright"
                        onCreated={this.onCreate}
                        onDeleted={this.onDelete}
                        draw={{
                            circle: false,
                            marker: false,
                            polyline: false,
                            rectangle: false,
                        }}
                        edit={{
                            edit: false,
                        }}
                    />
                </FeatureGroup>
                <Control position="bottomleft">
                    {dataCard}
                </Control>
                {optionsCard}
            </ReactLeafletMap>
        );
    }
}

Map.propTypes = {
    data: PropTypes.object,
    dispatch: PropTypes.func.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
    error: PropTypes.bool,
    errorMessage: PropTypes.string,
};

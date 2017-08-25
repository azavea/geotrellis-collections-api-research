import React, { Component, PropTypes } from 'react';
import { Map as ReactLeafletMap, TileLayer, FeatureGroup } from 'react-leaflet';
import { EditControl } from 'react-leaflet-draw';
import R from 'ramda';

import {
    submitAreaOfInterest,
    clearAreaOfInterest,
} from './actions';

import {
    defaultMapCenter,
    defaultZoomLevel,
    tiles,
    attribution,
} from '../constants';

import DataCard from './DataCard';
import OptionsCard from './OptionsCard';

export default class Map extends Component {
    constructor(props) {
        super(props);
        this.onCreate = this.onCreate.bind(this);
        this.onDelete = this.onDelete.bind(this);
    }

    componentDidMount() {
        const { leafletElement: leafletMap } = this.map;
        leafletMap.on('draw:drawstart', () => {
            if (this.drawnShapes) {
                this.props.dispatch(clearAreaOfInterest());
                R.forEach(l => { leafletMap.removeLayer(l); },
                    this.drawnShapes.leafletElement.getLayers());
            }
        });
    }

    onCreate({ layer }) {
        this.props.dispatch(submitAreaOfInterest(layer.toGeoJSON()));
    }

    onDelete() {
        this.props.dispatch(clearAreaOfInterest());
    }

    render() {
        const {
            data,
            dispatch,
            selectedApiEndpoint,
        } = this.props;

        const dataCard = data ? <DataCard data={data} /> : null;
        const optionsCard = (
            <OptionsCard
                dispatch={dispatch}
                selectedApiEndpoint={selectedApiEndpoint}
            />);

        return (
            <ReactLeafletMap
                center={defaultMapCenter}
                zoom={defaultZoomLevel}
                ref={l => { this.map = l; }}
            >
                <TileLayer
                    attribution={attribution}
                    url={tiles}
                />
                <FeatureGroup
                    ref={f => { this.drawnShapes = f; }}
                >
                    <EditControl
                        position="topleft"
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
                {dataCard}
                {optionsCard}
            </ReactLeafletMap>
        );
    }
}

Map.propTypes = {
    data: PropTypes.string,
    dispatch: PropTypes.func.isRequired,
    selectedApiEndpoint: PropTypes.string.isRequired,
};

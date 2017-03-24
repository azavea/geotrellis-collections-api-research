import React, { Component, PropTypes } from 'react';
import { Map as ReactLeafletMap, TileLayer, FeatureGroup } from 'react-leaflet';
import { EditControl } from 'react-leaflet-draw';
import { forEach } from 'lodash';

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
                forEach(this.drawnShapes.leafletElement.getLayers(), l => {
                    leafletMap.removeLayer(l);
                });
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
            </ReactLeafletMap>
        );
    }
}

Map.propTypes = {
    dispatch: PropTypes.func.isRequired,
};

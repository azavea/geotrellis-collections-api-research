export const isDevelopment = process.env.NODE_ENV === 'development';
export const defaultMapCenter = [39.961352, -75.154334];
export const defaultZoomLevel = 8;
export const tiles = 'http://{s}.tile.openstreetmap.se/hydda/base/{z}/{x}/{y}.png';
export const attribution = `Tiles courtesy of <a href="http://openstreetmap.se/"
    target="_blank">OpenStreetMap Sweden</a> &mdash; Map data &copy;
    <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>`;
export const apiServerURL = 'http://localhost:7000';
export const apiEndpoints = [
    '/nlcdcount',
    '/slopepercentagecount',
    '/soilgroupcount',
    '/soilgroupslopecount',
    '/nlcdsoilgroupcount',
    '/nlcdslopecount',
    '/soilslopekfactor',
];

export const nlcdMap = {
    11: 'Open water',
    12: 'Perennial ice & snow',
    21: 'Developed open',
    22: 'Developed low',
    23: 'Developed medium',
    24: 'Developed high',
    31: 'Barren land',
    41: 'Deciduous forest',
    42: 'Evergreen forest',
    43: 'Mixed forest',
    52: 'Scrub',
    71: 'Grassland',
    81: 'Pasture',
    82: 'Cultivated crops',
    90: 'Woody wetlands',
    95: 'Herbacious wetlands',
    [-2147483648]: 'Unknown',
};

export const soilGroupsMap = {
    1: 'A',
    2: 'B',
    3: 'C',
    4: 'D',
    5: 'A/D',
    6: 'B/D',
    7: 'C/D',
    [-2147483648]: 'Unknown',
};

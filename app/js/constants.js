export const isDevelopment = process.env.NODE_ENV === 'development';
export const defaultMapCenter = [39.961352, -75.154334];
export const defaultZoomLevel = 8;
export const tiles = 'http://{s}.tile.openstreetmap.se/hydda/base/{z}/{x}/{y}.png';
export const attribution = `Tiles courtesy of <a href="http://openstreetmap.se/"
    target="_blank">OpenStreetMap Sweden</a> &mdash; Map data &copy;
    <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>`;
export const apiServerURL = 'http://localhost:7000/pngtile';

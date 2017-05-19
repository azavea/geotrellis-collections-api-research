export const isDevelopment = process.env.NODE_ENV === 'development';
export const defaultMapCenter = [39.8333333, -98.585522];
export const defaultZoomLevel = 3;
export const tiles = 'https://stamen-tiles-{s}.a.ssl.fastly.net/toner/{z}/{x}/{y}.png';
export const attribution = 'Map tiles by <a href="https://stamen.com">Stamen Design</a>,' +
    '<a href="https://creativecommons.org/licenses/by/3.0">CC-BY-3.0</a> &mdash;' +
    'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>';
export const apiServerURL = 'http://localhost:7000/pngtile';

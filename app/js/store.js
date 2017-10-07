import { createStore, applyMiddleware } from 'redux';
import { createLogger } from 'redux-logger';
import thunk from 'redux-thunk';
import { isDevelopment } from './constants';

const createStoreWithMiddleware = isDevelopment ?
    applyMiddleware(thunk, createLogger())(createStore) :
    applyMiddleware(thunk)(createStore);

export default createStoreWithMiddleware;

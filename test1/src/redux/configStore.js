import { combineReducers, createStore } from "redux";
import word from "./modules/word";
// root 리듀서를 만들어줍니다.

const rootReducer = combineReducers({ word });
// 스토어를 만듭니다.


const store = createStore(rootReducer);
export default store;
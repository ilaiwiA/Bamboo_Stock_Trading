// import * as model from "../model/model.js";

import * as model from "../model/model.js";
import { WATCH_LIST, USER_STOCK } from "../config.js";

import StockListView from "../view/StockListView.js";
import PurchaseView from "../view/PurchaseView.js";

// (async function () {
//   await model.loadStockList(USER_STOCK);
//   await model.loadStockList(WATCH_LIST);

//   StockListView.render(model.state[USER_STOCK], USER_STOCK);
//   StockListView.render(model.state[WATCH_LIST], WATCH_LIST);
//   console.log("done");
// })();

const controllerStockList = async function (panelType) {
  try {
    await model.loadStockList(panelType);
    StockListView.render(model.state[panelType], panelType);

    PurchaseView.render(model.state[USER_STOCK][1]);
  } catch (error) {
    console.error(error);
  }
};

controllerStockList(USER_STOCK);
// controllerStockList(WATCH_LIST);

// const controllerPurchasePanel = async function (panelType) {
//   try {
//     await model.loadStockList(USER_STOCK);
//     PurchaseView.render(model.state[USER_STOCK][4]);
//   } catch (error) {
//     console.error(error);
//   }
// };

// controllerPurchasePanel();

const init = function () {};

init();

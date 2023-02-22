// import * as model from "../model/model.js";

import * as model from "../model/model.js";
import { WATCH_LIST, USER_STOCK } from "../config.js";

import StockListView from "../view/StockListView.js";
import PurchaseView from "../view/PurchaseView.js";
import NewsView from "../view/NewsView.js";
import StockDetailsView from "../view/StockDetailsView.js";
import PortfolioChartView from "../view/PortfolioChartView.js";
import MissingView from "../view/MissingView.js";

// const controllerStockList = async function (panelType) {
//   try {
//     await model.loadStockList(panelType);
//     await model.loadNews();

//     StockListView.render(model.state[panelType], panelType);
//     StockDetailsView.render(model.state[panelType][0]);
//     NewsView.render(model.state.news);
//     // PurchaseView.render(model.state[USER_STOCK][1]);
//   } catch (error) {
//     console.error(error);
//   }
// };

// const controllerPurchasePanel = async function (panelType) {
//   try {
//     await model.loadStockList(USER_STOCK);
//     PurchaseView.render(model.state[USER_STOCK][4]);
//   } catch (error) {
//     console.error(error);
//   }
// };

// controllerPurchasePanel();

// Load portfollio based on HASH
const controllerLoadPortfollio = async function () {
  try {
    const id = window.location.hash;

    if (id) return;

    PortfolioChartView.renderLoad();
    StockListView.renderLoad();
    NewsView.renderLoad();

    await model.loadStockList(USER_STOCK);
    // await model.loadStockList(WATCH_LIST);
    await model.loadNews();

    PortfolioChartView.clear();
    StockListView.clear();

    PortfolioChartView.render(model.state[USER_STOCK][0]);
    StockListView.render(model.state[USER_STOCK], USER_STOCK);
    // StockListView.render(model.state[WATCH_LIST], WATCH_LIST);
    if (model.state.stock.news) NewsView.render(model.state.stock);

    //load user stocks and watch list
    //load user portfolio
    //load current news
  } catch (error) {
    console.error(error);
  }
};

// controllerLoadPortfollio();

// const controllerSidePanels = async function () {
//   await model.loadStockList(USER_STOCK);
//   await model.loadStockList(WATCH_LIST);
//   await model.loadNews();

//   StockListView.render(model.state[USER_STOCK], USER_STOCK);
//   StockListView.render(model.state[WATCH_LIST], WATCH_LIST);
//   NewsView.render(model.state.news);
// };

const controllerChangePage = async function () {
  try {
    const val = window.location.hash.indexOf("/") + 1;
    const ticker = window.location.hash.slice(val);

    if (!ticker) return;

    PortfolioChartView.renderLoad();
    PurchaseView.renderLoad();
    NewsView.renderLoad();

    window.scrollTo(0, 0);

    await model.loadStock(ticker);
    await model.loadStockSummary(ticker);
    await model.loadNews(ticker);

    PortfolioChartView.clear();
    StockListView.clear();

    PortfolioChartView.render(model.state.stock);
    PurchaseView.render(model.state.stock);
    StockDetailsView.render(model.state.stock);
    if (model.state.stock.news) NewsView.render(model.state.stock);
  } catch (error) {
    if (error.message === "Ticker not Found") {
      MissingView.clear();
      MissingView.render();
    }
    console.error(error);
  }
};
// controllerChangePage();

const controllerPurchaseType = function (type) {
  model.updatePurchaseType(type);
  PurchaseView.clear();
  PurchaseView.render(model.state.stock);
};

const controller404Button = function () {
  try {
    console.log("called");
    MissingView.renderStart();
    window.location.hash = "stocks/AMD";
  } catch (error) {
    console.error(error);
  }
};

const init = function () {
  StockListView.addHandlerChangePage(controllerChangePage);
  StockListView.addHandlerRender(controllerLoadPortfollio);
  PortfolioChartView.addHandlerPortfolio(controllerChangePage);

  PurchaseView.addHandlerInput(controllerPurchaseType);
  PurchaseView.addHandlerWatchlist();

  NewsView.addHandlerTicker(controllerChangePage);

  MissingView.addHandlerHomeButton(controller404Button);
};

init();

// import * as model from "../model/model.js";

import * as model from "../model/model.js";
import { WATCH_LIST, USER_STOCK } from "../config.js";

import StockListView from "../view/StockListView.js";
import PurchaseView from "../view/PurchaseView.js";
import NewsView from "../view/NewsView.js";
import StockDetailsView from "../view/StockDetailsView.js";
import PortfolioChartView from "../view/PortfolioChartView.js";
import MissingView from "../view/MissingView.js";

// Load portfollio based on HASH
const controllerLoadPortfollio = async function () {
  try {
    const id = window.location.hash;

    if (id) return;

    PortfolioChartView.renderLoad();
    StockListView.renderLoad();
    NewsView.renderLoad();

    if (model.state[`${WATCH_LIST}1`]) {
      await model.loadStockList(WATCH_LIST, model.state[`${WATCH_LIST}1`]);
    }

    if (model.state.userStocks) await model.loadStockList(USER_STOCK);

    await model.loadStockList(USER_STOCK);
    // await model.loadNews();

    PortfolioChartView.clear();
    StockListView.clear();

    PortfolioChartView.render(model.state[USER_STOCK][0]);
    StockListView.render(model.state[USER_STOCK], USER_STOCK);
    StockListView.render(model.state[WATCH_LIST], WATCH_LIST);
    if (model.state.stock.news) NewsView.render(model.state.stock);

    //load user stocks and watch list
    //load user portfolio
    //load current news
  } catch (error) {
    console.error(error);
  }
};

// If hash changes or if page loads with hash not empty, load stock and render view
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
    // await model.loadNews(ticker);

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

// Change purchase type based on form purchase change
const controllerPurchaseType = function (type) {
  model.updatePurchaseType(type);
  PurchaseView.clear();
  PurchaseView.render(model.state.stock);
};

// Controller for 404 button so user can return to the home page
const controller404Button = function () {
  try {
    MissingView.renderStart();
  } catch (error) {
    console.error(error);
  }
};

const controllerWatchlist = function (ticker) {
  try {
    model.updateWatchlist(ticker);
  } catch (error) {
    console.error(error);
  }
};

//initialize event handlers
const init = function () {
  // StockListView.addHandlerChangePage(controllerChangePage);
  StockListView.addHandlerRender(controllerLoadPortfollio);
  PortfolioChartView.addHandlerPortfolio(controllerChangePage);
  PortfolioChartView.addHandlerPortfolio(controllerLoadPortfollio);

  PurchaseView.addHandlerInput(controllerPurchaseType);
  PurchaseView.addHandlerWatchlist(controllerWatchlist);

  NewsView.addHandlerTicker(controllerChangePage);

  MissingView.addHandlerHomeButton(controller404Button);
};

init();

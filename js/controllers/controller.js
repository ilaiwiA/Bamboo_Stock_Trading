// import * as model from "../model/model.js";

import * as model from "../model/model.js";

import StockListView from "../view/StockListView.js";
import PurchaseView from "../view/PurchaseView.js";
import NewsView from "../view/NewsView.js";
import StockDetailsView from "../view/StockDetailsView.js";
import PortfolioChartView from "../view/PortfolioChartView.js";
import MissingView from "../view/MissingView.js";
import UserStockDetailsView from "../view/UserStockDetailsView.js";
import HeaderListView from "../view/HeaderListView.js";
import AutoCompleteView from "../view/AutoCompleteView.js";

// Load Login and Registration Page

const controllerLogin = async function () {
  try {
    const login = window.location;
    console.log("!  @  " + login);
  } catch (error) {}
};

// Load portfollio based on HASH
const controllerLoadPortfollio = async function () {
  try {
    const id = window.location.hash;
    console.log("! " + id);

    if (id) return;

    PortfolioChartView.renderLoad();
    StockListView.renderLoad();
    NewsView.renderLoad();

    await model.loadUser();
    await model.loadPortfolio();

    // await model.loadNews();

    PortfolioChartView.clear();
    StockListView.clear();

    PortfolioChartView.render(model.state);

    StockListView.render(model.state);
    if (model.state.stock.news) NewsView.render(model.state);

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

    console.log("@ " + ticker);

    if (!ticker) return;

    await model.loadUser(ticker);

    PortfolioChartView.renderLoad();
    PurchaseView.renderLoad();
    NewsView.renderLoad();

    window.scrollTo(0, 0);

    await model.loadWatchList();

    await model.loadStock(ticker);
    await model.loadStockSummary(ticker);
    // await model.loadNews(ticker);

    PortfolioChartView.clear();
    StockListView.clear();

    PortfolioChartView.render(model.state);

    PurchaseView.render(model.state);
    UserStockDetailsView.render(model.state);
    StockDetailsView.render(model.state);
    if (model.state.stock.news) NewsView.render(model.state);
  } catch (error) {
    if (error.message === "Ticker not Found") {
      MissingView.clear();
      MissingView.render();
    }
    console.error(error);
  }
};

// Change purchase type based on form purchase change
const controllerPurchaseType = function (type, tradeType) {
  model.updatePurchaseType(type, tradeType);

  PurchaseView.clear();
  PurchaseView.render(model.state);
};

const controllerPurchaseCancel = function (cancel) {
  PurchaseView.clear();
  PurchaseView.render(model.state);
};

const controllerPurchaseSubmit = async function (
  orderBuyIn,
  orderValue,
  symbol,
  orderType
) {
  console.log("Submitted");

  await model.updateStock(orderBuyIn, orderValue.toString(), symbol, orderType);
  await model.updateUser();

  PortfolioChartView.clear();
  PurchaseView.clear();

  PortfolioChartView.render(model.state);
  PurchaseView.render(model.state);
  UserStockDetailsView.render(model.state);
  StockDetailsView.render(model.state);
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
    PurchaseView.updateWatchlistIMG();
  } catch (error) {
    console.error(error);
  }
};

const controllerPortfolioDate = async function (date) {
  await model.updateStockQuotes(date);
  PortfolioChartView.updateChart();
};

//initialize event handlers
const init = function () {
  console.log(window.location);
  PortfolioChartView.addHandlerPortfolio(controllerLoadPortfollio);
  PortfolioChartView.addHandlerPortfolio(controllerChangePage);
  PortfolioChartView.addHandlerPortfolioDate(controllerPortfolioDate);

  PurchaseView.addHandlerInput(controllerPurchaseType);
  PurchaseView.addHandlerWatchlist(controllerWatchlist);
  PurchaseView.addHandlerPurchaseForm(controllerPurchaseCancel);
  PurchaseView.addHandlerSubmit(controllerPurchaseSubmit);

  NewsView.addHandlerTicker(controllerChangePage);

  MissingView.addHandlerHomeButton(controller404Button);
};

init();

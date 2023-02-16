import { URL } from "../config.js";
import { getJSON } from "../helper.js";

export const state = {
  stock: {},
};

const generateStockList = function (data, panelType) {
  try {
    const list = [];
    data.forEach((val) => {
      const rest = val[1];
      list.push({
        // key: val[0],
        ...rest,
      });
    });

    list.forEach((a) => {
      a.lastPrice = Number(a.lastPrice).toFixed(2);
    });
    list[2].quantity = 1;
    return list;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

const generateStock = function (data) {
  try {
    return data;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

const generateStockSummary = function (data) {
  try {
    Object.keys(data).forEach((a, i) => {
      data[`${a[0].toLowerCase() + a.slice(1)}`] = data[`${a}`];
      delete data[`${a}`];
    });
    return data;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

const generateNewsObject = function (data) {
  try {
    if (!data.feed) return;

    const news = [];

    data.feed.forEach((a, i) => {
      if (i >= 5) return;
      news.push(a);
    });

    return news;
  } catch (error) {
    console.error(error);
  }
};

export const loadStockList = async function (panelType) {
  try {
    const stockList = "AMD,SPY,QQQ,AMZN,TSLA,AAPL,BBBY,GME";

    const data = [
      ...Object.entries(await getJSON(URL + "stocks/" + stockList)),
    ];
    state[`${panelType}`] = generateStockList(data, panelType);
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

export const loadStock = async function (ticker) {
  try {
    const data = Object.values(await getJSON(URL + "stock/" + ticker))[0];
    state.stock = generateStock(data);
    state.stock.availableBal = 1023.52;
  } catch (error) {
    console.error(error);
  }
};

export const loadStockSummary = async function (ticker) {
  try {
    const data = await getJSON(URL + "stock/" + ticker + "/summary");
    state.stock.summary = generateStockSummary(data);
  } catch (error) {
    console.error(error);
  }
};

export const loadNews = async function (ticker) {
  try {
    let data;
    if (ticker) data = await getJSON(`${URL}${ticker}/news`);
    else data = await getJSON(`${URL}news`);

    state.stock.news = generateNewsObject(data);
  } catch (error) {
    console.error(error);
  }
};

export const updatePurchaseType = function (type) {
  try {
    state.stock.purchaseType = type;
  } catch (error) {
    console.error(error);
  }
};

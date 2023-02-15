import { URL } from "../config.js";
import { getJSON } from "../helper.js";

export const state = {
  stock: {},
};

export const generateStockList = function (data, panelType) {
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

export const generateStock = function (data) {
  try {
    return data;
    // return ({ assetType, symbol, description, lastPrice } = stock);
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

export const generateNewsObject = function (data) {
  try {
    state.news = data.data.map((a) => a);
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

export const loadNews = async function () {
  try {
    const data = await getJSON(URL + "news");
    generateNewsObject(data);
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

import { NEWS_LIMIT, URL } from "../config.js";
import { getJSON } from "../helper.js";

export const state = {
  stock: {},
};

//Generate Data
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

const generateNewsObject = function (data, ticker) {
  try {
    if (!data.feed) return;

    const filtered = data.feed.filter((a) => a.symbol === ticker);

    return filtered.length > 1
      ? generateNewsLimit(filtered)
      : generateNewsLimit(data.feed);
  } catch (error) {
    console.error(error);
  }
};

const generateNewsLimit = function (data) {
  const news = [];

  data.forEach((a, i) => {
    if (i >= NEWS_LIMIT) return;
    news.push(a);
  });

  return news;
};

// Load Data
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

    if (!ticker || data.feed === null) {
      data = await getJSON(`${URL}news`);
    }

    if (!data) return;

    const news = generateNewsObject(data, ticker);

    const stocks = news.map((a) => a.symbol).toString();

    const stockData = Object.values(
      await getJSON(URL + "stocks/" + stocks)
    ).map((a) => {
      return a.symbol ? a.netChange : "";
    });

    news.forEach((a, i) => {
      if (stockData[i] || stockData[i] === 0) a.netChange = stockData[i];
    });

    state.stock.news = news;
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

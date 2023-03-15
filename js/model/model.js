import {
  AFTERHOURS_MINUTES,
  DAILY_MINUTES,
  NEWS_LIMIT,
  URL,
  WATCH_LIST,
} from "../config.js";
import { getJSON } from "../helper.js";

export const state = {
  stock: {},
};

//Generate Data
const generateStockList = function (data) {
  try {
    const list = [];
    data.forEach((val) => {
      const rest = val[1];
      list.push({
        ...rest,
      });
    });

    list.forEach((a) => {
      a.lastPrice = Number(a.lastPrice).toFixed(2);
    });

    return list;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

const generateStock = async function (ticker) {
  try {
    const data = Object.values(await getJSON(URL + "stock/" + ticker))[0];
    if (!data) throw Error;

    return data;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

const generateStockQuotes = async function (ticker, periodType = "week") {
  try {
    const data = Object.values(
      await getJSON(
        URL +
          "stock/" +
          ticker +
          "/quotes/" +
          `${periodType === "day" ? "" : periodType}`
      )
    )[0];

    return {
      dates: data
        .sort((a, b) => a.datetime - b.datetime)
        .map((a) => a.datetime),
      // .concat(
      //   Array(+`${periodType === "day" ? `${AFTERHOURS_MINUTES}` : 0}`).fill()
      // ),
      prices: data,
      timePeriod: periodType,
    };
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

    const filterSource = data.feed.filter((a) => a.source !== "Benzinga");

    const filterSymbol = filterSource.filter((a) => a.symbol === ticker);

    return filterSymbol.length > 1
      ? generateNewsLimit(filterSymbol)
      : generateNewsLimit(filterSource);
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
export const loadStockList = async function (
  panelType,
  stockList = "AMD,SPY,QQQ,AMZN,TSLA,AAPL,BBBY,GME"
) {
  try {
    if (stockList.length === 0) return delete state[`${panelType}`];
    const data = [
      ...Object.entries(await getJSON(URL + "stocks/" + stockList)),
    ];
    state[`${panelType}`] = generateStockList(data);
  } catch (error) {
    throw error;
  }
};

export const loadStock = async function (ticker) {
  try {
    state.stock = await generateStock(ticker);
    state.stock.quotes = await generateStockQuotes(ticker);
    state.stock.availableBal = 1023.52;
  } catch (error) {
    throw new Error("Ticker not Found");
  }
};

export const updateStockQuotes = async function (date) {
  try {
    if (!state.stock) return;

    state.stock.quotes = await generateStockQuotes(state.stock.symbol, date);
    console.log(state.stock.quotes);
    state.stock.quotes.timePeriod = date;
  } catch (error) {
    throw new Error("Ticker not Found");
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
    // !data.feed
    if (!ticker || data.feed === null) {
      data = await getJSON(`${URL}news`);
    }

    if (!data) return;

    const news = generateNewsObject(data, ticker);

    const stocks = news
      .map((a) => {
        if (a.symbol.startsWith("FOREX") || !a.symbol) return "";

        return a.symbol;
      })
      .toString();

    const stockData = Object.values(
      await getJSON(URL + "stocks/" + stocks)
    ).map((a, i, arr) => {
      return a.symbol
        ? {
            symbol: a.symbol,
            netChange: a.netChange,
          }
        : "";
    });

    news.forEach((a, i, arr) => {
      const index = stockData.findIndex((price) => price.symbol === a.symbol);
      if (index !== -1) a.netChange = stockData[index].netChange;
    });

    state.stock.news = news;
  } catch (error) {
    console.error(error);
  }
};

// Specific Functions
export const updatePurchaseType = function (type) {
  try {
    state.stock.purchaseType = type;
  } catch (error) {
    console.error(error);
  }
};

export const updateWatchlist = async function (ticker) {
  try {
    if (state[`${WATCH_LIST}1`]) {
      if (state[`${WATCH_LIST}1`].includes(ticker)) {
        return state[`${WATCH_LIST}1`].splice(
          state[`${WATCH_LIST}1`].indexOf(ticker),
          1
        );
      }

      return state[`${WATCH_LIST}1`].push(ticker);
    }

    return (state[`${WATCH_LIST}1`] = [ticker]);
  } catch (error) {
    console.error(error);
  }
};

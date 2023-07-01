import {
  AFTERHOURS_MINUTES,
  DAILY_1_MIN_INTERVAL,
  DAILY_5_MIN_INTERVAL,
  DAILY_MINUTES,
  DAILY_PRICE_CONFIG,
  NEWS_LIMIT,
  PAST_PRICE_CONFIG,
  RECENT_PRICE_CONFIG,
  TOP_LIST,
  UNIX_MS_PER_5_MINUTE,
  UNIX_MS_PER_MINUTE,
  URL,
  URL_AUTH,
  URL_LOGIN,
  USER_STOCK,
  WATCH_LIST,
  tickers,
} from "../config.js";
import { getJSON, getPayload, postJSON } from "../helper.js";

export const state = {
  stock: {},
};

//Generate Data
const generateStockList = function (data) {
  try {
    const list = data;

    list.forEach((a, i) => {
      a.lastPrice = Number(a.lastPrice).toFixed(2);
      a.quantity = state.userStocks.find(
        (val) => val.ticker === a.symbol
      )?.quantity;
    });

    return list;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

const generateStock = async function (ticker) {
  try {
    const data = await getJSON(URL + "stock/" + ticker);
    if (!data) throw Error;

    return data;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
    window.location = "/";
  }
};

const generateStockQuotes = async function (
  ticker,
  periodType = "day",
  sideChart = false
) {
  try {
    const endpointURL =
      ticker === "portfolio"
        ? URL + "user/portfolio/" + periodType
        : URL +
          "stock/" +
          ticker +
          "/quotes/" +
          `${periodType === "day" ? "" : periodType}` +
          `${sideChart ? "reduced" : ""}`;

    const data = await getJSON(endpointURL);

    const timeStart = new Date();
    let dailyTimeData = [];
    let dailyPriceData = [];

    let isWeekday =
      periodType === "day" &&
      timeStart.getDay() != 0 &&
      timeStart.getDay() != 6 &&
      timeStart.getHours() >= 6;

    if (
      (timeStart.getDay() === 1 && timeStart.getHours() < 7) ||
      new Date(data[0].datetime).getDay() != timeStart.getDay()
    )
      isWeekday = false;

    if (isWeekday) {
      timeStart.setHours(6);
      timeStart.setMinutes(0);
      timeStart.setSeconds(0);
      timeStart.setMilliseconds(0);

      let datetime = timeStart.getTime();

      const currentTime = new Date();
      currentTime.setSeconds(0);
      currentTime.setMilliseconds(0);

      dailyTimeData = [...new Array(DAILY_5_MIN_INTERVAL)].map((_, i) => {
        return datetime + UNIX_MS_PER_5_MINUTE * i;
      });

      const index = dailyTimeData.findIndex((a) => a > currentTime.getTime());

      if (currentTime.getMinutes() % 5 != 0 && index != -1)
        dailyTimeData.splice(index, 0, currentTime.getTime());

      let x = 0;

      dailyPriceData = dailyTimeData
        .map((val, i, arr) => {
          if (x === data.length - 1 && val <= new Date().getTime()) {
            const a = { ...data[x] };
            a.datetime = val;
            return a;
          }

          if (x > data.length - 1) {
            return;
          }

          if (val < data[x].datetime) {
            const a = { ...data[x] };
            a.datetime = val;

            if (arr[i + 1] >= data[x + 1]?.datetime) {
              x++;
            }
            return a;
          } else if (val === data[x].datetime) {
            const a = { ...data[x] };
            a.datetime = val;
            x++;
            return a;
          }
        })
        .filter((a) => a);
    }

    const stockPrices = dailyPriceData.length > 0 ? dailyPriceData : data;

    const stockDates = dailyTimeData.length > 0 ? dailyTimeData : data;

    const filteredDates = stockDates.map((a) => {
      return new Intl.DateTimeFormat(
        "en-US",
        periodType === "day"
          ? DAILY_PRICE_CONFIG
          : periodType === "week"
          ? RECENT_PRICE_CONFIG
          : PAST_PRICE_CONFIG
      ).format(a.datetime || a);
    });

    const dates = [...filteredDates];

    const preMarket = dates.findIndex((a) => a.includes("8:30"));
    const postMarket = dates.findIndex((a) => a.includes("3:00"));

    return {
      dates: dates,

      preDates: dates.filter((_, i) => i < preMarket),

      intraDates: dates.filter(
        (_, i) =>
          i >= preMarket &&
          i <= (postMarket === -1 ? dates.length - 1 : postMarket)
      ),

      postDates: dates.filter(
        (_, i) => i > (postMarket === -1 ? dates.length - 1 : postMarket)
      ),

      prices: stockPrices,

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

    const filterSource = data.feed.filter(
      (a) =>
        a.source !== "Benzinga" ||
        a.symbol.includes("CRYPTO") ||
        a.symbol.includes("FOREX")
    );

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

// load user
export const loadUser = async function (ticker) {
  try {
    const {
      firstName,
      userName,
      portfolio: {
        stocks,
        watchList,
        stockList,
        availableBalance: availableBal,
        totalBalance,
      },
    } = await getJSON(URL + "user");

    state["firstName"] = firstName;
    state["userName"] = userName;
    state[`availableBal`] = availableBal;
    state[`totalBal`] = totalBalance.toFixed(2);
    state["userStocks"] = stocks;

    if (!ticker) {
      await loadStockList(watchList, WATCH_LIST);
      await loadStockList(stockList, USER_STOCK);
    }

    if (!watchList && stockList.length < 1 && !ticker) {
      await loadStockList(
        ["AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "TSLA"],
        TOP_LIST
      );
    }
  } catch (error) {
    throw new Error(401);
  }
};

export const loadPortfolio = async function () {
  try {
    const data = await generateStockQuotes("portfolio", "day", false);

    state.stock = {};
    state.stock.symbol = "portfolio";
    state.stock.lastPrice = data.prices[data.prices.length - 1].close;
    state.stock.netChange = state.stock.lastPrice - data.prices[0].previous;
    state.stock.netPercentChangeInDouble =
      ((state.stock.lastPrice - data.prices[0].previous) /
        data.prices[0].previous) *
      100;
    state.stock.closePrice = data.prices[0].previous;
    state.stock.quotes = data;
    state.stock.quotes.timePeriod = "day";
  } catch (error) {
    console.error(error);
  }
};

// Load Data
export const loadStockList = async function (stockList, panelType) {
  try {
    if (!stockList?.length) return delete state[`${panelType}`];

    const dataList = [
      ...Object.values(await getJSON(URL + "stocks/" + stockList)),
    ];

    for (data of dataList) {
      data.quotes = await generateStockQuotes(data.symbol, "day", true);
    }

    state[`${panelType}`] = generateStockList(dataList);
  } catch (error) {
    throw error;
  }
};

export const loadWatchList = async function () {
  try {
    const data = await getJSON(URL + "user/" + `${WATCH_LIST}`);

    if (!data) return;

    state.watched = [...Object.values(data)];
  } catch (error) {
    throw error;
  }
};

export const loadStock = async function (ticker) {
  try {
    state.stock = await generateStock(ticker);
    state.stock.quotes = await generateStockQuotes(ticker);
    if (state.userStocks)
      state.stock.user = state?.userStocks.find((a) => a.ticker === ticker);

    if (state.watched?.includes(ticker)) state.stock.bookmarked = true;
  } catch (error) {
    throw new Error("Ticker not Found");
  }
};

export const updateStockQuotes = async function (date) {
  try {
    if (!state.stock) {
      return;
    }

    const ticker =
      state.stock.symbol === "portfolio" ? "portfolio" : state.stock.symbol;

    state.stock.quotes = await generateStockQuotes(ticker, date);
    state.stock.quotes.timePeriod = date;
  } catch (error) {
    throw new Error("Ticker not Found");
  }
};

export const loadStockSummary = async function (ticker) {
  try {
    const data = await getJSON(URL + "stock/" + ticker + "/summary");
    const summary = generateStockSummary(data);

    if (Object.keys(summary).length === 0) return;
    state.stock.summary = summary;
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
            netPercentChangeInDouble: a.netPercentChangeInDouble,
          }
        : "";
    });

    news.forEach((a, i, arr) => {
      const index = stockData.findIndex((price) => price.symbol === a.symbol);
      if (index !== -1) {
        a.netChange = stockData[index].netChange;
        a.netPercentChangeInDouble = stockData[index].netPercentChangeInDouble;
      }
    });

    state.stock.news = news;
  } catch (error) {
    console.error(error);
  }
};

// Specific Functions
export const updatePurchaseType = function (type, tradeType) {
  try {
    state.stock.purchaseType = type;
    state.stock.tradeType = tradeType;
  } catch (error) {
    console.error(error);
  }
};

//clean this up man
export const updateWatchlist = async function (ticker) {
  try {
    /*
      1. Check if watchlist exists, if it doesnt, create and add to state then push change to server
      2. If watchlist exists and ticker is found, remove it from state and push change to server
      3. if watchlust exists and ticker is not found, add to state then push change to server.
    */

    const res = await postJSON(URL + "user/portfolio/watchlist", { ticker });
  } catch (error) {
    console.error(error);
  }
};

export const updateStock = async function (
  orderBuyIn,
  orderValue,
  symbol,
  orderType
) {
  try {
    const status =
      orderType === "buy"
        ? await postJSON(URL + "user/purchase", {
            orderBuyIn,
            orderValue,
            symbol,
          })
        : await postJSON(URL + "user/sell", { orderBuyIn, orderValue, symbol });

    if ((await status.text()) === "all shares sold")
      delete state.stock.tradeType;
  } catch (error) {
    console.error(error);
  }
};

export const updateUser = async function () {
  try {
    const {
      firstName,
      userName,
      portfolio: { stocks, availableBalance: availableBal },
    } = await getJSON(URL + "user");

    state["firstName"] = firstName;
    state["userName"] = userName;
    state[`availableBal`] = availableBal;
    state["userStocks"] = stocks;
  } catch (error) {
    console.error(error);
  }
};

export const logout = async function () {
  try {
    console.log("LOGGED OUT");
    const response = await getJSON(URL_AUTH + "logout");
    if (response.response === "success")
      window.location.href = "/Bamboo-Stock-Trading/LoginPage.html";
  } catch (error) {
    throw error;
  }
};

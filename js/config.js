export const TIME_OUT = 5;

// export const URL = "http://192.168.0.215:8080/api/"; // window
export const URL = "http://192.168.0.198:8080/api/" // macbook

export const USER_STOCK = "stocklist";
export const WATCH_LIST = "watchlist";

export const NEWS_LIMIT = 10;

export const AFTERHOURS_MINUTES = 240; // 3pm to 7pm

export const DAILY_PRICE_CONFIG = {
  hour: "numeric",
  minute: "numeric",
};

export const PAST_PRICE_CONFIG = {
  month: "short",
  day: "numeric",
  year: "numeric",
  hour: "numeric",
  minute: "numeric",
};

// const tickerList = "/images/tickers.csv";

import tickerList from "url:/images/tickers.csv"; //prevents chrome due to CORS

export const tickers = fetch(tickerList)
  .then((a) => a.text())
  .then((a) => a.split(","));

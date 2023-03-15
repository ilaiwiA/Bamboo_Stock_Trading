export const TIME_OUT = 5;

export const URL = "http://192.168.0.215:8080/api/";

export const USER_STOCK = "userStocks";
export const WATCH_LIST = "watchList";

export const NEWS_LIMIT = 10;

export const AFTERHOURS_MINUTES = 240; // 3pm to 7pm

// const tickerList = "/images/tickers.csv";

import tickerList from "url:/images/tickers.csv"; //prevents chrome due to CORS

export const tickers = fetch(tickerList)
  .then((a) => a.text())
  .then((a) => a.split(","));

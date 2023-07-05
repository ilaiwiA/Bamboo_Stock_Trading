export const TIME_OUT = 10;

export const URL = "https://192.168.0.215:8080/api/"; // window
export const URL_AUTH = "https://192.168.0.215:8080/auth/";

// export const URL = "https://bamboospring-production.up.railway.app/api/"; // prod server
// export const URL_AUTH = "https://bamboospring-production.up.railway.app/auth/";
export const LOGIN_REDIRECT = "/Bamboo-Stock-Trading/Register.html";

export const USER_STOCK = "stocklist";
export const WATCH_LIST = "watchlist";
export const TOP_LIST = "toplist";

export const NEWS_LIMIT = 10;

// export const DAILY_1_MIN_INTERVAL = 600; // 7am to 5pm at 1 min interval
// export const DAILY_5_MIN_INTERVAL = 120; // 7am to 5pm at 5 min interval
// export const DAILY_15_MIN_INTERVAL = 40; // 7am to 5pm at 15 min interval
export const DAILY_1_MIN_INTERVAL = 780; // 5pm to 7pm at 1 min interval
export const DAILY_5_MIN_INTERVAL = 156; // 5pm to 7pm at 5 min interval
export const DAILY_15_MIN_INTERVAL = 52; // 5pm to 7pm at 15 min interval
export const UNIX_MS_PER_MINUTE = 60000;
export const UNIX_MS_PER_5_MINUTE = 300000;
export const UNIX_MS_PER_15_MINUTE = 900000;

export const CHART_UPDATE_INTERVAL = 7500;

export const DAILY_PRICE_CONFIG = {
  hour: "numeric",
  minute: "numeric",
};

export const RECENT_PRICE_CONFIG = {
  month: "short",
  day: "numeric",
  hour: "numeric",
  minute: "numeric",
};

export const DISPLAY_PRICE_CONFIG = {
  month: "short",
  day: "numeric",
  year: "numeric",
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

// import tickerList from "url:/images/tickers_noSpecialChart.csv"; //prevents chrome due to CORS
import tickerList from "url:./images/tickers_noSpecialChart_withName.csv"; //prevents chrome due to CORS

export const tickers = fetch(tickerList)
  .then((a) => a.text())
  .then((a) =>
    a
      .split("\n")
      .map((a) => {
        const tickerArr = a.split(",");
        tickerArr[1] = tickerArr[1]?.slice(0, -1);

        if (
          tickerArr[1] === "undefined" ||
          !tickerArr[1] ||
          tickerArr[1].length < 1 ||
          tickerArr[0] === "ZZZ"
        ) {
          return;
        }
        return tickerArr;
      })
      .filter((a) => a)
  );

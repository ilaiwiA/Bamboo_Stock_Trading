import { USER_STOCK, WATCH_LIST } from "../config.js";
import View from "./View.js";

class StockListView extends View {
  _parentElement = document.querySelector(".aside-container");
  _data;

  constructor() {
    super();
  }

  render(data, panelType) {
    this._data = data;

    const html = this._generateHTML(panelType);

    this._parentElement.insertAdjacentHTML("afterbegin", html);
  }

  _generateHTML = function (panelType) {
    return `
        <div class="side-container">
          <h1>${panelType === WATCH_LIST ? "Watchlist" : "My Stocks"}</h1>
          <div class="stocks-panel panel">
          ${this._data.map(this._generateStocks).join("")}
          </div>
        </div>
        `;
  };

  _generateStocks = function (data) {
    return `
    <div class="stocks" id = "${data.ticker}">
        <div class="ticker">
            <p>${data.ticker}</p>
            ${
              data.quantity
                ? `<p>${data.quantity} ${
                    data.quantity > 1 ? "Shares" : "Share"
                  }`
                : ""
            }
        </div>
    
        <div class="ticker-graph">jwadjaj</div>
    
        <div class="ticker-price">${data.lastPrice}</div>    
    </div>
    `;
  };
}

export default new StockListView();

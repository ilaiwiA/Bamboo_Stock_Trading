import { USER_STOCK, WATCH_LIST } from "../config.js";
import View from "./View.js";

class StockListView extends View {
  _parentElement = document.querySelector(".aside-container");
  _data;

  constructor() {
    super();
  }

  // addHandlerChangePage(handler) {
  //   this._parentElement
  //     .querySelector(".side-container")
  //     ?.addEventListener("click", function (e) {
  //       console.log("test");
  //       handler(e.target.closest(".stocks")?.id);
  //     });
  // }

  addHandlerRender(handler) {
    ["load"].forEach((a) =>
      window.addEventListener(a, function () {
        handler();
      })
    );
  }

  render(data, panelType) {
    if (!data) return;

    this._data = data;

    const html = this._generateHTML(panelType);

    this._parentElement.insertAdjacentHTML("beforeend", html);
  }

  _generateHTML = function (panelType) {
    return `
        <div class="side-container">
          <h1>${panelType === WATCH_LIST ? "Watchlist" : "My Stocks"}</h1>
          <div class="stocks-panel panel">
          ${this._data.map(this._generateStocks.bind(this)).join("")}
          </div>
        </div>
        `;
  };

  _generateStocks = function (data) {
    return `
    <a class="stocks" id = "${data.symbol}" href="#stocks/${data.symbol}">
        <div class="ticker">
            <p>${data.symbol}</p>
            ${
              data.quantity
                ? `<p class= "ticker-sub">${data.quantity} ${
                    data.quantity > 1 ? "Shares" : "Share"
                  }`
                : ""
            }
        </div>
    
        <div class="ticker-graph">jwadjaj</div>
    
        <div class="ticker-price ticker">
        <p>${data.lastPrice}<p>
        <p class= "ticker-sub ${this._generateColor(
          data.netPercentChangeInDouble
        )}">${Number(data.netPercentChangeInDouble).toFixed(2)}%<p>
        </div>    
    </a>
    `;
  };
}

export default new StockListView();

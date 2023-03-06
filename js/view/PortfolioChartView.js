import View from "./View.js";

class PortfolioChartView extends View {
  _parentElement = document.querySelector(".main-container");

  constructor() {
    super();
  }

  addHandlerPortfolio(handler) {
    ["hashchange", "load"].forEach((a) => window.addEventListener(a, handler));
  }

  _generateHTML() {
    return `
    <div class="portfolio-container">
        <div class="portfolio-chart-container">
          <div class="chart-info">
          ${this._data.symbol ? `<h1>${this._data.symbol}</h1>` : ""}
            <h1>${Number(this._data.lastPrice).toFixed(2)}</h1>
            <span class="${this._generateColor(
              +this._data.netChange
            )}">${this._generateSymbol(+this._data.netChange)}${Number(
      this._data.netChange
    ).toFixed(2)} <span>(${this._data.netPercentChangeInDouble.toFixed(2)}${
      this._data.netPercentChangeInDouble === 0 ? "" : "%"
    })</span></span>
            <span>Today</span>
          </div>
          <section class="portfolio-chart"></section>
        </div>

        <ul>
          <li id="day">1D</li>
          <li id="week">1W</li>
          <li id="month">1M</li>
          <li id="3month">3M</li>
          <li id="ytd">YTD</li>
          <li id="all">ALL</li>
        </ul>
        </div>
        
        ${
          this._data.symbol
            ? `
        <div class="sub-panel" id="buying-power">
          <div class="portfolio-bp">
            <p>Buying Power</p>
            <p>$${this._data.availableBal || "0.00"}</p>
         </div>
        </div>
        `
            : ""
        }
        `;
  }
}

export default new PortfolioChartView();

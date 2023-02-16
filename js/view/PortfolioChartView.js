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
          <li>1D</li>
          <li>1W</li>
          <li>1M</li>
          <li>3M</li>
          <li>3D</li>
          <li>YTD</li>
          <li>ALL</li>
        </ul>
        </div>
        
        <div class="sub-panel" id="buying-power">
          <div class="portfolio-bp">
            <p>Buying Power</p>
            <p>$${this._data.availableBal || "0.00"}</p>
          </div>
        </div>
        `;
  }
}

export default new PortfolioChartView();

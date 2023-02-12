import View from "./View.js";

class PortfolioChartView extends View {
  _parentElement = document.querySelector(".portfolio-container");

  addHandlerPortfolio(handler) {
    ["hashchange"].forEach((a) => window.addEventListener(a, handler));
  }

  _generateHTML() {
    return `
        <div class="portfolio-chart-container">
          <div class="chart-info">
            <h1>${this._data.symbol}</h1>
            <h1>${this._data.lastPrice}</h1>
            <span class="negative">${
              this._data.netChange
            } <span>(${this._data.netPercentChangeInDouble.toFixed(2)} ${
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
        `;
  }
}

export default new PortfolioChartView();

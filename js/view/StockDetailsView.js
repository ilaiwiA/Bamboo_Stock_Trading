import View from "./View.js";

class StockDetailsView extends View {
  _parentElement = document.querySelector(".main-container");

  _generateHTML() {
    return this._generateAboutSection() + this._generateStatisticsSection();
  }

  _generateAboutSection() {
    if (!this._data.summary || this._data.summary.description === "None")
      return "";
    return `
        <section>
          <h1>About</h1>
          <div class="sub-panel">
            <p class="stock-description">
            ${this._data.summary.description}
            </p>

            <ul>
              <li>
                <p><b>Industry</b></p>
                <p>${this._data.summary.industry}</p>
              </li>
              <li>
                <p><b>Employees</b></p>
                <p>15,500</p>
              </li>
              <li>
                <p><b>Headquarters</b></p>
                <p>${this._data.summary.address}</p>
              </li>
              <li>
                <p><b>Founded</b></p>
                <p>1969</p>
              </li>
            </ul>
          </div>
        </section>
        
        `;
  }

  _generateStatisticsSection() {
    return `
        <section>
          <h1>Key statistics</h1>
          <div class="sub-panel">
            <ul>
              <li>
                <p><b>Market cap</b></p>
                <p>${
                  Number.isFinite(this._data.summary?.marketCapitalization)
                    ? Intl.NumberFormat("en-US", {
                        notation: "compact",
                        maximumFractionDigits: 2,
                      }).format(this._data.summary?.marketCapitalization)
                    : "-"
                }</p>
              </li>
              <li>
                <p><b>Price-Earnings ratio</b></p>
                <p>${this._data.peRatio.toFixed(2) || "-"}</p>
              </li>
              <li>
                <p><b>Dividend yield</b></p>
                <p>${this._data.divAmount ? this._data.divAmount : "-"}</p>
              </li>
              <li>
                <p><b>Average volume</b></p>
                <p>-</p>
              </li>
              <li>
                <p><b>High today</b></p>
                <p>${this._data.highPrice.toFixed(2) || "-"}</p>
              </li>
              <li>
                <p><b>Low today</b></p>
                <p>${this._data.lowPrice.toFixed(2) || "-"}</p>
              </li>
              <li>
                <p><b>Open price</b></p>
                <p>${this._data.openPrice.toFixed(2) || "-"}</p>
              </li>
              <li>
                <p><b>Volume</b></p>
                <p>${
                  Number.isFinite(this._data.totalVolume)
                    ? Intl.NumberFormat("en-US", {
                        notation: "compact",
                        maximumFractionDigits: 2,
                      }).format(this._data.totalVolume)
                    : "-"
                }</p>
              </li>
              <li>
                <p><b>52 Week high</b></p>
                <p>${this._data[`52WkHigh`].toFixed(2) || "-"}</p>
              </li>
              <li>
                <p><b>52 Week low</b></p>
                <p>${this._data[`52WkLow`].toFixed(2) || "-"}</p>
              </li>
            </ul>
          </div>
        </section>
        `;
  }
}

export default new StockDetailsView();

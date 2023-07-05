import { Chart } from "chart.js/auto";
import annotationPlugin from "chartjs-plugin-annotation";
import { TOP_LIST, USER_STOCK, WATCH_LIST } from "../config.js";
import View from "./View.js";

Chart.register(annotationPlugin);

class StockListView extends View {
  _parentElement = document.querySelector(".aside-container");
  _data;

  constructor() {
    super();
  }

  addHandlerRender(handler) {
    ["load"].forEach((a) =>
      window.addEventListener(a, function () {
        handler();
      })
    );
  }

  _generateHTML = function () {
    return `${this._generatePanelTypes()
      .map(this._generatePanel.bind(this))
      .join("")}`;
  };

  _generatePanel = function (panelType) {
    return `
        <div class="side-container ${panelType}">
          <h1>${this._generatePanelType(panelType)}</h1>
          <div class="stocks-panel panel">
          ${this._user[`${panelType}`]
            .map(this._generateStocks.bind(this))
            .join("")}
          </div>
        </div>
        `;
  };

  _generatePanelType = function (panelType) {
    switch (panelType) {
      case WATCH_LIST:
        return "Watchlist";
      case USER_STOCK:
        return "My Stocks";

      case TOP_LIST:
        return "Top Stocks";
    }
  };

  _generateStocks = function (data) {
    return `
    <a class="stocks" id = "${data.symbol}" href="#stocks/${data.symbol}">
        <div class="ticker">
            <p>${data.symbol}</p>
            ${
              data.quantity
                ? `<p class= "ticker-sub user-shares">${data.quantity} ${
                    data.quantity > 1 ? "Shares" : "Share"
                  }`
                : ""
            }
        </div>
    
        <canvas class="ticker-graph">GRAPH</canvas>
    
        <div class="ticker-price ticker">
        <p>${data.lastPrice}<p>
        <p class= "ticker-sub ${this._generateColor(
          data.netPercentChangeInDouble
        )}">${Number(data.netPercentChangeInDouble).toFixed(2)}%<p>
        </div>    
    </a>
    `;
  };

  _generateChart() {
    const panelType = this._generatePanelTypes();

    panelType.map(this.createChart.bind(this));
  }

  createChart(panelType) {
    if (!this._user[`${panelType}`][0].quotes) return;

    const tickers = this._parentElement
      .querySelector(`.${panelType}`)
      .querySelector(".stocks-panel");

    for (let i = 0; i < tickers.children.length; i++) {
      const dailyLine = {
        type: "line",
        yMax: this._user[`${panelType}`][i].closePrice,
        yMin: this._user[`${panelType}`][i].closePrice,
        borderColor: "rgb(123, 123, 123)",
        borderDash: [1, 5],
      };

      const rgb = this._generateRGB(this._user[`${panelType}`][i].netChange);

      new Chart(tickers.children[i].querySelector("canvas"), {
        type: "line",
        data: {
          labels: this._user[`${panelType}`][i].quotes.dates,
          datasets: [
            {
              data: this._user[`${panelType}`][i].quotes.prices.map(
                (a) => a.close
              ),
            },
          ],
        },
        options: {
          borderColor: rgb,

          hover: {
            mode: null,
          },

          animation: {
            animation: false,
          },

          elements: {
            point: {
              radius: 0,
            },
            line: {
              borderWidth: 2,
            },
          },

          plugins: {
            annotation: {
              annotations: {
                line1: dailyLine,
              },
            },
            legend: {
              display: false,
            },
            tooltip: {
              enabled: false,
            },
          },

          scales: {
            y: {
              display: false,
              // beginAtZero: true,
              grid: {
                display: false,
              },
            },
            x: {
              display: false,
              grid: {
                display: false,
              },
            },
          },
        },
      });
    }
  }
}

export default new StockListView();

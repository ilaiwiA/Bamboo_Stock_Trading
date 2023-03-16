import { Chart } from "chart.js/auto";
import annotationPlugin from "chartjs-plugin-annotation";
import View from "./View.js";

Chart.register(annotationPlugin);

class PortfolioChartView extends View {
  _parentElement = document.querySelector(".main-container");
  _dailyPrice = {
    hour: "numeric",
    minute: "numeric",
  };

  _pastPrice = {
    month: "short",
    day: "numeric",
    year: "numeric",
  };

  constructor() {
    super();
  }

  addHandlerPortfolio(handler) {
    ["hashchange", "load"].forEach((a) => window.addEventListener(a, handler));
  }

  addHandlerPortfolioDate(handler) {
    this._parentElement.addEventListener("click", function (e) {
      if (e.target.closest(".portfolio-dates") && e.target.id !== "")
        handler(e.target.id);
    });
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
          <canvas id="portfolio-chart"></canvas>
        </div>

        <ul class="portfolio-dates">
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

  updateChart() {
    const dailyLine =
      this._data.quotes.timePeriod === "day"
        ? {
            type: "line",
            yMax: this._data.closePrice,
            yMin: this._data.closePrice,
            borderColor: "rgb(123, 123, 123)",
            borderDash: [1, 5],
          }
        : "";

    this.myChart.data.labels = this._data.quotes.dates.map((a) => {
      return new Intl.DateTimeFormat(
        "en-US",
        this._data.quotes.timePeriod === "day"
          ? this._dailyPrice
          : this._pastPrice
      ).format(a);
    });

    this.myChart.data.datasets[0].data = this._data.quotes.prices.map(
      (a) => a.close
    );

    this.myChart.options.plugins.annotation.annotations.line1 = dailyLine;

    this.myChart.options.borderColor = this._generateRGB(this._data.netChange);

    this.myChart.update();
  }

  _generateChart() {
    const mainChart = document.querySelector("#portfolio-chart");

    const rgb = this._generateRGB(this._data.netChange);

    const dailyLine =
      this._data.quotes.timePeriod === "day"
        ? {
            type: "line",
            yMax: this._data.closePrice,
            yMin: this._data.closePrice,
            borderColor: "rgb(123, 123, 123)",
            borderDash: [1, 5],
          }
        : "";

    this.myChart = new Chart(mainChart, {
      type: "line",
      data: {
        labels: this._data.quotes.dates.map((a) => {
          return new Intl.DateTimeFormat(
            "en-US",
            this._data.quotes.timePeriod === "day"
              ? this._dailyPrice
              : this._pastPrice
          ).format(a);
        }),
        datasets: [
          {
            data: this._data.quotes.prices.map((a) => a.close),
          },
        ],
      },
      options: {
        borderColor: rgb,

        onHover: this._generateVerticalLine.bind(this),

        hover: {
          intersect: false,
          axis: "x",
        },
        animation: {
          animation: false,
        },

        elements: {
          point: {
            radius: 0,
            hoverRadius: 6,
            backgroundColor: rgb,
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

  _generateRGB(netChange) {
    return this._generateColor(+netChange) === "positive_green"
      ? "rgb(0,200,0)"
      : "rgb(253,82,64)";
  }

  _generateVerticalLine(e, active, chart) {
    this.myChart.options.plugins.annotation.annotations.verticalLine = {
      type: "line",
      xMin: this.myChart.data.labels[active[0].index],
      xMax: this.myChart.data.labels[active[0].index],
      borderColor: this._generateRGB(this._data.netChange),
      borderWidth: 2,
    };
    this.myChart.update();
  }
}

export default new PortfolioChartView();

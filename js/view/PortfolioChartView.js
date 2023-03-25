import { Chart } from "chart.js/auto";
import annotationPlugin from "chartjs-plugin-annotation";
import View from "./View.js";

Chart.register(annotationPlugin);

class PortfolioChartView extends View {
  _parentElement = document.querySelector(".main-container");

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
        ${this._generatePriceHTML()}
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

  _generatePriceHTML() {
    return `
    <div class="chart-info">
    ${this._data.symbol ? `<h1>${this._data.symbol}</h1>` : ""}
      <h1 class="ticker-price">$${Number(this._data.lastPrice).toFixed(2)}</h1>
      <span class="${this._generateColor(
        +this._data.netChange
      )} ticker-change">${this._generateSymbol(+this._data.netChange)}${Number(
      this._data.netChange
    ).toFixed(2)} <span>(${this._data.netPercentChangeInDouble.toFixed(2)}${
      this._data.netPercentChangeInDouble === 0 ? "" : "%"
    })</span></span>
      <span class="ticker-date">${
        this._generateDate(this._data.quotes?.timePeriod) || ""
      }</span>
    </div>
    `;
  }

  updateChart() {
    const rgb = this._generateRGB(
      this._data.quotes.timePeriod === "day"
        ? this._data.netChange
        : this._data.quotes.prices.slice(-1)[0].close -
            this._data.quotes.prices[0].close
    );

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

    this.myChart.data.labels = this._data.quotes.dates;

    this.myChart.data.datasets[0].data = this._data.quotes.prices.map(
      (a) => a.close
    );

    this.myChart.options.plugins.annotation.annotations.line1 = dailyLine;

    this.myChart.options.borderColor = rgb;

    this._updatePrice(
      +this._data.lastPrice,
      +this._data.quotes.prices[0].close
    );

    this.myChart.update();
  }

  _generateChart() {
    const mainChart = document.querySelector("#portfolio-chart");

    this._data;

    const rgb = this._generateRGB(
      this._data.quotes.timePeriod === "day"
        ? this._data.netChange // default
        : this._data.quotes.prices.slice(-1)[0].close -
            this._data.quotes.prices[0].close
    );

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
        labels: this._data.quotes.dates,
        datasets: [
          {
            data: this._data.quotes.prices.map((a) => a.close),
          },
        ],
      },
      options: {
        borderColor: rgb,

        onHover: this._onHover.bind(this),

        interaction: {
          mode: "index",
          intersect: false,
          axis: "x",
          animation: false,
        },

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
          clearHover: {
            test: "hey",
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
      plugins: [
        {
          id: "clearHover",
          afterEvent: this._onMouseOut.bind(this),
        },
      ],
    });
    this._updatePrice(
      +this._data.lastPrice,
      +this._data.quotes.prices[0].close
    );
  }

  _generateRGB(netChange) {
    return this._generateColor(+netChange) === "positive_green"
      ? "rgb(0,200,0)"
      : "rgb(253,82,64)";
  }

  _generateDate(date) {
    switch (date) {
      case "day":
        return "Today";

      case "week":
        return "Past week";

      case "month":
        return "Past month";

      case "3month":
        return "Past 3 months";

      case "ytd":
        return "Year to date";

      case "all":
        return "Past 5 years";

      default:
        break;
    }
  }

  _updatePrice(currentPrice, previousPrice) {
    const tickerPrice = document.querySelector(".ticker-price");
    const tickerNetChange = document.querySelector(".ticker-change");
    const tickerDate = document.querySelector(".ticker-date");

    const netChangeColor = tickerNetChange.classList[0];
    const netChange = (currentPrice - previousPrice).toFixed(2);
    const netChangePercent = (
      ((currentPrice - previousPrice) / previousPrice) *
      100
    ).toFixed(2);

    tickerPrice.innerHTML = "$" + currentPrice.toFixed(2);

    tickerNetChange.classList.replace(
      netChangeColor,
      this._generateColor(netChangePercent)
    );

    tickerNetChange.textContent = `${netChange} (${netChangePercent}%)`;
    tickerDate.textContent = this._generateDate(this._data.quotes.timePeriod);
  }

  _onHover(e, active, chart) {
    const rgb = this._generateRGB(
      this._data.quotes.timePeriod === "day"
        ? this._data.netChange
        : this._data.quotes.prices.slice(-1)[0].close -
            this._data.quotes.prices[0].close
    );

    const currentLabel = chart.data.labels[active[0].index];
    const currentPrice = chart.data.datasets[0].data[active[0].index];
    let currentSection;

    if (this._data.quotes.timePeriod === "day") {
      chart.options.borderColor = rgb.slice(0, -1) + ",.25)";

      if (this._data.quotes.preDates.includes(currentLabel)) {
        currentSection = "preDates";
      } else if (this._data.quotes.intraDates.includes(currentLabel))
        currentSection = "intraDates";
      else currentSection = "postDates";

      chart.data.datasets[0].segment = {
        borderColor: (ctx) => {
          return this._data.quotes[`${currentSection}`].includes(
            chart.data.labels[ctx.p0DataIndex]
          )
            ? rgb
            : undefined;
        },
      };
    }

    chart.options.plugins.annotation.annotations.verticalLine = {
      type: "line",
      xMin: currentLabel,
      xMax: currentLabel,
      borderColor: "#b8b8b8",
      borderWidth: 2,
    };

    if (this._data.quotes.timePeriod === "day") {
      this._updatePrice(+currentPrice.toFixed(2), +this._data.closePrice);
    } else {
      this._updatePrice(
        +currentPrice.toFixed(2),
        +this._data.quotes.prices[0].close
      );
    }
    chart.update();
  }

  _onMouseOut(chart, args, options) {
    const event = args.event;
    if (event.type === "mouseout") {
      const rgb = this._generateRGB(
        this._data.quotes.timePeriod === "day"
          ? this._data.netChange
          : this._data.quotes.prices.slice(-1)[0].close -
              this._data.quotes.prices[0].close
      );
      chart.options.plugins.annotation.annotations.verticalLine = null;
      chart.data.datasets[0].segment = null;
      chart.options.borderColor = rgb;

      this._updatePrice(
        +this._data.lastPrice,
        +this._data.quotes.prices[0].close
      );
      chart.update();
    }
  }
}

export default new PortfolioChartView();

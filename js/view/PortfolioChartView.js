import { Chart, Tooltip } from "chart.js/auto";
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
            <p id="available-bal">${this._formatCurrency(
              this._user.availableBal || "0.00"
            )}</p>
         </div>
        </div>
        `
            : ""
        }
        `;
  }

  _generatePriceHTML() {
    const title = this._data.summary?.name
      ? this._data.summary.name.indexOf("Inc") != -1
        ? this._data.summary.name.slice(
            0,
            this._data.summary.name.indexOf("Inc")
          )
        : this._data.summary.name
      : -1;

    return `
    <div class="chart-info">
    ${
      title != -1
        ? `<h1>${title}</h1>`
        : `<h1>${
            this._data.symbol === "portfolio" ? "" : this._data.symbol || ""
          }</h1>`
    }
      <h1 class="ticker-price">${this._formatCurrency(
        +this._data.lastPrice
      )}</h1>
      <span class="${this._generateColor(
        +this._data.netChange
      )} ticker-change">${this._generateSymbol(+this._data.netChange)}${Number(
      this._data.netChange
    ).toFixed(2)} 
        
        <span>(${this._data.netPercentChangeInDouble.toFixed(2)}${
      this._data.netPercentChangeInDouble === 0 ? "" : "%"
    })</span>
      </span>
        
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

    const max = Math.max.apply(
      this,
      this._data.quotes.prices.map((a) => a.close)
    );

    const min = Math.min.apply(
      this,
      this._data.quotes.prices.map((a) => a.close)
    );

    this.myChart.data.labels = this._data.quotes.dates;

    this.myChart.data.datasets[0].data = this._data.quotes.prices.map(
      (a) => a.close
    );

    this.myChart.options.plugins.annotation.annotations.line1 = dailyLine;

    this.myChart.options.borderColor = rgb;

    this.myChart.options.scales.y.max =
      max - min !== 0 ? max + (max - min) / 5 : undefined;

    const recentLivePrice = this.myChart.data.datasets[0].data.slice(-1)[0];
    const recentPreviousPrice =
      this._data.symbol === "portfolio"
        ? this._data.closePrice
        : +recentLivePrice - +this._data.netChange;

    this._updatePrice(
      +recentLivePrice,
      this._data.quotes.timePeriod === "day"
        ? recentPreviousPrice
        : +this._data.quotes.prices[0].close
    );

    this.myChart.update();
  }

  _generateChart() {
    const mainChart = document.querySelector("#portfolio-chart");

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

    Tooltip.positioners.top = function (elements, eventPosition) {
      const {
        chartArea: { top },
        scales: { x },
      } = this.chart;

      const xIndex = x.getValueForPixel(eventPosition.x);

      const xIndexData = this.chart.data.datasets[0].data[xIndex];

      const dataIndex = this.chart.data.datasets[0].data.length;

      return {
        x: xIndexData
          ? x.getPixelForValue(xIndex)
          : x.getPixelForValue(dataIndex - 1),
        y: top,
      };
    };

    const min = Math.min.apply(
      null,
      this._data.quotes.prices.map((a) => a.close)
    );

    let max = Math.max.apply(
      null,
      this._data.quotes.prices.map((a) => a.close)
    );

    if (this._data.quotes.timePeriod === "day" && this._data.closePrice > max) {
      max = this._data.closePrice;
    }

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

        clip: false,

        layout: {
          padding: {
            top: 15,
            left: 10,
            right: 10,
          },
        },

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
            zindex: 1,
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
            enabled: true,
            backgroundColor: "#ffffff",
            displayColors: false,
            titleColor: "#939393",
            bodyColor: "rgba(0, 0, 0, 0)",
            xAlign: "center",
            yAlign: "center",
            titleFont: {
              weight: "lighter",
              size: "16",
            },
            position: "top",
            callbacks: {
              label: () => null,
            },
          },
        },

        scales: {
          y: {
            display: false,
            grid: {
              display: false,
            },
            max: max - min !== 0 ? max + (max - min) / 5 : undefined,
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

    // tickerPrice.innerHTML = "$" + currentPrice.toFixed(2);
    tickerPrice.innerHTML = this._formatCurrency(currentPrice);

    tickerNetChange.classList.replace(
      netChangeColor,
      this._generateColor(netChangePercent)
    );

    tickerNetChange.textContent = `${this._generateSymbol(
      netChange
    )}${netChange} (${netChangePercent}%)`;
    tickerDate.textContent = this._generateDate(this._data.quotes.timePeriod);
  }

  renderUserBalance(data) {
    const availableBal = this._parentElement.querySelector("#available-bal");
    availableBal.textContent = `${this._formatCurrency(data.availableBal)}`;
  }

  _onHover(e, active, chart) {
    if (active.length <= 0) return;

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
      borderColor: "#BFBFBF",
      borderWidth: 2,
      drawTime: "beforeDraw",
    };

    chart.options.elements.point.backgroundColor = rgb;

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
        this._data.quotes.timePeriod === "day"
          ? +this._data.lastPrice - +this._data.netChange
          : +this._data.quotes.prices[0].close
      );
      chart.update();
    }
  }
}

export default new PortfolioChartView();

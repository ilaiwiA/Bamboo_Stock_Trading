import View from "./View.js";
import defaultStockIMG from "url:./images/stock_img.jpg";

class NewsView extends View {
  _parentElement = document.querySelector(".main-container");

  constructor() {
    super();
    this.addHandlerTicker();
  }

  addHandlerTicker() {
    this._parentElement.addEventListener("click", function (e) {
      if (!e.target.closest(".news-tickers")) return;
      e.preventDefault();

      const id = e.target
        .closest(".news-tickers")
        ?.querySelector(".news-ticker").innerHTML;

      window.location.hash = `stocks/${id}`;
    });
  }

  _generateHTML() {
    return `
    <section>
    <h1>News</h1>
    ${this._data.news.map((val) => this._generateNewsPanel(val)).join("")}
    </section>
    `;
  }

  _generateNewsPanel(data) {
    return `
    <a class="news-panel sub-panel" href = "${data.url}" target="_blank">
    <div class="news-panel-main">
    <p class="news-publisher">
    ${data.source}
    <span class="news-publisher-time">${this._calcDate(
      data.time_published
    )}</span>
    </p>
    <h1 class="news-title">
    ${data.title}
    </h1>
    
    <p class="news-teaser">${data.summary}</p>

    ${
      this._data.symbol && data.symbol.length > 0
        ? `
    <div class="news-tickers">
      <span class="news-ticker">${data.symbol}</span>
      <span class="${this._generateColor(+data.netChange)}">${
            +data.netPercentChangeInDouble
              ? Number(+data.netPercentChangeInDouble).toFixed(2) + "%"
              : ""
          }</span>
    </div>
    `
        : ""
    }

      </div>
        <div class = "news-image">
            <img
            src="${data.banner_image || defaultStockIMG}"
            alt=""
            />
      </div>
  </a>  
  `;
  }

  _calcDate(data) {
    const today = new Date();
    const date = new Date(this._convertTime(data));

    if (
      today.getDay() === date.getDay() &&
      today.getMonth() === date.getMonth()
    ) {
      if (today.getHours() === date.getHours()) {
        const minutes = today.getMinutes() - date.getMinutes();
        return `${minutes}min`;
      }

      const hours = today.getHours() - date.getHours();
      return `${hours}h`;
    }

    const dayDiff = Math.abs(
      this._convertToDays(today) - this._convertToDays(date)
    );

    if (dayDiff < 31) return `${dayDiff}d`;

    return `${
      Math.trunc(dayDiff / 31) === 1
        ? `1 Month Ago`
        : `${Math.trunc(dayDiff / 31)} months ago`
    }`;
  }

  _convertTime(time) {
    const timeSplit = time.split("T");
    timeSplit[0] =
      timeSplit[0].slice(0, 4) +
      "-" +
      timeSplit[0].slice(4, 6) +
      "-" +
      timeSplit[0].slice(6);
    timeSplit[1] =
      timeSplit[1].slice(0, 2) +
      ":" +
      timeSplit[1].slice(2, 4) +
      ":" +
      timeSplit[1].slice(4) +
      "Z";
    return timeSplit.join("T");
  }

  _convertToDays(date) {
    return (
      (Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()) -
        Date.UTC(date.getFullYear(), 0, 0)) /
      24 /
      60 /
      60 /
      1000
    );
  }
}

export default new NewsView();

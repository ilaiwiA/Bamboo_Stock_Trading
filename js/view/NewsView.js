import View from "./View.js";

class NewsView extends View {
  _parentElement = document.querySelector(".main-container");

  _generateHTML() {
    return `
    ${this._data.news.map((val) => this._generateNewsPanel(val)).join("")}
    `;
  }

  _generateNewsPanel(data) {
    console.log(data.banner_image);
    return `
    <a class="news-panel" href = "${data.url}" target="_blank">
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
            
      <div class="news-tickers">
        <span class="news-ticker">${this._data.symbol}</span>
        <span class="${this._generateColor(+this._data.netChange)}">${+this
      ._data.netChange}%</span>
      </div>
            </div>
            <div class = "news-image">
            <img
            src="${
              data.banner_image ? data.banner_image : "images/stock_img.jpg"
            }"
            alt=""
            />
            </div>
            </a>
            
            <hr />
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
        return `${minutes} min ago`;
      }

      const hours = today.getHours() - date.getHours();
      return hours > 1 ? `${hours} hours ago` : `${hours} hour ago`;
    }

    const dayDiff = Math.abs(
      this._convertToDays(today) - this._convertToDays(date)
    );
    // today.getDate() - date.getDate();

    if (dayDiff === 1) return `${dayDiff} day ago`;

    if (dayDiff < 31) return `${dayDiff} days ago`;

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

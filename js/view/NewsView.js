import View from "./View.js";

class NewsView extends View {
  _parentElement = document.querySelector(".main-container");

  _generateHTML() {
    return `
    ${this._data.map((val) => this._generateNewsPanel(val)).join("")}
    `;
  }

  _generateNewsPanel(data) {
    return `
    <a class="news-panel" href = "${data.url}" target="_blank">
    <div class="news-panel-main">
    <p class="news-publisher">
    ${data.source[0].toUpperCase() + data.source.slice(1, -4)}
    <span class="news-publisher-time">${this._calcDate(
      data.published_at
    )}</span>
    </p>
    <h1 class="news-title">
    ${data.title}
    </h1>
    
    <p class="news-teaser">${data.description}</p>
            
      <div class="news-tickers">
        <span class="news-ticker">${data.ticker}</span>
        <span>^2.66%</span>
      </div>
            </div>
            <div class = "news-image">
            <img
            src="${data.image_url}"
            alt=""
            />
            </div>
            </a>
            
            <hr />
  `;
  }
  _calcDate(data) {
    const today = new Date();
    const date = new Date(data);

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

    const dayDiff = today.getDate() - date.getDate();

    if (dayDiff < 7)
      return `${dayDiff} ${dayDiff === 1 ? "day ago" : "days ago"}`;
  }
}

export default new NewsView();

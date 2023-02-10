import View from "./View.js";

class NewsView extends View {
  _parentElement = document.querySelector(".news-container");

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
                ${data.publisher}
                <span class="news-publisher-time">${data.time}</span>
            </p>
            <h1 class="news-title">
            ${data.title}
            </h1>

            <p class="news-teaser">
            ${data.teaser}
            </p>

            <div class="news-tickers">
                <span class="news-ticker">${data.ticker}</span>
                <span>^2.66%</span>
            </div>
            </div>

            <img
            src="${data.image_url}"
            alt=""
            />
      </a>

      <hr />
        `;
  }
}

export default new NewsView();

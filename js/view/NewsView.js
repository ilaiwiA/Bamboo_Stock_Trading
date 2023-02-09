import View from "./View";

class NewsView extends View {
  _generateHTML() {
    return `
        <div class="news-panel">
            <div class="news-panel-main">
              <p class="news-publisher">
                CNBC
                <span class="news-publisher-time">4d</span>
              </p>
              <h1 class="news-title">
                Elon Musk says Twitter is trending to break even after he saved
                it from bankruptcy: ‘Last 3 months were extremely tough
              </h1>

              <p class="news-teaser">
                Twitter still has challenges, Musk said Sunday, but it looks set
                to avoid the bankruptcy he had warned about.
              </p>

              <div class="news-tickers">
                <span class="news-ticker">TSLA</span>
                <span>^ 2.66%</span>
              </div>
            </div>

            <img
              src="https://content.fortune.com/wp-content/uploads/2023/02/GettyImages-1246507198-e1675632304333.jpg?resize=1200,600"
              alt=""
            />
        </div>
        <hr />
        `;
  }

  _generateNewsPanel(data) {
    return `
        <div class="news-panel">
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
            src="${image_url}"
            alt=""
            />
      </div>

      <hr />
        `;
  }
}

export default new NewsView();

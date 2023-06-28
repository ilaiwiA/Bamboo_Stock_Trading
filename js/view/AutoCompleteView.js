import { tickers } from "../config.js";
import View from "./View.js";

class AutoCompleteView extends View {
  mainPage = document.querySelector("body");
  searchBar = document.querySelector("#search_input");
  resultsHTML = document.querySelector("#results");

  constructor() {
    super();
    this.addSearchHandler();
    this.addResultsHandler();
    this.addPageHandler();
  }

  addSearchHandler() {
    ["input"].forEach((action) =>
      this.searchBar.addEventListener(action, this.autoComplete.bind(this))
    );
  }

  addResultsHandler() {
    this.resultsHTML.addEventListener("click", this.setResultsHTML.bind(this));
  }

  addPageHandler() {
    this.mainPage.addEventListener("click", this.hideResults.bind(this));
  }

  async autoComplete(e) {
    let results = [];
    const userInput = this.searchBar.value;

    this.resultsHTML.innerHTML = "";
    this.resultsHTML.classList.add("hidden");

    if (!userInput) return;

    if (userInput.length > 1) {
      results = await this.getSearchResults(userInput.toUpperCase());

      this.resultsHTML.classList.remove("hidden");

      if (!results) return;
      this.searchBar.style.borderBottom = "none";

      if (results.length === 0) {
        this.resultsHTML.innerHTML =
          "<li>We were unable to find any results for your search.</li>";
        return;
      }

      const resultsLength = results.length >= 10 ? 10 : results.length;

      for (let i = 0; i < resultsLength; i++) {
        this.resultsHTML.innerHTML += `<li class='auto-ticker' id='${results[i][0]}'><p>${results[i][1]}</p> <p>${results[i][0]}</p></li>`;
      }
    }
  }

  async getSearchResults(userInput) {
    const results = [];

    const data = await tickers;

    let tickerBool = false;

    for (let i = 0; i < data.length; i++) {
      if (userInput === data[i][1]?.slice(0, userInput.length).toUpperCase()) {
        results.push(data[i]);
        tickerBool = true;
      }
    }
    return results;
  }

  setResultsHTML(e) {
    window.location.hash = "#stocks/" + e.target.closest(".auto-ticker")?.id;
    this.resultsHTML.innerHTML = "";
    this.searchBar.value = "";
  }

  hideResults(e) {
    if (e.target !== this.searchBar) {
      this.resultsHTML.innerHTML = "";
      this.resultsHTML.classList.add("hidden");
      this.searchBar.style.borderBottom = "1px solid black";
      this.searchBar.value = "";
    }
  }
}

export default new AutoCompleteView();

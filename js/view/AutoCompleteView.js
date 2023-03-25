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
    ["input", "click"].forEach((action) =>
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

      if (results.length === 0) {
        this.resultsHTML.innerHTML =
          "<li>We were unable to find any results for your search.</li>";
        return;
      }

      const resultsLength = results.length >= 10 ? 10 : results.length;

      for (let i = 0; i < resultsLength; i++) {
        this.resultsHTML.innerHTML += `<li class='ticker'>${results[i]}</li>`;
      }
    }
  }

  async getSearchResults(userInput) {
    const results = [];

    const data = await tickers;

    let tickerBool = false;

    for (let i = 0; i < data.length; i++) {
      if (userInput === data[i].slice(0, userInput.length)) {
        results.push(data[i]);
        tickerBool = true;
      }

      if (userInput !== data[i].slice(0, userInput.length) && tickerBool)
        return results;
    }
    return results;
  }

  setResultsHTML(e) {
    window.location.hash = "#stocks/" + e.target.closest(".ticker")?.innerHTML;
    this.resultsHTML.innerHTML = "";
    this.searchBar.value = "";
  }

  hideResults(e) {
    if (e.target !== this.searchBar) {
      this.resultsHTML.innerHTML = "";
      this.resultsHTML.classList.add("hidden");
    }
  }
}

export default new AutoCompleteView();

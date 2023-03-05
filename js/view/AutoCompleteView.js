import View from "./View.js";

class AutoCompleteView extends View {
  searchBar = document.querySelector("#search_input");
  resultsHTML = document.querySelector("#results");

  constructor() {
    super();
    this.addSearchHandler();
  }

  addSearchHandler() {
    this.searchBar.addEventListener("input", this.autoComplete.bind(this));
  }

  autoComplete(e) {
    this.resultsHTML.innerHTML += `<li> ${e.data} </li>`;
  }
}

export default new AutoCompleteView();

import View from "./View.js";

class HeaderListView extends View {
  _parentElement = document.querySelector("header");

  constructor() {
    super();
    this.addHandlerNav();
  }

  addHandlerNav(handler) {
    this._parentElement.addEventListener("click", function (e) {
      if (e.target.id === "portfolio") window.location.href = "#";

      if (e.target.id === "logout") handler();
    });
  }

  addHandlerLogout(handler) {
    this._parentElement.addEventListener("click", function (e) {
      if (e.target.id === "logout") handler();
    });
  }
}

export default new HeaderListView();

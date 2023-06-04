import View from "./View.js";

class HeaderListView extends View {
  _parentElement = document.querySelector("header");

  constructor() {
    super();
    console.log(this._parentElement);
    this.addHandlerNav();
  }

  addHandlerNav(handler) {
    this._parentElement.addEventListener("click", function (e) {
      console.log(e.target);

      if (e.target.id === "portfolio") window.location.href = "#";

      if (e.target.id === "account") console.log("account");

      if (e.target.id === "logout") handler();
    });
  }
}

export default new HeaderListView();

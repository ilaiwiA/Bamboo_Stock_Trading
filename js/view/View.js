import { USER_STOCK, WATCH_LIST } from "../config";

export default class View {
  render(data) {
    if (!data) return;

    this._user = data;
    this._currentStock = data?.userStocks.find(
      (a) => a.ticker === data.stock.symbol
    );
    this._data = data.stock;

    const html = this._generateHTML();

    this._parentElement.insertAdjacentHTML("beforeend", html);
    this._generateChart?.();
  }

  _clear() {
    this._parentElement.innerHTML = "";
  }

  clear() {
    this._parentElement.innerHTML = "";
  }

  renderLoad() {
    if (!this._parentElement.querySelector(".loading")) this.clear();

    const html = `
    <div class="loading">
    </div>
    `;

    this._parentElement.insertAdjacentHTML("afterbegin", html);
  }

  _generatePanelTypes() {
    return [
      this._user.stocklist ? USER_STOCK : undefined,
      this._user.watchlist ? WATCH_LIST : undefined,
    ].filter((a) => a);
  }

  _generateColor(data) {
    if (data > 0) return "positive_green";
    else if (data < 0) return "negative_red";

    return "NEUTRAL";
  }

  _generateSymbol(data) {
    if (data > 0) return "+";
    else if (data < 0) return "";

    return "";
  }

  _generateRGB(netChange) {
    return this._generateColor(+netChange) === "positive_green"
      ? "rgb(0,200,0)"
      : "rgb(253,82,64)";
  }

  _formatCurrency(val) {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
      signDisplay: "auto",
    }).format(val);
  }
}

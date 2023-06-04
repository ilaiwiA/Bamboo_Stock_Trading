import { DISPLAY_PRICE_CONFIG, RECENT_PRICE_CONFIG } from "../config.js";
import View from "./View.js";

class UserStockDetailsView extends View {
  _parentElement = document.querySelector(".main-container");

  _generateHTML() {
    if (!this._currentStock) return "";
    return `
    <section>
        <div class="sub-panel-stocks">

        ${this._generateEquityPanel()}

        ${this._generateCostPanel()}

        </div>
    </section>
    `;
  }

  _generateEquityPanel() {
    return `
    <div class="user-stock-panel equity-panel">
        <div class="user-stock-info">
            <h4>Your Equity</h4>
            <h1>${this._formatCurrency(
              this._data.lastPrice * this._currentStock.quantity
            )}</h1>
        </div>
        <div class="user-stock-stats">
            <p>Today's Return</p>
            <p>${this._generateTodayReturnHTML()}</p>
        </div>
        <hr />
        <div class="user-stock-stats">
            <p>Total Return</p>
            <p>${this._generateTotalReturnHTML()}</p>
        </div>
    </div>
    `;
  }

  _generateCostPanel() {
    return `
    <div class="user-stock-panel cost-panel">
        <div class="user-stock-info">
            <h4>Your average cost</h4>
            <h1>${this._formatCurrency(this._currentStock.avgPrice)}</h1>
        </div>

        <div class="user-stock-stats">
            <p>Date purchased</p>
            <p>${new Intl.DateTimeFormat("en-US", DISPLAY_PRICE_CONFIG).format(
              this._currentStock.date
            )}</p>
        </div>
        <hr/>

        <div class="user-stock-stats">
            <p>Quantity</p>
            <p>${this._currentStock.quantity}</p>
        </div>
    </div>
    `;
  }

  _generateTotalReturnHTML() {
    const returnDollars =
      (this._data.lastPrice - this._currentStock.avgPrice) *
      this._currentStock.quantity;

    const totalReturnDollars = this._formatCurrency(returnDollars);

    const totalReturnPercentage =
      ((this._data.lastPrice - this._currentStock.avgPrice) /
        this._data.lastPrice) *
      100;

    return `${this._generateSymbol(
      returnDollars
    )}${totalReturnDollars} (${totalReturnPercentage.toFixed(2)}%)`;
  }

  _generateTodayReturnHTML() {
    console.log(this._data.closePrice);
    console.log(this._data.netChange);

    const returnDollars = this._currentStock.quantity * this._data.netChange;
    const todayReturnDollars = this._formatCurrency(returnDollars);
    const todayReturnPercentage = this._data.netPercentChangeInDouble;

    if (
      new Date().getFullYear() ===
        new Date(this._currentStock.date).getFullYear() &&
      new Date().getMonth() === new Date(this._currentStock.date).getMonth() &&
      new Date().getDate() === new Date(this._currentStock.date).getDate()
    )
      return this._generateTotalReturnHTML();

    return `${this._generateSymbol(
      returnDollars
    )}${todayReturnDollars} (${todayReturnPercentage.toFixed(2)}%)`;
  }
}

export default new UserStockDetailsView();

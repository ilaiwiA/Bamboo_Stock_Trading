import View from "./View.js";

class PurchaseView extends View {
  _parentElement = document.querySelector(".aside-container");

  constructor() {
    super();
  }

  _generateHTML() {
    return `
    <div class="purchase-container side-container">
          <div class="purchase-panel panel">
            <h2>Buy ${this._data.symbol}</h2>
            <hr />
            <form action="" class="purchase-form">
              <section>
                <p>Order Type</p>
                <p>Market Order</p>
              </section>

              <section>
                <label for="order-type">Buy in</label>
                <select name="order-type" id="order-type">
                  <option value="shares">Shares</option>
                  <option value="dollars">Dollars</option>
                </select>
              </section>

              <section>
                <label for="order-shares">Shares</label>
                <input type="number" min="0" id="order-shares" placeholder = '0'/>
              </section>

              <section class="current-market">
                <p>Market Price</p>
                <p id="market-price">$${Number(this._data.lastPrice).toFixed(
                  2
                )}</p>
              </section>
              <hr />

              <section class="currnet-estimated">
                <p>Estimated Cost</p>
                <p id="estimated-price">$0.00</p>
              </section>

              <input type="submit" id="btn-submit" value="Review" />
            </form>
            <hr />

            <p class="user-available">$3123<span> available</span></p>
          </div>
        </div>
    `;
  }
}

export default new PurchaseView();

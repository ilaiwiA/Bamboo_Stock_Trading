import View from "./View.js";

class PurchaseView extends View {
  _parentElement = document.querySelector(".aside-container");

  constructor() {
    super();
  }

  addHandlerInput(handler) {
    this._parentElement.addEventListener("input", function (e) {
      console.log(this, e.target);
      e.preventDefault();
      if (e.target.id === "order-type") return handler(e.target.value);

      const val = e.target.value;

      if (e.target.id === "order-Shares") {
        const html = this.querySelector("#estimated-price");
        console.log(val);
        console.log(+this.querySelector("#market-price").innerHTML.slice(1));
        html.innerHTML =
          "$" +
          Number(
            val * +this.querySelector("#market-price").innerHTML.slice(1)
          ).toFixed(2);
      } else if (e.target.id === "order-Dollars") {
      }

      handler(e.target.id.split("-")[1], e.target.value);
    });
  }

  addHandlerPurchaseCost(handler) {
    this._parentElement.querySelector(".");
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
                  <option value="Shares">Shares</option>
                  <option value="Dollars">Dollars</option>
                </select>
              </section>

              <section>
                <label for="order-${this._generatePurchaseType(
                  this._data.purchaseType
                )}">${this._generatePurchaseType(
      this._data.purchaseType
    )}</label>
                <input type="number" min="0" class= "order-value" id="order-${this._generatePurchaseType(
                  this._data.purchaseType
                )}" placeholder = '0'/>
              </section>
              
              <section class="current-market">
                <p>Market Price</p>
                <p id="market-price">$${Number(this._data.lastPrice).toFixed(
                  2
                )}</p>
              </section>
              <hr />

              <section class="current-estimated">
                <p>Estimated Cost</p>
                <p id="estimated-price">$${
                  this._data.estimatedCost ? this._data.estimatedCost : "0.00"
                }</p>
              </section>

              <input type="submit" id="btn-submit" value="Review" />
            </form>
            <hr />

            <p class="user-available">$3123<span> available</span></p>
          </div>
        </div>
    `;
  }

  _generateCost(quantityShares) {
    return Number(this._data.lastPrice) * quantityShares;
  }
  _generateQuantity(quantityMoney) {
    return quantityMoney / Number(this._data.lastPrice);
  }

  _generatePurchaseType(data) {
    if (data) return data;

    return "Shares";
  }
}

export default new PurchaseView();

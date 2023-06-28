import View from "./View.js";

import checkMarkIMG from "url:/images/checkmark.svg";
import plusSignIMG from "url:/images/plus-sign.svg";

class PurchaseView extends View {
  _parentElement = document.querySelector(".aside-container");

  constructor() {
    super();

    this.addHandlerReview();
  }

  addHandlerTransactionType() {}

  addHandlerReview() {
    this._parentElement.addEventListener(
      "submit",
      this.reviewPurchase.bind(this)
    );
  }

  addHandlerPurchaseForm(handler) {
    const a = this;

    this._parentElement.addEventListener("click", function (e) {
      if (e.target.value?.toLowerCase() === "cancel") {
        const panel = e.target.closest(".purchase-panel");
        panel.style.height = `550px`;
        handler();
      }

      if (e.target.closest(".purchase-panel-header")?.children.length > 1) {
        const id = e.target.id;

        e.target.style = `border-bottom: 1px solid var(--${a._generateColor(
          a._data.netChange
        )})`;

        e.target.classList.toggle("active");

        const siblingElement =
          id === "buy"
            ? e.target.nextElementSibling
            : e.target.previousElementSibling;

        if (!siblingElement) return;

        siblingElement.style = "";
        siblingElement.classList.toggle("active");

        a._data.tradeType = id;

        a._updateAvailableBal();
      }
    });
  }

  addHandlerSubmit(handler) {
    this._submitHandler = handler;
  }

  reviewPurchase(e) {
    e.preventDefault();

    const orderType = e.target
      .closest(".purchase-panel")
      .querySelector(".active").id;
    const button = e.target.querySelector(".btn-submit");

    const buttonContainer = e.target.querySelector(".form-buttons");

    if (!button) return;

    const type = this._parentElement.querySelector(".active").id;
    const orderBuyIn = e.target.querySelector(".order-value").id.slice(6);
    const orderValue = +e.target.querySelector(`#order-${orderBuyIn}`).value;
    const orderEstCost = e.target
      .querySelector("#estimated-price")
      .innerHTML.slice(1)
      .replace(",", "");

    if (button.value.toLowerCase() === "submit") {
      return this._submitHandler(
        orderBuyIn,
        orderValue,
        this._data.symbol,
        orderType
      );
    }

    const parameterSuccess = [
      buttonContainer,
      this._generateReviewMessageSuccess(
        orderBuyIn,
        orderValue,
        this._data.lastPrice,
        type
      ),
      this._generateCancelButton(this._data.netChange),
    ];

    const parameterFailure = [
      buttonContainer,
      this._generateReviewMessageFailure(
        orderBuyIn,
        orderBuyIn === "Share" ? orderEstCost : orderValue
      ),
      this._generateCancelButton(this._data.netChange),
      false,
    ];

    console.log(parameterSuccess, parameterFailure);

    //orderValue <= 0 || !this._user.availableBal

    if (orderType === "buy") {
      this._checkValidPurchase(orderBuyIn, orderValue)
        ? this.renderPurchaseReview.call(...parameterSuccess)
        : this.renderPurchaseReview.call(...parameterFailure);
    } else if (orderType === "sell") {
      this._checkValidSell(orderBuyIn, orderValue)
        ? this.renderPurchaseReview.call(...parameterSuccess)
        : this.renderPurchaseReview.call(...parameterFailure);
    }
  }

  addHandlerInput(handler) {
    this._parentElement.addEventListener(
      "input",
      this.updatePurchasePanel.bind(handler)
    );
  }

  renderPurchaseReview(html, buttonHtml, success = true) {
    console.log(buttonHtml);
    console.log(this);
    const panel = this.closest(".purchase-panel");
    const availableText = panel.querySelector(".current-available");

    const text = availableText.outerHTML;
    availableText.remove();

    this.classList.add("animateReview");

    this.insertAdjacentHTML("beforebegin", html);
    this.insertAdjacentHTML("beforeend", buttonHtml);
    this.insertAdjacentHTML("beforeend", text);
    success
      ? (this.querySelector(".btn-submit").value = "Submit")
      : this.querySelector(".btn-submit").remove();

    let panelHeight = panel.offsetHeight + this.offsetHeight;
    if (success) panelHeight += 20;
    console.log(panelHeight);

    panel.style.height = `${panelHeight}px`;
    availableText.style.maxHeight = "800px";
  }

  addHandlerWatchlist(handler) {
    this._parentElement.addEventListener("click", function (e) {
      const btn_watchlist = e.target.closest("#btn_watchlist");

      if (btn_watchlist) {
        const id = this.querySelector(".purchase-container").id;

        handler(id);
      }
    });
  }

  updatePurchasePanel(e) {
    e.preventDefault();
    if (e.target.id === "order-type") {
      const purchaseID = e.target
        .closest(".purchase-panel")
        .querySelector(".active").id;

      return this(e.target.value, purchaseID);
    }

    const parent = e.target.closest(".aside-container");

    const html = parent.querySelector("#estimated-price");
    const val = e.target.value;

    if (e.target.id === "order-Shares") {
      const cost = Number(
        val * +parent.querySelector("#market-price").innerHTML.slice(1)
      ).toFixed(2);

      html.innerHTML =
        "$" +
        Intl.NumberFormat("en-US", {
          minimumFractionDigits: 2,
        }).format(cost);
    } else if (e.target.id === "order-Dollars") {
      const cost =
        val / +parent.querySelector("#market-price").innerHTML.slice(1);

      html.innerHTML = cost - cost.toFixed(6) === 0 ? cost : cost.toFixed(6);
    }
  }

  _updateAvailableBal() {
    const userBalance = this._parentElement.querySelector(".user-available");

    userBalance.innerHTML = `${this._generateAvailableBal()}`;
  }

  updateWatchlistIMG() {
    const button = this._parentElement.querySelector("#btn_watchlist");
    const img = button.querySelector("img");

    if (img.src.includes("plus")) img.src = checkMarkIMG;
    else img.src = plusSignIMG;
  }

  _generateHTML() {
    return `
    <div class="purchase-container side-container" id = '${this._data.symbol}'>
          <div class="purchase-panel panel">
          ${this._generateHeaderHTML()}
            <hr />
            <form action="" class="purchase-form">
              <section>
                <p>Order Type</p>
                <p>Market Order</p>
              </section>

              <section>
              ${this._generatePurchaseLabelHTML()}
              </section>

              <section>
                <label for="order-${this._generatePurchaseType()}">${this._generatePurchaseType()}</label>
                <input type="number" min="0" class= "order-value" step="0.01" id="order-${this._generatePurchaseType(
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
                <p>${
                  this._data.purchaseType === "Dollars"
                    ? "Est. Quantity"
                    : "Est. Price"
                }</p>
                <p id="estimated-price">${
                  this._data.purchaseType === "Dollars" ? "" : "$"
                }0.00
                </p>
              </section>


              <div class="form-buttons">
                <input type="submit" class="btn-submit btn-alternative" value="Review" style="background-color: var(--${this._generateColor(
                  `${this._data.netChange}`
                )}" />
              </div>
            </form>

            ${this._generateBalanceHTML()}

          </div>
          ${this._generateSubLabelHTML()}
        </div>
    `;
  }

  _generateHeaderHTML() {
    const activeClass = `class = "active" style= "border-bottom: 1px solid var(--${this._generateColor(
      this._data.netChange
    )})"`;

    return `
    <section class="purchase-panel-header">
    <h2 id="buy" ${
      !this._data.tradeType ||
      !this._currentStock ||
      this._data.tradeType === "buy"
        ? activeClass
        : ""
    }>Buy ${this._data.symbol}</h2>
    ${
      this._currentStock
        ? `<h2 id="sell" ${
            this._data.tradeType === "sell" ? activeClass : ""
          }>Sell ${this._data.symbol}</h2>`
        : ""
    }
  </section>
    `;
  }

  _generatePurchaseLabelHTML() {
    const data = this._data.purchaseType ? this._data.purchaseType : "Shares";

    return `
    <label for="order-type">Buy in</label>
    <select name="order-type" id="order-type">
      <option value="Shares" ${
        data === "Shares" ? "selected" : ""
      }>Shares</option>
      <option value="Dollars" ${
        data === "Dollars" ? "selected" : ""
      }>Dollars</option>
    </select>
    `;
  }

  _generateBalanceHTML() {
    return `
    <section class="current-available">
      <p class="user-available">${this._generateAvailableBal()}</p>
    </section>
    `;
  }

  _generateSubLabelHTML() {
    return `
    <button class="btn-alternative ${this._generateColor(
      this._data.netChange
    )}_button btn-subPanel" id= 'btn_watchlist'>
    <img
      class="${this._generateColor(this._data.netChange)}_symbol"
      src="${this._data.bookmarked ? checkMarkIMG : plusSignIMG}"
      alt=""
    />
    Add to Watchlist
    </button>
    `;
  }

  _generateAvailableBal() {
    if (
      !this._currentStock ||
      !this._data.tradeType ||
      this._data.tradeType === "buy"
    )
      return `${this._formatCurrency(+this._user.availableBal)} available`;

    if (!this._data.purchaseType || this._data.purchaseType === "Shares")
      return `${this._currentStock.quantity} ${this._currentStock.ticker} available`;
    else
      return `${this._formatCurrency(
        +this._currentStock.quantity * +this._data.lastPrice
      )} available`;
  }

  _generateCost(quantityShares) {
    return Number(this._data.lastPrice) * quantityShares;
  }
  _generateQuantity(quantityMoney) {
    return quantityMoney / Number(this._data.lastPrice);
  }

  _generatePurchaseType() {
    return this._data.purchaseType ? this._data.purchaseType : "Shares";
  }

  _generateReviewMessageSuccess(orderBuyIn, quantity, price, type = "buy") {
    return `
    <section class="order-summary">
    <h3>Order Summary </h3>
    ${
      orderBuyIn === "Shares"
        ? `You are placing a market order to ${type} ${quantity} ${
            quantity === 1 ? "share" : "shares"
          } of ${
            this._data.symbol
          }. Your pending order will execute at ${price} per share or better.`
        : `You are placing a market order to ${type} ${quantity} ${
            quantity === 1 ? "dollar" : "dollars"
          } of ${this._data.symbol}. 
    Your pending order will execute at the best market price.`
    }</section>`;
  }

  _generateReviewMessageFailure(orderBuyIn, failureRes) {
    return `
    <section class="order-summary">
    <h3>${
      failureRes <= 0 ? "Order not valid." : "Not enough buying power"
    } </h3>

    ${
      failureRes <= 0
        ? `Your order must be greater than 0 ${
            orderBuyIn === "Shares" ? "shares" : "dollars"
          }.`
        : "You do not have enough buying power in your account to place this order."
    }
    </section>
    `;
  }

  _generateCancelButton(newChange) {
    return `
    <input type="button" 
    class="btn-alternative" 
    value="Cancel" 
    style="color: var(--${this._generateColor(newChange)}); 
    border: 1px solid var(--${this._generateColor(newChange)})"
    />`;
  }

  _checkValidPurchase(type, orderValue) {
    if (type === "Shares") {
      return (
        orderValue * this._data.lastPrice <= this._user.availableBal &&
        orderValue > 0
      );
    } else if (type === "Dollars") {
      return orderValue <= this._user.availableBal;
    }
  }

  _checkValidSell(type, orderValue) {
    const ticker = this._user.userStocks.find(
      (a) => a.ticker === this._data.symbol
    );

    if (type === "Shares") {
      return orderValue <= ticker.quantity;
    } else if (type === "Dollars") {
      return orderValue / this._data.lastPrice <= ticker.quantity;
    }
  }
}

export default new PurchaseView();

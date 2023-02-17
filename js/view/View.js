export default class View {
  render(data) {
    this._data = data;

    const html = this._generateHTML();

    this._parentElement.insertAdjacentHTML("beforeend", html);
  }

  _clear() {
    this._parentElement.innerHTML = "";
  }

  clear() {
    this._parentElement.innerHTML = "";
  }

  renderLoad() {
    const html = `
    <div class="loading">
    </div>
    `;

    this._parentElement.insertAdjacentHTML("afterbegin", html);
  }

  _generateColor(data) {
    if (data > 0) return "positive";
    else if (data < 0) return "negative";

    return "NEUTRAL";
  }

  _generateSymbol(data) {
    if (data > 0) return "+";
    else if (data < 0) return "";

    return "";
  }
}

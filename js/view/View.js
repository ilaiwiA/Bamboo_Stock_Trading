export default class View {
  render(data) {
    this._data = data;

    const html = this._generateHTML();

    // this._clear(); // turn back on
    this._parentElement.insertAdjacentHTML("afterbegin", html);
  }

  _clear() {
    this._parentElement.innerHTML = "";
  }
}

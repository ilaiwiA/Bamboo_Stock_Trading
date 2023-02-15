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

  update() {}
}

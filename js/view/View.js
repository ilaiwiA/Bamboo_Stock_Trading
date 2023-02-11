export default class View {
  render(data) {
    this._data = data;
    console.log(data);

    const html = this._generateHTML();

    // this._clear(); // turn back on
    this._parentElement.insertAdjacentHTML("beforeend", html);
  }

  _clear() {
    this._parentElement.innerHTML = "";
  }
}

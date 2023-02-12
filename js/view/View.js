export default class View {
  render(data) {
    this._data = data;

    const html = this._generateHTML();
    console.log(html);

    this._clear(); // turn back on
    this._parentElement.insertAdjacentHTML("beforeend", html);
    console.log(this._parentElement);
  }

  _clear() {
    this._parentElement.innerHTML = "";
  }
}

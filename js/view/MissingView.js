import View from "./View.js";

class MissingView extends View {
  _parentElement = document.querySelector("body");

  addHandlerHomeButton(handler) {
    this._parentElement.addEventListener("click", function (e) {
      if (e.target.closest(".home-btn")) handler();
    });
  }

  _generateHTML() {
    return `
        <div class="missing-page">
        <div class="missing-text">
          <section>
            <h1>
              404<br />
              Page Not Found
            </h1>
          </section>
          <p>Can't seem to find that page! Click to return home.</p>
          <button class="home-btn">Home</button>
        </div>
        <div class="missing-img">
          <img src="images/404-error.png" alt="" />
        </div>
      </div>
        `;
  }

  renderStart() {
    const html = `
    <header>
      <a href="#">
        <img src="images/bamboo_logo.png" alt="main-logo-home" />
      </a>
      <form action="">
        <label for="search_input"></label>
        <input type="text" id="search_input" placeholder="Search" />
      </form>

      <nav>
        <ul>
          <li>Portfolio</li>
          <li>About</li>
          <li>Logout</li>
        </ul>
      </nav>
    </header>

    <main class="app-container">
      <div class="main-container">
      </div>

      <aside class="aside-container">
      </aside>
    </main>
    `;
    this.clear();
    window.location.hash = "#";
    this._parentElement.insertAdjacentHTML("afterBegin", html);
  }
}

export default new MissingView();

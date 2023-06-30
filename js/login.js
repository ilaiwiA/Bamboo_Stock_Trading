import { URL_AUTH } from "./config";

const form = document.querySelector("#login-form");
const button = document.querySelector("#button-submit");
const failure = document.querySelector("#login-failure");

window.addEventListener("load", function () {
  this.document.querySelector(".waitLoad").classList.add("hidden");
});

form.addEventListener("submit", function (e) {
  e.preventDefault();
  button.innerHTML = `<div class="loader-button"></div>`;

  const data = new FormData(form);

  const credentials = {
    username: data.get("formName"),
    password: data.get("formPass"),
  };

  loginFetch(credentials);
});

const loginFetch = async function (auth) {
  let header = new Headers();
  header.append("Content-Type", "application/json");

  let request = new Request(URL_AUTH + "login", {
    method: "POST",
    headers: header,
    body: JSON.stringify(auth),
    credentials: "include",
  });

  const response = await fetch(request);

  if (!response.ok) return loginFailure();

  window.location.href = "/";
};

const loginFailure = function () {
  failure.classList.remove("hidden");

  button.innerHTML = `<p>Log in</p>`;
};

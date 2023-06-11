import { URL_LOGIN } from "./config";

const form = document.querySelector("#login-form");
const button = document.querySelector("#button-submit");

window.addEventListener("load", function () {
  console.log("called");
  this.document.querySelector(".waitLoad").classList.add("hidden");
});

form.addEventListener("submit", function (e) {
  e.preventDefault();
  console.log(button.innerHTML);
  button.innerHTML = `<div class="loader-button"></div>`;

  const data = new FormData(form);

  console.log("submitted");
});

const loginFetch = async function () {
  let header = new Headers();
  header.append("Accept", "application/json");
  header.append("Authroization");

  let request = new Request(URL_LOGIN, {
    method: "GET",
    headers: header,
    credentials: "include",
  });

  const response = fetch(request);

  console.log(response);
};

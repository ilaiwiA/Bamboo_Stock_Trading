import { URL_AUTH } from "./config";

import ERROR_ICON from "url:/images/error-icon.png";
import SUCCESS_ICON from "url:/images/successIconLarge.png";

const main = document.querySelector("main");
const register = document.querySelector(".login-form");
const form = document.querySelector("#register-form");
const button = document.querySelector("#button-submit");
const labels = document.querySelectorAll("label");

window.addEventListener("load", function () {
  this.document.querySelector(".waitLoad").classList.add("hidden");
});

form.addEventListener("submit", function (e) {
  e.preventDefault();

  button.innerHTML = `<div class="loader-button"></div>`;
  registrationFieldReset();
  const data = new FormData(form);

  const userRegistration = {};

  data.forEach((val, key) => {
    if (key === "formStartBal") val = +val;
    userRegistration[key] = val;
  });

  registrationFetch(userRegistration);
});

const registrationFetch = async function (data) {
  let header = new Headers();
  header.append("Content-Type", "application/json");

  let request = new Request(URL_AUTH + "register", {
    method: "POST",
    headers: header,
    body: JSON.stringify(data),
  });

  const response = await fetch(request);

  if (!response.ok) return registrationFailure(await response.json());

  registrationSuccess();
};

const registrationFailure = function (err) {
  err.details.forEach((val) => {
    const index = val.indexOf(":");
    const label =
      val.slice(0, index) === "validatePassword" ||
      val.slice(0, index) === "passwordMatch"
        ? "password"
        : val.slice(0, index);
    document.querySelector(`#${label}`).innerHTML = generateErrMsg(
      val.slice(index + 1)
    );
  });
  button.innerHTML = `<p>Log in</p>`;
};

const registrationSuccess = function () {
  register.classList.add("hidden");

  const html = `
  <section class="register-success main-form">
    <img
        src="${SUCCESS_ICON}"
        alt="account_creation_success"
    />
    <div class="success-message">
        <h1>Account successfully created!</h1>
        <p id="redirect-msg">
        You will be redirected to the login page shortly...
        </p>
        <a id="redirect-manual" href="/LoginPage.html">Click here if you are not redirected.</a>
    </div>
  </section>
  `;

  main.insertAdjacentHTML("beforeend", html);

  setTimeout(() => {
    window.location.href = "/LoginPage.html";
  }, "2000");
};

const registrationFieldReset = function () {
  labels.forEach((a) => {
    a.innerHTML = "&nbsp;";
  });
};

const generateErrMsg = function (msg) {
  return `
    <img src="${ERROR_ICON}" alt="error-icon">
    <p>${msg}</p>
    `;
};

import { TIME_OUT } from "./config.js";

const timeOut = function (sec) {
  return new Promise(function (res) {
    setTimeout(res, 1000 * sec);
  });
};

export const getJSON = async function (url) {
  try {
    let header = new Headers();

    let request = new Request(url, {
      method: "GET",
      headers: header,
      credentials: "include",
    });

    const res = await Promise.race([fetch(request), timeOut(TIME_OUT)]);

    if (res.status === 204) return;

    if (!res.ok) {
      throw new Error(`${res.status}`);
    }
    return await res.json();
  } catch (error) {
    console.error(`${"🚨JSON🚨"} + ${error}`);
    throw error;
  }
};

export const postJSON = async function (url, data) {
  let header = new Headers();
  header.append("Content-type", "application/json");

  let request = new Request(url, {
    method: "POST",
    headers: header,
    credentials: "include",
    body: JSON.stringify(data),
  });

  return await fetch(request);
};

import { TIME_OUT } from "./config.js";

const timeOut = function (sec) {
  return new Promise(function (res) {
    setTimeout(res, 1000 * sec);
  });
};

export const getJSON = async function (url) {
  try {
    const res = await Promise.race([fetch(url), timeOut(TIME_OUT)]);

    if (!res.ok) {
      console.log(res);
      throw new Error(`${res.ok}`);
    }
    return await res.json();
  } catch (error) {
    console.error(`${"🚨JSON🚨"} + ${error.headers}`);
    throw error;
  }
};

export const getPayload = function (data) {
  return {
    method: "POST",
    body: JSON.stringify(data),
    headers: {
      "Content-type": "application/json",
    },
  };
};

import { TIME_OUT } from "./config";

const timeOut = function (sec) {
  return new Promise(function (res) {
    setTimeout(res, 1000 * sec);
  });
};

export const getJSON = async function (url) {
  const res = await Promise.race(fetch(url), timeOut(TIME_OUT));
  console.log(res);
  return await res.json();
};

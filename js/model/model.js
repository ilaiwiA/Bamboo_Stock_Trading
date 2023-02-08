import { URL } from "../config.js";
import { getJSON } from "../helper.js";

export const generateStockList = async function () {
  try {
    const data = await getJSON(URL);
    console.log(data);
    return data;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

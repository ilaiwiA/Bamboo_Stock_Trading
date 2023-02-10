import { URL } from "../config.js";
import { getJSON } from "../helper.js";

export const state = {
  stock: {},
};

export const generateStockList = function (data, panelType) {
  try {
    const list = [];
    data.forEach((val) => {
      const { assetType, symbol, description, lastPrice } = val[1];
      list.push({
        ticker: val[0],
        assetType,
        symbol,
        description,
        lastPrice,
        ...(val[1].quantity && { quantity: val[1].key }),
      });
    });
    return list;
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

export const generateStock = function (data) {
  try {
    return ({ assetType, symbol, description, lastPrice } = stock);
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

export const generateNewsObject = function (data) {
  try {
    state.news = data.data.map((a) => a);
    console.log(state);
  } catch (error) {
    console.error(error);
  }
};

export const loadStockList = async function (panelType) {
  try {
    const data = [...Object.entries(await getJSON(URL + "stocks"))];
    state[`${panelType}`] = generateStockList(data, panelType);
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

export const loadNews = async function () {
  try {
    const data = await getJSON(URL + "news");
    generateNewsObject(data);
  } catch (error) {
    console.error(error);
  }
};

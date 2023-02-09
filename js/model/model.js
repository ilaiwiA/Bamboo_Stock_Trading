import { URL } from "../config.js";
import { getJSON } from "../helper.js";

export const state = {
  stock: {},
};

export const generateStockList = async function (panelType) {
  try {
    const data = [...Object.entries(await getJSON(URL))];
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

export const generateStock = async function (data) {
  try {
    return ({ assetType, symbol, description, lastPrice } = stock);
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

export const loadStockList = async function (panelType) {
  try {
    state[`${panelType}`] = await generateStockList(panelType);
  } catch (error) {
    console.error(`${"🚨🚨🚨"} + ${error}`);
  }
};

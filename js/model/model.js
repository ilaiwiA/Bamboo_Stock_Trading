import { URL } from "../config";
import { getJSON } from "../helper";

const generateStockList = async function () {
  const data = await getJSON(URL);
  console.log(data);
};

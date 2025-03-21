package com.touchMind.core.json;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This program is a very simple implementation to emulate xpath APIs
 * for JSON Data in Java.
 *
 * @author satyajitpaul
 * @version 1.0
 * @modified 2017-04-24
 * @since 2017-04-24
 */

/**
 * MIT license
 * ==========================================================================
 * Copyright 2017 Satyajit Paul
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * ==========================================================================
 */

public class SimpleJSONDataReader {

    private static final String JSON_ARRAY_TYPE = "4";
    private static final String JSON_OBJECT_TYPE = "6";
    /*
     * The variable is introduced to deal with two different
     * situations -
     *
     * design time : when a user is exploring the jpath
     * for a particular JSON Content. During that time the
     * jpath may be wrong and may throw legitimate Exception
     * when data is missing due to wrong path.
     *
     * run time: Usually a user will use a jpath that has been
     * validated. In that case, an exception may be thrown as
     * data may be missing even when path is correct. In such
     * situations, one may like to supress the exceptions. You
     * may set the value of SUPPRESS_JSON_EXCEPTION to true to
     * achieve this.
     *
     */
    public boolean SUPPRESS_JSON_EXCEPTION = false;

    /**
     * NOT a singleton class, this is just a helper method.
     * Users are free to instantiate the class directly and
     * use it.
     *
     * @return
     */
    public static SimpleJSONDataReader getInstance() {
        return new SimpleJSONDataReader();
    }

    /**
     * main method has few examples of how you can invoke the methods
     *
     * @param args
     */
    public static void main(String[] args) {
        SimpleJSONDataReader reader = SimpleJSONDataReader.getInstance();
        String jsonLocation = "product__PRICE__.json";
        //reader.runTests2(jsonLocation);
        SimpleJSONDataReader.runTests4(jsonLocation);
        //SimpleJSONDataReader.runTests3(jsonLocation);
        // SimpleJSONDataReader.runTests5(jsonLocation);
        //runTests4();
    }

    /*
            private static void runTests5(String sourceLocation) {
                SimpleJSONDataReader jDataReader = SimpleJSONDataReader.getInstance();

                String jsonDataUrl = "http://www.fanffair.com/json?fetchsize=10&before=1490799314000&type=HOME_PAGE&noCache=1490801422724";
                try {
                    JSONObject jsonData = jDataReader.getFileContent(sourceLocation);
                    String jPath = "/data[]/likes/summary/total_count";
                    //Map<StringBuilder, Object> jpathList = jDataReader.getJPathValueMap(jPath, jsonData);

                    long t0 = System.currentTimeMillis();

                    //System.out.println(jDataReader.getJPathValueMap(jPath, jsonData));
                    System.out.println(jDataReader.getValueList(jPath, jsonData));

                    long t1 = System.currentTimeMillis();

                    System.out.println("t1 - t0 = " + (t1 - t0));



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


     */
    private static void runTests4(String sourceLocation) {
        SimpleJSONDataReader jDataReader = SimpleJSONDataReader.getInstance();
        //String sourceLocation = "/Users/johndoe/Documents/workspace/StockPeeker/20170325/yahoo/stock-quotes/ABB.NS.json";
        String jsonDataUrl = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/ABB.NS?modules=assetProfile,financialData,defaultKeyStatistics,incomeStatementHistory,cashflowStatementHistory,balanceSheetHistory";
        try {
            //JSONObject jsonData = jReader.getFileContent(sourceLocation);
            JSONObject jsonData = jDataReader.getFileContent(sourceLocation);

            //String jPath = "/quoteSummary/result[]/defaultKeyStatistics/sharesOutstanding/fmt";
            long t0 = System.currentTimeMillis();

            // Map<StringBuilder, Object> jpathValueMap = jDataReader.getJPathValueMap(jPath, jsonData);

            Map<StringBuilder, Object> jpathValueMapOne = jDataReader.getJPathValueMap("/", jsonData);

            long t1 = System.currentTimeMillis();

            System.out.println("t1 - t0 = " + (t1 - t0));

            //System.out.println(jpathValueMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void runTests3(String sourceLocation) {
        SimpleJSONDataReader jDataReader = SimpleJSONDataReader.getInstance();
        //String sourceLocation = "/Users/johndoe/Documents/workspace/StockPeeker/20170325/yahoo/stock-quotes/ABB.NS.json";
        String jsonDataUrl = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/ABB.NS?modules=assetProfile,financialData,defaultKeyStatistics,incomeStatementHistory,cashflowStatementHistory,balanceSheetHistory";
        try {
            //JSONObject jsonData = jReader.getFileContent(sourceLocation);
            JSONObject jsonData = jDataReader.getFileContent(sourceLocation);
            jDataReader.getJPathValueMap("/data[]/l", jsonData);
            String jPath = "/quoteSummary/result[0]/defaultKeyStatistics/sharesOutstanding/fmt";
            String value = jDataReader.getValue(jPath, jsonData);
            System.out.println("jPath = " + jPath);
            System.out.println("value = " + value);
/*
                jPath = "/quoteSummary/result[0]/defaultKeyStatistics/forwardPE/raw";
                String dValue = jDataReader.getValue(jPath, jsonData);
                System.out.println("jPath = " + jPath);
                System.out.println("value = " + dValue);

                jPath = "quoteSummary/result[0]/defaultKeyStatistics/sharesOutstanding/raw";
                String intValue = jDataReader.getValue(jPath, jsonData);
                System.out.println("jPath = " + jPath);
                System.out.println("value = " + intValue );




                jPath = "quoteSummary/result[0]/defaultKeyStatistics/sharesOutstanding/raw";
                System.out.println("jPath = " + jPath);
                intValue = jDataReader.getValue(jPath, jsonData);
                System.out.println("value = " + intValue);


 */
            jPath = "quoteSummary/result[0]/defaultKeyStatistics/lastSplitDate/raw1";
            System.out.println("jPath = " + jPath);
            jDataReader.SUPPRESS_JSON_EXCEPTION = true;
            Object splitDateLong = jDataReader.getValue(jPath, jsonData);
            if (splitDateLong != null && !splitDateLong.equals("")) {
                Date splitDate = new Date(Long.valueOf(splitDateLong.toString()).longValue());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String _splitDate = formatter.format(splitDate);
                System.out.println("value = " + _splitDate);
            }

            jPath = "quoteSummary/result[0]";
            String jsonValue = jDataReader.getValue(jPath, jsonData);
            System.out.println("jPath = " + jPath);
            System.out.println("value = " + new JSONObject(jsonValue).toString(4));

            jPath = "quoteSummary/result[Array]";
            String jsonArrValue = jDataReader.getValue(jPath, jsonData);
            System.out.println("jPath = " + jPath);
            System.out.println("value = " + new JSONArray(jsonArrValue).toString(4));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method can be used when the jpath translates to single value.
     *
     * @param path
     * @param jsonData
     * @return
     * @throws JSONException
     */
    public String getValue(String path, JSONObject jsonData) throws JSONException {
        path = path.startsWith("/") ? path.substring(1) : path;
        String[] parts = path.split("/");
        Object value = getJSONValueSimple(parts, jsonData);
        return value.toString();
    }

    /**
     * This method can be used when the jpath translates to multiple values.
     *
     * @param jPath
     * @param jsonData
     * @return
     * @throws JSONException
     */
    public String getValueList(String jPath, JSONObject jsonData) throws JSONException {
        Map<StringBuilder, Object> jpathList = getJPathValueMap(jPath, jsonData);
        return jpathList.values().toString();
    }

    /**
     * The method is useful when one 'jpath' expression translates to multiple values. This returns
     * the map of jpath expressions along with values.
     *
     * @param jpath
     * @param jsonData
     * @return
     * @throws JSONException
     */
    public Map<StringBuilder, Object> getJPathValueMap(String jpath, Object jsonData) throws JSONException {
        JSONObject jsonDataObj = (JSONObject) jsonData;
        jpath = jpath.startsWith("/") ? jpath.substring(1) : jpath;
        String[] parts = jpath.split("/");
        Map<StringBuilder, Object> jPathValueMap = new LinkedHashMap<StringBuilder, Object>();
        StringBuilder generatedJPath = new StringBuilder("/");
        jPathValueMap.put(generatedJPath, jsonDataObj);
        int counter = 0;

        for (String part : parts) {
            StringBuilder newJPath = new StringBuilder();
            String partName = part.indexOf("[") != -1 ? part.substring(0, part.indexOf("[")) : part;
            String dataType = getDataTypeSimple(part, parts.length - counter);
            Map<StringBuilder, Object> _jPathValueMap = new LinkedHashMap<StringBuilder, Object>();
            if ("String".equals(dataType)) {
                //last part in the list of parts
                Iterator<StringBuilder> iter = jPathValueMap.keySet().iterator();
                StringBuilder _jpath = null;
                while (iter.hasNext()) {
                    _jpath = iter.next();
                    JSONObject _jsonDataObj = (JSONObject) jPathValueMap.get(_jpath);
                    _jPathValueMap.put(new StringBuilder(_jpath).append(partName), _jsonDataObj.getString(partName));
                }
            } else if ("Object".equals(dataType)) {
                Iterator<StringBuilder> iter = jPathValueMap.keySet().iterator();
                StringBuilder _jpath = null;
                while (iter.hasNext()) {
                    _jpath = iter.next();
                    JSONObject _jsonDataObj = (JSONObject) jPathValueMap.get(_jpath);
                    _jPathValueMap.put(new StringBuilder(_jpath).append(partName).append("/"), _jsonDataObj.getJSONObject(partName));
                }

            } else if ("Array".equals(dataType)) {
                String arrValuePart = part.substring(part.indexOf('[') + 1, part.indexOf(']'));
                Iterator<StringBuilder> _iter = jPathValueMap.keySet().iterator();

                while (_iter.hasNext()) {
                    StringBuilder _jpath = _iter.next();
                    JSONObject _jsonDataObj = (JSONObject) jPathValueMap.get(_jpath);
                    JSONArray _dataArray = _jsonDataObj.getJSONArray(partName);
                    int _arraySize = _dataArray.length();
                    if (arrValuePart.length() == 0) {
                        while (--_arraySize >= 0) {
                            _jPathValueMap.put(new StringBuilder(_jpath).append(partName + "[" + _arraySize + "]" + "/"), _dataArray.getJSONObject(_arraySize));
                        }
                    } else if (arrValuePart.indexOf("-") != -1) {
                        //A Range of numeric values represent Array location range
                        String[] range = arrValuePart.split("-");
                        int startVal = Integer.parseInt(range[0]);
                        int endVal = Integer.parseInt(range[1]);
                        for (; startVal <= endVal; startVal++) {
                            _jPathValueMap.put(new StringBuilder(_jpath).append(partName + "[" + startVal + "]").append("/"), _dataArray.getJSONObject(startVal));
                        }
                    } else if (arrValuePart.indexOf("=") != -1) {
                        //String comparison
                        int location = -1;
                        String name = arrValuePart.split("=")[0];
                        String value = arrValuePart.split("=")[1];
                        JSONArray arr = _jsonDataObj.getJSONArray(partName);
                        for (int i = 0; i < arr.length(); i++) {
                            String val = arr.getJSONObject(i).getString(name);
                            if (value.equals(val)) {
                                location = i;
                                _jPathValueMap.put(new StringBuilder(_jpath).append(partName + "[" + location + "]").append("/"), _dataArray.getJSONObject(location));
                            } else if (StringUtils.isNumeric(value) && StringUtils.isNumeric(val)) {
                                double _value = Double.parseDouble(value);
                                double _val = Double.parseDouble(val);

                                if (_value == _val) {
                                    location = i;
                                    _jPathValueMap.put(new StringBuilder(_jpath).append(partName + "[" + location + "]").append("/"), _dataArray.getJSONObject(location));
                                }
                            }
                        }

                    } else if (arrValuePart.indexOf("==") != -1) {
                        System.err.println("Arithmatic Comparison. Not Yet Implemented");
                    } else if (arrValuePart.indexOf(">") != -1) {
                        System.err.println("Arithmatic Comparison. Not Yet Implemented");
                    } else if (arrValuePart.indexOf("<") != -1) {
                        System.err.println("Arithmatic Comparison. Not Yet Implemented");
                    } else if (arrValuePart.indexOf(">=") != -1) {
                        System.err.println("Arithmatic Comparison. Not Yet Implemented");
                    } else if (arrValuePart.indexOf("<=") != -1) {
                        System.err.println("Arithmatic Comparison. Not Yet Implemented");
                    } else if (arrValuePart.indexOf("!=") != -1) {
                        System.err.println("Arithmatic Comparison. Not Yet Implemented");
                    } else if (arrValuePart.indexOf(",") != -1) {
                        System.err.println("For list of comma separated Indexes. Not Yet Implemented");
                    } else if (StringUtils.isNumeric(arrValuePart)) {
                        //Numeric value represents a specific Array location
                        int arrValue = Integer.parseInt(arrValuePart);
                        _jPathValueMap.put(new StringBuilder(_jpath).append(part).append("/"), _dataArray.getJSONObject(arrValue));
                    }
                }
            }
            jPathValueMap = _jPathValueMap;
            counter++;
        }
        return jPathValueMap;
    }

    /**
     * This recursive method is core method that traverses the JSON Data for return the requested jpath data.
     * This is a private method.
     *
     * @param parts
     * @param jsonData
     * @return
     * @throws JSONException
     */
    private Object getJSONValueSimple(String[] parts, Object jsonData) throws JSONException {
        String pathValue = getPathValue(parts[0]);
        String dataType = getDataTypeSimple(parts[0], parts.length);
        if ("Object".equalsIgnoreCase(dataType) || JSON_OBJECT_TYPE.equals(dataType)) {
            JSONObject jsonDataObj = (JSONObject) jsonData;
            jsonData = jsonDataObj.getJSONObject(pathValue);
        } else if ("Array".equalsIgnoreCase(dataType) || JSON_ARRAY_TYPE.equals(dataType)) {
            JSONObject jsonDataObj = (JSONObject) jsonData;
            int dataLocInArr = getArrayLocation(parts[0], pathValue, jsonDataObj);
            if (dataLocInArr > -1) {
                jsonData = jsonDataObj.getJSONArray(pathValue).get(dataLocInArr);
            } else {
                jsonData = jsonDataObj.getJSONArray(pathValue);
            }
        } else {
            String value = "";
            JSONObject jsonDataObj = (JSONObject) jsonData;

            if (SUPPRESS_JSON_EXCEPTION) {
                try {
                    value = jsonDataObj.getString(pathValue);
                } catch (JSONException jsone) {
                    System.err.println("User has used a wrong jpath or a non existing jpath.");
                }
            } else {
                value = jsonDataObj.getString(pathValue);
            }
            return value;
        }

        if (parts.length > 1) {
            return getJSONValueSimple(Arrays.copyOfRange(parts, 1, parts.length), jsonData);
        } else {
            return jsonData;
        }
    }

    private String getPathValue(String pathlet) {
        String pathValue = pathlet.indexOf("[") != -1 ? pathlet.substring(0, pathlet.indexOf("[")) : pathlet;
        return pathValue;
    }

    private String getDataTypeSimple(String pathlet, int partsLength) {
        if (pathlet.indexOf("[") != -1) return "Array";
        if (partsLength == 1) return "String";
        return "Object";
    }

    private int getArrayLocation(String pathlet, String pathValue, JSONObject jsonDataObj) throws JSONException {
        int location = -1;
        String loc = pathlet.substring(pathlet.lastIndexOf("[") + 1, pathlet.lastIndexOf("]"));
        if (loc.indexOf("=") != -1) {
            String name = loc.split("=")[0];
            String value = loc.split("=")[1];
            JSONArray arr = jsonDataObj.getJSONArray(pathValue);
            for (int i = 0; i < arr.length(); i++) {
                String val = arr.getJSONObject(i).getString(name);
                if (value.equals(val)) {
                    return i;
                }
            }
            return location;
        }

        try {
            location = Integer.parseInt(loc);
        } catch (java.lang.NumberFormatException nfe) {
            //eat it
        }
        return location;
    }

    /**
     * Method will be used to read the json data from local file system
     *
     * @param sourceLocation
     * @return
     * @throws JSONException
     */
    public JSONObject getFileContent(String sourceLocation) throws JSONException {
        File file = new File(sourceLocation);
        System.out.println("###########");
        System.out.println(file.getAbsolutePath());
        System.out.println("###########");
        StringBuilder strbldr = new StringBuilder();
        FileReader fr;
        try {
            fr = new FileReader(sourceLocation);
            BufferedReader br = new BufferedReader(fr);
            String s;
            while ((s = br.readLine()) != null) {
                strbldr.append(s);
                strbldr.append("\n");
            }
            fr.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new JSONObject(strbldr.toString().trim());
    }
/*
        private void runTests2(String sourceLocation) {
            SimpleJSONDataReader jDataReader = SimpleJSONDataReader.getInstance();

            String jsonDataUrl = "http://www.fanffair.com/json?fetchsize=10&before=1490799314000&type=HOME_PAGE&noCache=1490801422724";
            try {
                JSONObject jsonData = jDataReader.getFileContent(sourceLocation);
                String jPath = "/fbids";
                String value = jDataReader.getValue(jPath, jsonData);
                System.out.println("jPath = " + jPath);
                System.out.println("value = " + value);

                jPath = "/data[4]/id";
                value = jDataReader.getValue(jPath, jsonData);
                System.out.println("jPath = " + jPath);
                System.out.println("value = " + value);

                jPath = "/data[1]/likes/summary/total_count";
                System.out.println("jPath = " + jPath);
                value = jDataReader.getValue(jPath, jsonData);
                System.out.println("value = " + value);

                jPath = "/data[3]/likes";
                System.out.println("jPath = " + jPath);
                String jsonValue = jDataReader.getValue(jPath, jsonData);
                System.out.println("value = " + jsonValue);

                jPath = "/data[id=131272076894593_1420960724592382]/likes/summary/total_count";
                System.out.println("jPath = " + jPath);
                value = jDataReader.getValue(jPath, jsonData);
                System.out.println("value = " + value); // 142

                jPath = "/data";
                String jArrValue = jDataReader.getValue(jPath, jsonData);
                System.out.println("jPath = " + jPath);
                System.out.println("value = " + jArrValue);

                jPath = "/data[3]";
                String jObjValue = jDataReader.getValue(jPath, jsonData);
                System.out.println("jPath = " + jPath);
                System.out.println("value = " + jObjValue);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

 */
}


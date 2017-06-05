package com.estudio.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang3.StringUtils;

public final class JSONUtils {

	/**
	 * 异常转化为JSON对象
	 * 
	 * @param json
	 * @param e
	 * @return
	 */
	public static JSONObject except2JSON(final JSONObject json,
			final Exception e) {
		final JSONObject result = json != null ? json : new JSONObject();
		result.put("r", false);
		result.put("msg", ExceptionUtils.loggerException(e));
		return result;
	}

	/**
	 * 异常转化为JSON对象
	 * 
	 * @param json
	 * @param e
	 * @return
	 */
	public static JSONObject except2JSON(final Exception e) {
		final JSONObject json = new JSONObject();
		except2JSON(json, e);
		return json;
	}

	/**
	 * 将值追加到JSON
	 * 
	 * @param result
	 * @param key
	 * @param value
	 * @return
	 */
	public static JSONObject append(final JSONObject json, final String key,
			final Object value) {
		final JSONObject result = json != null ? json : new JSONObject();
		if (result.containsKey(key))
			result.getJSONArray(key).add(value);
		else {
			final JSONArray array = new JSONArray();
			array.add(value);
			result.put(key, array);
		}

		return result;
	}

	private JSONUtils() {
	}

	public static JSONObject parserJSONObject(final String jsonStr) {
		if (StringUtils.isEmpty(jsonStr))
			return null;
		return (JSONObject) JSONValue.parse(jsonStr);
	}

	public static JSONArray parserJSONArray(final String jsonStr) {
		if (StringUtils.isEmpty(jsonStr))
			return null;
		return (JSONArray) JSONValue.parse(jsonStr);
	}

	public static List<String> array2StringList(JSONArray jsonArray) {
		List<String> result = new ArrayList<String>();
		if (jsonArray != null)
			for (int i = 0; i < jsonArray.size(); i++)
				result.add(jsonArray.getString(i));
		return result;
	}

	public static void sort(JSONArray ja, final String field, boolean isAsc) {
		if (ja != null) {
			List<JSONObject> list = new ArrayList<JSONObject>();
			for (int i = 0; i < ja.size(); i++) {
				list.add(ja.getJSONObject(i));
			}
			Collections.sort(list, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					// TODO Auto-generated method stub
					Object f1 = o1.get(field);
					Object f2 = o2.get(field);
					if (f1 instanceof Number && f2 instanceof Number) {
						return ((Number) f1).intValue()
								- ((Number) f2).intValue();
					} else {
						return f1.toString().compareTo(f2.toString());
					}
				}

			});
			if (!isAsc) {
				Collections.reverse(ja);
			}
		}
	}
}

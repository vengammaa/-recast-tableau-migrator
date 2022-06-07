package com.lti.data.recasttableaumigrator.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lti.data.recasttableaumigrator.model.DatasourceModel;

public class DatasourceModelingJoinsEngine {

	public static List<DatasourceModel> joinEngine(List<DatasourceModel> dataSourceList) {

		List<DatasourceModel> newDataSourceModelList = new ArrayList<>();
		DatasourceModel datasourceModel = null;

		Map<String, Integer> map = new HashMap<>();

		List<DatasourceModel> tempList = new ArrayList<>();
		DatasourceModel temp = null;

		int i = 0;
		boolean isFlag = false;
		if (dataSourceList.size() > 0) {
			for (DatasourceModel mdl : dataSourceList) {
				isFlag = false;

				try {
					if (i == 0) {
						map.put(mdl.getLtable(), 1);
						map.put(mdl.getRtable(), 1);

						datasourceModel = new DatasourceModel();
						datasourceModel.setReportId(mdl.getReportId());
						datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
						datasourceModel.setRcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
						datasourceModel.setLcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
						datasourceModel.setRtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
						datasourceModel.setLtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
						isFlag = true;
					} else if (map.containsKey(mdl.getLtable()) && i > 0) {
						datasourceModel = new DatasourceModel();
						datasourceModel.setReportId(mdl.getReportId());
						datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
						datasourceModel.setRcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
						datasourceModel.setLcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
						datasourceModel.setRtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
						datasourceModel.setLtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
						isFlag = true;
					} else if (!map.containsKey(mdl.getLtable()) && map.containsKey(mdl.getRtable()) && i > 0) {
						datasourceModel = new DatasourceModel();
						datasourceModel.setReportId(mdl.getReportId());

						datasourceModel.setRtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
						datasourceModel.setRcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
						datasourceModel.setLtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
						datasourceModel.setLcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");

						if (mdl.getType() != null) {
							if (mdl.getType().trim().equalsIgnoreCase("left")) {
								datasourceModel.setType("right");
							} else if (mdl.getType().trim().equalsIgnoreCase("right")) {
								datasourceModel.setType("left");
							} else {
								datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
							}
						}

						isFlag = true;
					} else {
						temp = new DatasourceModel();
						temp.setReportId(mdl.getReportId());
						temp.setType(mdl.getType() != null ? mdl.getType().trim() : "");
						temp.setRcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
						temp.setLcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
						temp.setRtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
						temp.setLtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
						tempList.add(temp);
					}

					if (isFlag) {
						newDataSourceModelList.add(datasourceModel);
						map.put(mdl.getLtable(), 1);
						map.put(mdl.getRtable(), 1);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}

			for (DatasourceModel mdl : tempList) {
				try {

					if (map.containsKey(mdl.getLtable())) {
						datasourceModel = new DatasourceModel();
						datasourceModel.setReportId(mdl.getReportId());
						datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
						datasourceModel.setRcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
						datasourceModel.setLcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
						datasourceModel.setRtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
						datasourceModel.setLtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
						isFlag = true;
					} else if (!map.containsKey(mdl.getLtable()) && map.containsKey(mdl.getRtable())) {
						datasourceModel = new DatasourceModel();
						datasourceModel.setReportId(mdl.getReportId());
//					datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
						datasourceModel.setRtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
						datasourceModel.setRcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
						datasourceModel.setLtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
						datasourceModel.setLcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");

						if (mdl.getType() != null) {
							if (mdl.getType().trim().equalsIgnoreCase("left")) {
								datasourceModel.setType("right");
							} else if (mdl.getType().trim().equalsIgnoreCase("right")) {
								datasourceModel.setType("left");
							} else {
								datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
							}
						}

						isFlag = true;
					} else {
						System.out.println("Joins is not correct");
						throw new Exception("Joins is not correct");
					}

					if (isFlag) {
						newDataSourceModelList.add(datasourceModel);
						map.put(mdl.getLtable(), 1);
						map.put(mdl.getRtable(), 1);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			newDataSourceModelList.forEach(System.out::println);

			Collections.reverse(newDataSourceModelList);

			return newDataSourceModelList;
		} else {
			return dataSourceList;
		}
	}

//	public static List<DatasourceModel> joinEngine(List<DatasourceModel> dataSourceList) {
//		if (dataSourceList.size() > 1) {
//			List<DatasourceModel> newDataSourceModelList = new ArrayList<>();
//			Map<String, Integer> map = new HashMap<>();
//
//			for (DatasourceModel mdl : dataSourceList) {
//
//				if (mdl.getLtable() != null && !mdl.getLtable().trim().isEmpty()) {
//					if (map.containsKey(mdl.getLtable())) {
//						map.replace(mdl.getLtable(), map.get(mdl.getLtable()) + 1);
//					} else {
//						map.put(mdl.getLtable(), 1);
//					}
//				}
//				if (mdl.getRtable() != null && !mdl.getRtable().trim().isEmpty()) {
//					if (map.containsKey(mdl.getRtable())) {
//						map.replace(mdl.getRtable(), map.get(mdl.getRtable()) + 1);
//					} else {
//						map.put(mdl.getRtable(), 1);
//					}
//				}
//			}
//			map.entrySet().removeIf(entry -> entry.getValue() == 1);
//
//			LinkedHashMap<String, Integer> map1 = sortByValue(map);
//
//			DatasourceModel datasourceModel = null;
//
//			int i = 0;
//			String previousKey = "";
//			boolean isFlag = false;
//			System.out.println("Size = " + dataSourceList.size());
//			for (Map.Entry<String, Integer> entry : map1.entrySet()) {
//				String key = entry.getKey();
//				Integer val = entry.getValue();
//				System.out.println(key + " " + val);
//				i++;
//
//				for (DatasourceModel mdl : dataSourceList) {
//					isFlag = false;
//
//					if (mdl.getLtable().trim().equalsIgnoreCase(key) || mdl.getRtable().trim().equalsIgnoreCase(key)) {
//
//						if (key.equalsIgnoreCase(mdl.getLtable()) && val > 1 && i == 1) {
//							datasourceModel = new DatasourceModel();
//							datasourceModel.setReportId(mdl.getReportId());
//							datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
//							datasourceModel.setRcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
//							datasourceModel.setLcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
//							datasourceModel.setRtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
//							datasourceModel.setLtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
//							isFlag = true;
//
//						} else if (key.equalsIgnoreCase(mdl.getRtable()) && val > 1 && i == 1) {
//							datasourceModel = new DatasourceModel();
//							datasourceModel.setReportId(mdl.getReportId());
//							datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
//							datasourceModel.setRtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
//							datasourceModel.setRcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
//							datasourceModel.setLtable(key != null ? key.trim() : "");
//							datasourceModel.setLcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
//							isFlag = true;
//						} else if (key.equalsIgnoreCase(mdl.getRtable()) && val > 1 && i > 1) {
//							datasourceModel = new DatasourceModel();
//							if (!previousKey.trim().equals("")) {
//								if (!previousKey.contains(mdl.getLtable())) {
//									datasourceModel.setReportId(mdl.getReportId());
//									datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
//									datasourceModel.setLtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
//									datasourceModel.setRtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
//									datasourceModel.setRcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
//									datasourceModel.setLtable(key != null ? key.trim() : "");
//									datasourceModel.setLcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
//									isFlag = true;
//								}
//							}
//
//						} else if (key.equalsIgnoreCase(mdl.getLtable()) && val > 1 && i > 1) {
//							datasourceModel = new DatasourceModel();
////							if (!previousKey.trim().equals("")) {
////								if (!previousKey.contains(mdl.getLtable())) {
//							datasourceModel.setReportId(mdl.getReportId());
//							datasourceModel.setType(mdl.getType() != null ? mdl.getType().trim() : "");
//							datasourceModel.setLtable(mdl.getLtable() != null ? mdl.getLtable().trim() : "");
//							datasourceModel.setRtable(mdl.getRtable() != null ? mdl.getRtable().trim() : "");
//							datasourceModel.setRcolumn(mdl.getRcolumn() != null ? mdl.getRcolumn().trim() : "");
//							datasourceModel.setLtable(key != null ? key.trim() : "");
//							datasourceModel.setLcolumn(mdl.getLcolumn() != null ? mdl.getLcolumn().trim() : "");
//							isFlag = true;
////								}
////							}
//						}
//
//						if (isFlag) {
//							newDataSourceModelList.add(datasourceModel);
//						}
//					}
//
//				}
//
//				if (!previousKey.contains(key)) {
//					previousKey = previousKey + "," + key;
//				}
//			}
//
//			Collections.reverse(newDataSourceModelList);
//
//			return newDataSourceModelList;
//		} else {
//			return dataSourceList;
//		}
//	}

//	private static LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> hm) {
//
//		LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
//
//		// Use Comparator.reverseOrder() for reverse ordering
//		hm.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//				.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
//
////		System.out.println("Reverse Sorted Map   : " + reverseSortedMap);
//
//		return reverseSortedMap;
//	}

}

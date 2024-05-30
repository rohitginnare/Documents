package com.pct.geotab.payload;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pct.geotab.util.ErrorStackPrinter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ReportPayload {

	StringBuilder infoMessages = new StringBuilder();
	StringBuilder errorMessages = new StringBuilder();
	private int reportNumber;
	private String gatewayId;
	private String assetId;
	private String vin;
	private Double latitude;
	private Long engineHours;
	private Integer cargoState;
	private Integer doorState;
	private Double longitude;
	private Integer heading;
	private Double speed;
	private Long odometer;
	private String currentPowerSource;
	private Boolean isTrip;
	private Integer isPowerExternal; // int to Integer
	private String eventDatetime;
	private Double externalVoltage1;
	private String powerSource;
	private String locationDatetime;
	private Double externalVoltage2;
	private Double batteryVoltage = null; // double to Double
	private Integer absManufacturer;
	private String absModel;
	private Double absOdometer;
	private Integer absLampState; // int to Integer
	private int ATISQuantity;
	private int ATISDuration;
	private int ATISCondition;
	private String circuitColor;
	private String circuitState;
	private String liteSentryStatus;
	private Integer batteryRemainingCapacity = null; // int to Integer
	private Boolean isDoorInstalled = false;
	private Boolean isTemperatureInstalled = false;
	private Boolean isCargoInstalled = false;
	private Boolean isMinewTemperatureInstalled = false;
	private Boolean isInternalTemperatureInstalled = false;
	private Boolean doorStateInstalled;
	private String cargoInstalled;
	private Boolean isGPSValid;
	private Boolean isIgnitionOn;
	private String doorInstalled;
	private SensorMeasurements[] sensor;
	private MinewSensorMeasurements[] minewSensor;
	private ABSFaults[] absFaults;
	private Double internalTemperature;
	private String bleProcessorCondition;
	private Boolean isABSInstalled = false;
	private Boolean isABSFaultState = false;
	private Boolean isATISInstalled = false;
	private Boolean isLiteSentryInstalled = false;
	private Boolean isMaxonInstalled = false;
	private String statusFlags = null;
	private Integer liftGateCycles = null;
	private Integer motor1Runtime = null;
	private Integer motor2Runtime = null;
	public Integer stateOfCharge = null;
	private Double liftGateVoltage = null;
	private List<String> faultCodes;
	private String eventName;

//  ------------------
	private Boolean isTPMSInstalled = false;
	private Integer axle_index = null;
	private Integer tire_index = null;

	@Data
	public static class SensorMeasurements {

		public Double temperature;
		public String sensor_id;
		public String measurement_datetime;
	}

	@Data
	public static class MinewSensorMeasurements {

		public Double temperature;
		public String sensor_id;
		public String measurement_datetime;
		public String battery_level;
	}

	@Data
	public static class ABSFaults {

		public Integer fmi_codes;
		public Integer sid;
		public Integer pid;
		public Integer occuranceCount;
	}

//	---------------------------------------
	@Data
	public static class TPMSBetaMeasure {
		public Double floTemperatureDeg_C;
		public Double floPressureM_Bar;
		public String floBattery;
		public String floAge;

		public Double fliTemperatureDeg_C;
		public Double fliPressureM_Bar;
		public String fliBattery;
		public String fliAge;

		public Double friTemperatureDeg_C;
		public Double friPressureM_Bar;
		public String friBattery;
		public String friAge;

		public Double froTemperatureDeg_C;
		public Double froPressureM_Bar;
		public String froBattery;
		public String froAge;

		public Double rloTemperatureDeg_C;
		public Double rloPressureM_Bar;
		public String rloBattery;
		public String rloAge;

		public Double rliTemperatureDeg_C;
		public Double rliPressureM_Bar;
		public String rliBattery;
		public String rliAge;

		public Double rriTemperatureDeg_C;
		public Double rriPressureM_Bar;
		public String rriBattery;
		public String rriAge;

		public Double rroTemperatureDeg_C;
		public Double rroPressureM_Bar;
		public String rroBattery;
		public String rroAge;

	}

	public ReportPayload setPayload(String report, String uuid) {
		infoMessages.append("Message uuid " + uuid);
		errorMessages.append("Message uuid " + uuid);
		ArrayList<SensorMeasurements> measurementsList = new ArrayList<SensorMeasurements>();
		ArrayList<MinewSensorMeasurements> minewMeasurementsList = new ArrayList<MinewSensorMeasurements>();
		ArrayList<ABSFaults> absFaultsList = new ArrayList<ABSFaults>();
		List<String> faultCodesList = new ArrayList<String>();
		
//		----------------------------------
		ArrayList<TPMSBetaMeasure> tpmsBetaMeasureList = new ArrayList<TPMSBetaMeasure>();
		
		
		ReportPayload rPayload = new ReportPayload();
		try {
			JSONObject jsonObj = new JSONObject(report);
			rPayload.setGatewayId(jsonObj.getString("gateway_id"));
			
			try {
				rPayload.setEngineHours(jsonObj.getLong("engine_hours"));
			} catch (Exception e2) {
//				log.info("Message UUID: " + uuid + " ...engine hours not found");
				infoMessages.append(", engine hours not found, ");
			}
			rPayload.setEventName(jsonObj.getString("event_name"));
			if(rPayload.getEventName().equalsIgnoreCase("IGNITION_ON"))
			{
				rPayload.setIsIgnitionOn(true);
			}
			if(rPayload.getEventName().equalsIgnoreCase("IGNITION_OFF"))
			{
				rPayload.setIsIgnitionOn(false);
			}
			JSONObject summaryJsonObj = jsonObj.getJSONObject("summary");
			rPayload.setReportNumber(jsonObj.getInt("report_number"));
			rPayload.setLatitude(summaryJsonObj.getDouble("latitude"));
			rPayload.setLongitude(summaryJsonObj.getDouble("longitude"));
			rPayload.setSpeed(summaryJsonObj.getDouble("speed"));
			rPayload.setLocationDatetime(summaryJsonObj.getString("location_datetime"));
			try {
			rPayload.setIsTrip(summaryJsonObj.getBoolean("is_trip"));
			}
			catch(Exception e1) {
//				log.info("Message UUID: " + uuid + " ... isTrip not found");
				infoMessages.append("isTrip not found, ");
			}

			// This calculation is done because Geotab doesn't accept KMs
			// it accepts meters, hence we multiply the value by 1000 to get value in meters
			Double odometerDouble = summaryJsonObj.getDouble("odometer");
			odometerDouble = odometerDouble * 1000;
			rPayload.setOdometer(odometerDouble.longValue());
			rPayload.setEventDatetime(jsonObj.getString("event_datetime"));
			try {
				rPayload.setVin(jsonObj.getString("vin"));
			} catch (Exception e1) {
//				log.info("Message UUID: " + uuid + " ... vin not found");
				infoMessages.append("vin not found, ");
			}
			try {
				rPayload.setAssetId(jsonObj.getString("asset_id"));
			} catch (Exception e1) {
				rPayload.setAssetId("");
//				log.info("Message UUID: " + uuid + " ... assetId not found");
				infoMessages.append("assetId not found, ");
			}
			try {
				rPayload.setHeading(summaryJsonObj.getInt("heading"));
			} catch (Exception e1) {
				rPayload.setHeading(null);
//				log.info("Message UUID: " + uuid + " ... heading not found");
				infoMessages.append("heading not found, ");
			}
			try {
			rPayload.setCurrentPowerSource(summaryJsonObj.getString("current_power_source"));
			rPayload.setPowerSource(rPayload.getCurrentPowerSource());
			if (rPayload.getCurrentPowerSource().toUpperCase().contains("EXTERNAL")) {
				rPayload.setIsPowerExternal(1);
			} else {
				rPayload.setIsPowerExternal(0);
			}
			} catch(Exception e1) {
//				log.info("Message UUID: " + uuid + " ... Current power source not found");
				infoMessages.append("current power source not found, ");
			}

			JSONArray jsonArrayInternslSensors = jsonObj.getJSONArray("internal_sensors");
			if (jsonArrayInternslSensors != null && jsonArrayInternslSensors.length() > 0) {
				for (int i = 0; i < jsonArrayInternslSensors.length(); i++) {
					JSONObject childJsonArray = jsonArrayInternslSensors.getJSONObject(i);
					if (childJsonArray.get("type").equals("power-sensor")) {
						if (childJsonArray.get("external") != null) {
							JSONObject external = childJsonArray.getJSONObject("external");
							rPayload.setExternalVoltage1(external.getDouble("external_voltage_1"));
							try {
								rPayload.setExternalVoltage2(external.getDouble("external_voltage_2"));
							} catch (Exception e) {
								rPayload.setExternalVoltage2(null);
//								log.info("Message UUID: " + uuid + " ... external_voltage_2 not found");
								infoMessages.append("external_voltage_2 not found, ");
							}
						}
						if (childJsonArray.get("battery") != null) {
							JSONObject battery = childJsonArray.getJSONObject("battery");
							rPayload.setBatteryVoltage(battery.getDouble("voltage"));
							rPayload.setBatteryRemainingCapacity(battery.getInt("remaining_capacity"));
						}
					}
				}
			}

			JSONArray jsonArrayperipheralSensors = jsonObj.optJSONArray("peripheral_sensors");
			if (jsonArrayperipheralSensors != null && jsonArrayperipheralSensors.length() > 0) {
				try {
					for (int i = 0; i < jsonArrayperipheralSensors.length(); i++) {
						JSONObject childJsonArrayps = jsonArrayperipheralSensors.getJSONObject(i);
						if (childJsonArrayps.get("type").equals("ABS")) {
							rPayload.setIsABSInstalled(true);
							try {
								if (childJsonArrayps.getString("manufacturer").equalsIgnoreCase("Unknown")) {
									rPayload.setAbsManufacturer(0b000);
								} else if (childJsonArrayps.getString("manufacturer").equalsIgnoreCase("Hadlex")) {
									rPayload.setAbsManufacturer(0b001);
								} else if (childJsonArrayps.getString("manufacturer").equalsIgnoreCase("Wabco")) {
									rPayload.setAbsManufacturer(0b010);
								} else if (childJsonArrayps.getString("manufacturer").equalsIgnoreCase("Bendix")) {
									rPayload.setAbsManufacturer(0b011);
								}
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + " ... ABS Manufacturer not found");
								infoMessages.append("ABS Manufacturer not found, ");
							}
							try {
								rPayload.setAbsModel(childJsonArrayps.getString("model"));
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + " ... ABS Model not found");
								infoMessages.append("ABS Model not found, ");
							}
							try {
								if (childJsonArrayps.getDouble("odometer_kms") != 0.0) {
									rPayload.setAbsOdometer(childJsonArrayps.getDouble("odometer_kms"));
								}
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + "... ABS Odometer not found");
								infoMessages.append("ABS Odometer not found, ");
							}

							try {
								if (childJsonArrayps.getString("warning_lamp_status") != null) {
									String wLampState = childJsonArrayps.getString("warning_lamp_status");
									if (wLampState.equalsIgnoreCase("off")) {
										rPayload.setAbsLampState(0);
									} else if (wLampState.equalsIgnoreCase("on")) {
										rPayload.setAbsLampState(1);
									} else if (wLampState.equalsIgnoreCase("unknown")) {
										rPayload.setAbsLampState(2);
									}
								}
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + "... ABS Warning Lamp Status not found");
								infoMessages.append("ABS Warning Lamp Status not found, ");
							}
							try {
								if (childJsonArrayps.getBoolean("active_fault") == true) {
									rPayload.setIsABSFaultState(true);
									JSONArray absFaults = childJsonArrayps.getJSONArray("faults");
									for (int j = 0; j < absFaults.length(); j++) {
										JSONObject absFault = absFaults.getJSONObject(j);
										ABSFaults absFaultMeasurement = new ABSFaults();
										absFaultMeasurement
												.setFmi_codes(Integer.parseInt(absFault.getString("FMI_code")));
										try {
											absFaultMeasurement.setSid(Integer.parseInt(absFault.getString("SID")));
										} catch (Exception e) {
											// log.info("Message UUID: " + uuid + " ... SID not found");
										}
										try {
											absFaultMeasurement.setPid(Integer.parseInt(absFault.getString("PID")));
										} catch (Exception e) {
											// log.info("Message UUID: " + uuid + " ... PID not found");
										}
										absFaultMeasurement.setOccuranceCount(absFault.getInt("count"));
										absFaultsList.add(absFaultMeasurement);
									}
									if (!absFaultsList.isEmpty()) {
										rPayload.setAbsFaults(absFaultsList.toArray(new ABSFaults[0]));
									}
								}
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + "... ABS faults not found");
								infoMessages.append("ABS faults not found, ");
							}
						}
						if (childJsonArrayps.get("type").equals("ATIS")) {
							rPayload.setIsATISInstalled(true);
							if (childJsonArrayps.getInt("duration") != 0) {
								rPayload.setATISDuration(childJsonArrayps.getInt("duration"));
							}
							if (childJsonArrayps.getString("condition") != null) {
								if (childJsonArrayps.getString("condition").equalsIgnoreCase(("error"))) {
									rPayload.setATISCondition(3);

								} else if (childJsonArrayps.getString("condition").equalsIgnoreCase(("off"))) {
									rPayload.setATISCondition(0);

								} else if (childJsonArrayps.getString("condition")
										.equalsIgnoreCase(("on"))) {
									rPayload.setATISCondition(1);

								}
							}
							try {
								rPayload.setATISQuantity(childJsonArrayps.getInt("quantity"));
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + "... ATIS quantity not found");
								infoMessages.append("ATIS quantity not found, ");
							}
						}
						if (childJsonArrayps.get("type").equals("lite-sentry")) {
							rPayload.setIsLiteSentryInstalled(true);
							try {
								rPayload.setLiteSentryStatus(childJsonArrayps.getString("status"));
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + "... Lite Sentry status not found");
								infoMessages.append("Lite Sentry status not found, ");
							}
							try {
								if (childJsonArrayps.getString("circuitColor") != null) {
									rPayload.setCircuitColor(childJsonArrayps.getString("circuitColor"));
								}
							} catch (Exception e) {
//								log.info("Message UUID: " + uuid + "... Circuit color not found");
								infoMessages.append("circuit color not found, ");
							}
						}
						try {
							if (childJsonArrayps.get("type").equals("cargo-sensor")) {
								rPayload.setIsCargoInstalled(true);
								if (childJsonArrayps.getString("cargo_state").equals("loaded")) {
									rPayload.setCargoState(1);
								} else {
									rPayload.setCargoState(0);
								}
							}
						} catch (Exception e1) {
//							log.error("Message UUID: " + uuid + " Problem with processing cargo sensors : "
//									+ ErrorStackPrinter.getStackString(e1));
							errorMessages.append(" Problem with processing cargo sensors, ");
						}
						try {
							if (childJsonArrayps.get("type").equals("temperature-sensor")) {
								rPayload.setIsTemperatureInstalled(true);
								log.info("Message UUID: " + uuid + "... temperature sensor found");
								JSONArray measurements = childJsonArrayps.getJSONArray("measurements");
								for (int j = 0; j < measurements.length(); j++) {
									JSONObject measurementObject = measurements.getJSONObject(j);
									SensorMeasurements sensorMeasurements = new SensorMeasurements();
									sensorMeasurements.setTemperature(measurementObject.getDouble("temperature"));
									sensorMeasurements.setSensor_id(measurementObject.getString(("sensor_id")));
									sensorMeasurements.setMeasurement_datetime(
											measurementObject.getString("measurement_datetime"));
									measurementsList.add(sensorMeasurements);
								}
								if (!measurementsList.isEmpty()) {
									rPayload.setSensor(measurementsList.toArray(new SensorMeasurements[0]));
								}
							}
						} catch (Exception e) {
//							log.error("Message UUID: " + uuid + " Problem with processing temperature sensors : "
//									+ ErrorStackPrinter.getStackString(e));
							errorMessages.append(" Problem with processing temperature sensors, ");
						}
						try {
							if (childJsonArrayps.get("type").equals("temperature-sensor-minew")) {
								rPayload.setIsMinewTemperatureInstalled(true);
								rPayload.setBleProcessorCondition(childJsonArrayps.getString("condition"));

								log.info("Message UUID: " + uuid + "... minew temperature sensor found");
								try {
									JSONArray measurements = childJsonArrayps.getJSONArray("measurements");
									for (int j = 0; j < measurements.length(); j++) {
										JSONObject measurementObject = measurements.getJSONObject(j);
										MinewSensorMeasurements sensorMeasurements = new MinewSensorMeasurements();
										sensorMeasurements.setTemperature(measurementObject.getDouble("temperature"));
										sensorMeasurements.setSensor_id(measurementObject.getString(("sensor_id")));
										sensorMeasurements.setMeasurement_datetime(
												measurementObject.getString("measurement_datetime"));
										sensorMeasurements
												.setBattery_level(measurementObject.getString("battery_level"));
										minewMeasurementsList.add(sensorMeasurements);
									}
									if (!minewMeasurementsList.isEmpty()) {
										rPayload.setMinewSensor(
												minewMeasurementsList.toArray(new MinewSensorMeasurements[0]));
									}

								} catch (Exception e) {
//									log.info("Message UUID: " + uuid + "... Minew measurements not found");
									infoMessages.append(" Minew not found, ");
								}
							}
						} catch (Exception e) {
//							log.error("Message UUID: " + uuid + " Problem with processing minew temperature sensors : "
//									+ ErrorStackPrinter.getStackString(e));
							errorMessages.append(" Problem with processing minew temperature sensors, ");

						}

						// Internal Temperature Sensor also known as ambient temperature
						try {
							if (childJsonArrayps.get("type").equals("temperature-sensor-internal")) {
								rPayload.setIsInternalTemperatureInstalled(true);
//								log.info("Message UUID: " + uuid + "... internal temperature sensor found");
								infoMessages.append(", internal temperature found, ");
								JSONArray measurements = childJsonArrayps.getJSONArray("measurements");
								JSONObject measurementObject = measurements.getJSONObject(0);
								rPayload.setInternalTemperature(measurementObject.getDouble("temperature"));
							}
						} catch (Exception e) {
//							log.error(
//									"Message UUID: " + uuid + " Problem with processing internal temperature sensors : "
//											+ ErrorStackPrinter.getStackString(e));
							errorMessages.append(" Problem with processing internal temperature sensors, ");

						}
						try {
							if (childJsonArrayps.get("type").equals("door-sensor")) {
								rPayload.setIsDoorInstalled(true);
								if (childJsonArrayps.getString("door_state").equals("open")) {
									rPayload.setDoorState(1);
								} else {
									rPayload.setDoorState(0);
								}
							}
						} catch (Exception e) {
//							log.error("Message UUID: " + uuid + " Problem with processing door sensors : "
//									+ ErrorStackPrinter.getStackString(e));
							errorMessages.append(" Problem with processing door sensors, ");

						}

						// Maxon Sensors - 21.09.2022
						try {
							if (childJsonArrayps.get("type").equals("maxon-sensor")) {
								if (childJsonArrayps.getString("condition").equalsIgnoreCase("Online")) {
									rPayload.setIsMaxonInstalled(true);
									try {
										JSONArray faultCodes = childJsonArrayps.getJSONArray("fault_codes");
										for (int j = 0; j < faultCodes.length(); j++) {
											JSONObject fCode = faultCodes.getJSONObject(j);
											faultCodesList.add(fCode.getString("code"));
										}
										rPayload.faultCodes = faultCodesList;
									} catch (Exception e) {
//										log.info("Message UUID: " + uuid + "... Maxon fault codes not found"); // Changes
										infoMessages.append(" maxon fault codes not found, ");		// made
									}
									try {
										JSONArray measurements = childJsonArrayps.getJSONArray("measurements");
										for (int j = 0; j < measurements.length(); j++) {
											JSONObject measurementObject = measurements.getJSONObject(j);
											if (measurementObject.has("battery")) {
												rPayload.setLiftGateVoltage(measurementObject.getDouble("battery"));
											}
											if (measurementObject.has("charge")) {
												rPayload.setStateOfCharge(measurementObject.getInt("charge"));
											}
											if (measurementObject.has("liftgate_cycles")) {
												rPayload.setLiftGateCycles(measurementObject.getInt("liftgate_cycles"));
											}
											if (measurementObject.has("motor1_runtime")) {
												String motor1SecondStr = measurementObject.getString("motor1_runtime");
												if (!motor1SecondStr.equalsIgnoreCase("Time > 32 days")
														&& !motor1SecondStr.equalsIgnoreCase("Unknown time")) {
													int motor1Seconds = getTimeUtil(
															measurementObject.getString("motor1_runtime"));
													rPayload.setMotor1Runtime(motor1Seconds);
												}
											}
											if (measurementObject.has("motor2_runtime")) {
												String motor2SecondStr = measurementObject.getString("motor2_runtime");
												if (!motor2SecondStr.equalsIgnoreCase("Time > 32 days")
														&& !motor2SecondStr.equalsIgnoreCase("Unknown time")) {
													int motor2Seconds = getTimeUtil(
															measurementObject.getString("motor2_runtime"));
													rPayload.setMotor2Runtime(motor2Seconds);
												}
//											int motor2Seconds = getTimeUtil(measurementObject.getString("motor2_runtime"));
//											rPayload.setMotor2Runtime(motor2Seconds);
											}
										}
									} catch (Exception e) {
//										log.info("Message UUID: " + uuid + "... Maxon measurements not found");
										infoMessages.append("Maxon measurements not found, ");
									}
								}
						}
						}catch (Exception e) {
//							log.error("Message UUID: " + uuid + " Problem with processing maxon sensors : "
//									+ ErrorStackPrinter.getStackString(e));
							errorMessages.append(" Problem with processing maxon sensors, ");

						}
						
						
//						------------------------------------------
						// TPMS sensors 
						try {
							if (childJsonArrayps.get("type").equals("TPMS")) {
								if (childJsonArrayps.getString("condition").equalsIgnoreCase("Online")) {
									rPayload.setIsTPMSInstalled(true);
									log.info("Message UUID: " + uuid + "... TPMS sensor found");
									try {
										JSONArray measurements = childJsonArrayps.getJSONArray("measurements");
										for (int j = 0; j < measurements.length(); j++) {
											JSONObject measurementObject = measurements.getJSONObject(j);
											TPMSBetaMeasure tpmsBetaMeasure = new TPMSBetaMeasure();
											if(axle_index != null && axle_index == 1 && tire_index != null && tire_index == 1) {
											tpmsBetaMeasure.setFloTemperatureDeg_C(measurementObject.getDouble("temperature"));
											tpmsBetaMeasure.setFloPressureM_Bar(measurementObject.getDouble("pressure"));
											tpmsBetaMeasure.setFloBattery(measurementObject.getString("battery"));
											tpmsBetaMeasure.setFloAge(measurementObject.getString("age"));	
											
//											tpmsBetaMeasureList.add(tpmsBetaMeasure);
											}
											else if(axle_index != null && axle_index == 1 && tire_index != null && tire_index == 2) {
												tpmsBetaMeasure.setFloTemperatureDeg_C(measurementObject.getDouble("temperature"));
												tpmsBetaMeasure.setFloPressureM_Bar(measurementObject.getDouble("pressure"));
												tpmsBetaMeasure.setFloBattery(measurementObject.getString("battery"));
												tpmsBetaMeasure.setFloAge(measurementObject.getString("age"));
											}
										}
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}
						}catch (Exception e) {
							log.error("Message UUID: " + uuid
							+ " Error ocurred while processing peripheral sensors Json object : "
							+ ErrorStackPrinter.getStackString(e));
				}
				}	

		} catch (Exception e) {
			log.error("Message UUID: " + uuid + " Exception occured in report payload class : "
					+ ErrorStackPrinter.getStackString(e));
		}
		log.info("Info messages " + infoMessages);
		log.info("Error messages " + errorMessages);
		return rPayload;
	}

	public int getTimeUtil(String dateTime) {
		Integer seconds = 0;
		if (dateTime.contains("days")) {
			seconds = seconds + Integer.parseInt(dateTime.replace(" days", "")) * 86400; // converting days into seconds
		}
		if (dateTime.contains(" hours")) {
			seconds = Integer.parseInt(dateTime.split(" h")[0]) * 3600;
			if (dateTime.contains(" minutes")) {
				int strs2 = Integer
						.parseInt(dateTime.substring(dateTime.lastIndexOf("s ") + 2, dateTime.lastIndexOf(" m"))) * 60;
				seconds = seconds + strs2;
			}
		}
		if (dateTime.contains(" minutes")) {
			seconds = Integer.parseInt(dateTime.split(" m")[0]) * 60;
			if (dateTime.contains(" seconds")) {
				int strs2 = Integer
						.parseInt(dateTime.substring(dateTime.lastIndexOf("s ") + 2, dateTime.lastIndexOf(" s")));
				seconds = seconds + strs2;
			}
		}

		return seconds;
	}
}

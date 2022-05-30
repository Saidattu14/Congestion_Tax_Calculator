package com.example.Congestion_Tax_Calculator;

import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimatedResponse;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimationModel;
import com.example.Congestion_Tax_Calculator.Model.VehiclesModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CongestionTaxCalculatorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CongestionTaxCalculatorApplicationTests {

	@Autowired
	TestRestTemplate template;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Test
	void contextLoads() {
		assertNotNull(template);
	}

	@Test
	public void TestingCongestionTaxRulesApiOnCreateTable_shouldSucceedWith200() throws Exception {

		HttpEntity<String> request = new HttpEntity<>(null, new HttpHeaders());
		try {
			template.postForEntity("/api/v1/congestion_tax_rules/drop",request,String.class);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		ResponseEntity<String> result = template.postForEntity("/api/v1/congestion_tax_rules/create",request,String.class);
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
	}
	@Test
	public void TestingCongestionTaxRulesApiOnInsertTable_shouldSucceedWith200() throws Exception {
		try
		{
			CongestionTaxRulesModel congestionTaxRulesModel = new
					CongestionTaxRulesModel("Stockholm",true,
					8,
					13,
					18,
					13,
					8,
					13,
					18,
					13,
					8,
					0);
			HttpEntity<CongestionTaxRulesModel> request = new HttpEntity<>(congestionTaxRulesModel, new HttpHeaders());
			ResponseEntity<String> result = template.postForEntity("/api/v1/congestion_tax_rules/insert",request,String.class);
			assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
		}
		catch (Exception e)
		{
			System.out.println(e);
			assertEquals((Integer) null,1);
		}
	}


	@Test
	public void TestingCongestionTaxCalculationApiOnInvalidCity_shouldSucceedWith400() throws JsonProcessingException, IOException {

		List<VehiclesModel> vehiclesList = new ArrayList<>();
		LocalDateTime[] dates = {LocalDateTime.parse("2013-05-03 06:45:00",formatter),
				LocalDateTime.parse("2013-05-03 07:15:00",formatter),
				LocalDateTime.parse("2013-05-03 08:05:00",formatter)
		};
		vehiclesList.add(new VehiclesModel(123,"Car","Volvo",dates));
		TaxEstimationModel tx = new TaxEstimationModel("Stockholmm",vehiclesList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<TaxEstimationModel>(tx,null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnValidCity_shouldSucceedWith200() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {LocalDateTime.parse("2013-05-03 06:45:00",formatter),
				LocalDateTime.parse("2013-05-03 07:15:00",formatter),
				LocalDateTime.parse("2013-05-03 08:05:00",formatter)};
		vehiclesModelList.add(new VehiclesModel(123,"Car","Volvo",dates));
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx, null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnTaxExemptedValidVehicleType() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-07-01 06:45:00",formatter),
				LocalDateTime.parse("2013-07-11 17:25:00",formatter),
				LocalDateTime.parse("2013-07-12 08:09:10",formatter)
		};
		VehiclesModel v = new VehiclesModel(1234,"Diplomat_vehicles","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		TaxEstimatedResponse t1 = result.getBody().get(0);
		assertEquals(t1.getMessage(), "Tax_is_Exempted_For_Diplomat_vehicles");
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnNonTaxExemptedValidVehicleType() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-07-01 06:45:00",formatter),
				LocalDateTime.parse("2013-07-11 17:25:00",formatter),
				LocalDateTime.parse("2013-07-12 08:09:10",formatter)
		};
		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		assertEquals(result.getStatusCode(), HttpStatus.OK);
	}

//
	@Test
	public void TestingCongestionTaxCalculationApiOnValidTaxExemptedDatesSaturdaySunday() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-01-05 06:45:00",formatter),
				LocalDateTime.parse("2013-01-06 07:15:00",formatter),
		};
		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		TaxEstimatedResponse t1 = result.getBody().get(0);
		assertEquals(t1.getTaxExemptedDates().size(), 2);
	}
//
	@Test
	public void TestingCongestionTaxCalculationApiOnValidTaxExemptedJulyMonth() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-07-05 06:45:00",formatter),
				LocalDateTime.parse("2013-07-06 07:15:00",formatter),
				LocalDateTime.parse("2013-07-06 07:16:00",formatter),
		};
		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		TaxEstimatedResponse t1 = result.getBody().get(0);
		assertEquals(t1.getTaxExemptedDates().size(), 3);
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnValidTaxExemptedBeforeHolidayDate() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();

		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-12-24 06:45:00",formatter),
				LocalDateTime.parse("2013-04-30 07:15:00",formatter),
		};
		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		TaxEstimatedResponse t1 = result.getBody().get(0);
		assertEquals(t1.getTaxExemptedDates().size(), 2);
	}

//
	@Test
	public void TestingCongestionTaxCalculationApiOnTaxEstimationForTimingDetails() throws Exception {

		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-02-22 06:45:00",formatter),
		};
		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		Map<String,String> queryParams=new HashMap<>();
		queryParams.put("city_name","Stockholm");
		ResponseEntity<List<CongestionTaxRulesModel>> database_result = template.exchange("/api/v1/congestion_tax_rules/read", HttpMethod.GET,null,  new ParameterizedTypeReference<List<CongestionTaxRulesModel>>() {},queryParams);
		TaxEstimatedResponse t1 = result.getBody().get(0);
		CongestionTaxRulesModel cs = database_result.getBody().get(0);
		assertEquals(t1.getTax(), cs.getTax_details_on_time_630_to_659());
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnMaximumTaxEstimationForTimingDetails() throws Exception {
		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-01-15 06:05:00",formatter),
				LocalDateTime.parse("2013-01-15 06:45:00",formatter),
				LocalDateTime.parse("2013-01-15 07:45:00",formatter),
				LocalDateTime.parse("2013-01-15 08:45:00",formatter),
				LocalDateTime.parse("2013-01-15 09:45:00",formatter),
				LocalDateTime.parse("2013-01-15 10:45:00",formatter),
				LocalDateTime.parse("2013-01-15 15:45:00",formatter),
				LocalDateTime.parse("2013-01-15 16:45:00",formatter),
				LocalDateTime.parse("2013-01-15 17:45:00",formatter),
		};

		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		TaxEstimatedResponse t1 = result.getBody().get(0);
		assertEquals(t1.getTax(), 60);
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnMaximumTaxEstimationAsPerSingleChargeRule() throws Exception {
		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-02-22 06:45:00",formatter),
				LocalDateTime.parse("2013-02-22 06:10:00",formatter),
		};
		VehiclesModel v = new VehiclesModel(1234,"Bike","Volvo",dates);
		vehiclesModelList.add(v);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		Map<String,String> queryParams=new HashMap<>();
		queryParams.put("city_name","Stockholm");
		ResponseEntity<List<CongestionTaxRulesModel>> database_result = template.exchange("/api/v1/congestion_tax_rules/read", HttpMethod.GET,null,  new ParameterizedTypeReference<List<CongestionTaxRulesModel>>() {},queryParams);
		TaxEstimatedResponse t1 = result.getBody().get(0);
		CongestionTaxRulesModel cs = database_result.getBody().get(0);
		assertEquals(t1.getTax(), Math.max(cs.getTax_details_on_time_600_to_629(),cs.getTax_details_on_time_630_to_659()));
	}

	@Test
	public void TestingCongestionTaxCalculationApiOnMultipleVehicleObjects() throws Exception {
		List<VehiclesModel> vehiclesModelList = new ArrayList<>();
		LocalDateTime[] dates = {
				LocalDateTime.parse("2013-02-22 06:45:00",formatter),
		};
		VehiclesModel v1 = new VehiclesModel(1234,"Bike","Volvo",dates);
		VehiclesModel v2 = new VehiclesModel(1235,"Car","Volvo",dates);
		vehiclesModelList.add(v1);
		vehiclesModelList.add(v2);
		TaxEstimationModel tx = new TaxEstimationModel("Stockholm",vehiclesModelList);
		HttpEntity<TaxEstimationModel> request = new HttpEntity<>(tx,  null);
		ResponseEntity<List<TaxEstimatedResponse>> result = template.exchange("/api/v1/tax_calculation", HttpMethod.POST, request,  new ParameterizedTypeReference<List<TaxEstimatedResponse>>() {});
		assertEquals(result.getBody().size(),2);
	}
}

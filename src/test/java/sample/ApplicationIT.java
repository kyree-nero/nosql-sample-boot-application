package sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sample.entities.ForceSensitiveOrder;
import sample.entities.Person;
import sample.repository.ForceSensitiveOrderRepository;
import sample.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
public class ApplicationIT {
	
	@Autowired PersonRepository personRepository;
	@Autowired ForceSensitiveOrderRepository forceSensitiveOrderRepository;
	
	@Autowired MockMvc mockMvc;
	
	
	
	@Before public void before() {
		
		MockMvcClientHttpRequestFactory requestFactory = new MockMvcClientHttpRequestFactory(mockMvc);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        
		personRepository.deleteAll();
		forceSensitiveOrderRepository.deleteAll();
		
		Person lukeSkywalker = new Person();
		lukeSkywalker.setName("Luke Skywalker");
		lukeSkywalker = personRepository.save(lukeSkywalker);
		
		Person yoda = new Person();
		yoda.setName("yoda");
		yoda = personRepository.save(yoda);
		
		Person darthSideous = new Person();
		darthSideous.setName("darth Sideous");
		darthSideous = personRepository.save(darthSideous);
		
		Person anakinSkywalker = new Person();
		anakinSkywalker.setName("Anakin Skywalker");
		anakinSkywalker.setAlias("Darth Vader");
		anakinSkywalker = personRepository.save(anakinSkywalker);
		
		ForceSensitiveOrder jedi = new ForceSensitiveOrder();
		jedi.setName("jedi");
		jedi = forceSensitiveOrderRepository.save(jedi);
	
		ForceSensitiveOrder sith = new ForceSensitiveOrder();
		sith.setName("sith");
		sith = forceSensitiveOrderRepository.save(sith);
		
		lukeSkywalker.addTo(jedi);
		yoda.addTo(jedi);
		anakinSkywalker.addTo(jedi);
		anakinSkywalker.addTo(sith);
		darthSideous.addTo(sith);
		
		personRepository.save(lukeSkywalker);
		personRepository.save(yoda);
		personRepository.save(anakinSkywalker);
		
	}
	
	@Test public void testRepo() {
		Assert.assertEquals(4, personRepository.count());
		Assert.assertEquals(2, forceSensitiveOrderRepository.count());
	}
	
	
	@Test public void testFindJedi() {
		ForceSensitiveOrder group = forceSensitiveOrderRepository.findByName("jedi");
		Assert.assertNotNull(group);
		Assert.assertEquals(3, group.getMembers().size());
	}
	
	@Test public void testFindBeingsThatAreJediAndSith() {
		List<Person> people = personRepository.findX();
		Assert.assertEquals(1, people.size());
		Assert.assertEquals("Anakin Skywalker", people.get(0).getName());
	}
	
	@Test public void testForceSensitiveOrders() throws Exception {
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/orders", new Object[] {})
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				
		)
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		JsonNode node = new ObjectMapper().readTree(result.getResponse().getContentAsString());

		node = node.findValue("orders");
		
		final Map<String, Long> orderIdMap = new HashMap<String, Long>();
		
		
		
		node.forEach(
			c -> {
				final JsonNode name = c.findValue("name");
				JsonNode hrefs = c.findValue("href");
				
				String value = hrefs.textValue();
				
				while(value.indexOf("/") != -1) {
					value = value.substring((value.indexOf("/") + 1) , value.length());
				}
				
				orderIdMap.put(name.textValue(), new Long(value));
			}	
		);
		
		
		Long id = orderIdMap.get("jedi");
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/orders/"+id+"/members", new Object[] {})
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				
		)
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded", Matchers.notNullValue()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.person", Matchers.hasSize(3)));
		
		id = orderIdMap.get("sith");
		
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/orders/"+id+"/members", new Object[] {})
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				
		)
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded", Matchers.notNullValue()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.person", Matchers.hasSize(2)));
		
	}
	

	@Test public void addPerson() throws Exception{ 
		//add entity
		long existingCount = personRepository.count();
		
		Person testPerson = new Person();
		testPerson.setName("test person");
		
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(testPerson);
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/person", new Object[] {})
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonInString)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andReturn();
		System.out.println("-----");
		
		
		JsonNode node = new ObjectMapper().readTree(result.getResponse().getContentAsString());
		node = node.findValue("href");
		String value = node.textValue();
		String personUrl = value;
		while(value.indexOf("/") != -1) {
			value = value.substring((value.indexOf("/") + 1) , value.length());
		}
		Assert.assertNotNull(value);
		
		Assert.assertNotEquals(existingCount, personRepository.count());
		String personId = value;
				
		//find an existing order
		result = mockMvc.perform(
				MockMvcRequestBuilders.get("/orders", new Object[] {})
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				
		)
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		node = new ObjectMapper().readTree(result.getResponse().getContentAsString());
		node = node.findValue("href");
		value = node.textValue();
		String orderUrl = value;
		while(value.indexOf("/") != -1) {
			value = value.substring((value.indexOf("/") + 1) , value.length());
		}
		String orderId = value;
		
		//add relationship to order
		result = mockMvc.perform(
				MockMvcRequestBuilders.put("/person/"+personId+"/partOf", new Object[] {})
				.contentType("text/uri-list")
				.content(orderUrl)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		//add relationship to person
		result = mockMvc.perform(
				MockMvcRequestBuilders.put("/orders/"+orderId+"/members", new Object[] {})
				.contentType("text/uri-list")
				.content(personUrl)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		.andReturn();
		
		
		//assert relationships were added
		result = mockMvc.perform(
				MockMvcRequestBuilders.get("/person/"+personId+"/partOf", new Object[] {})
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded", Matchers.anything()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.orders[*]", Matchers.anything()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.orders[*].name", Matchers.anything()))
		.andReturn();
	
		result = mockMvc.perform(
				MockMvcRequestBuilders.get("/orders/"+orderId+"/members", new Object[] {})
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded", Matchers.anything()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.person[*]", Matchers.anything()))
		.andExpect(MockMvcResultMatchers.jsonPath("$._embedded.person[*].name", Matchers.anything()))
		.andReturn();
		
	}
	
	
}

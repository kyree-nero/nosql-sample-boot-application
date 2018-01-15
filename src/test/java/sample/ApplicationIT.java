package sample;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import sample.entities.ForceSensitiveOrder;
import sample.entities.Person;
import sample.repository.ForceSensitiveOrderRepository;
import sample.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@TestPropertySource("/application-test.properties")
public class ApplicationIT {
	@Autowired PersonRepository personRepository;
	@Autowired ForceSensitiveOrderRepository forceSensitiveOrderRepository;
	
	
	@Before public void before() {
		
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
	
	@Test public void test() {
		Assert.assertEquals(4, personRepository.count());
		Assert.assertEquals(2, forceSensitiveOrderRepository.count());
	}
	
	
	@Test public void findJedi() {
		ForceSensitiveOrder group = forceSensitiveOrderRepository.findByName("jedi");
		Assert.assertNotNull(group);
		Assert.assertEquals(3, group.getMembers().size());
	}
	
	@Test public void findBeingsThatAreJediAndSith() {
		List<Person> people = personRepository.findX();
		Assert.assertEquals(1, people.size());
		Assert.assertEquals("Anakin Skywalker", people.get(0).getName());
	}
}

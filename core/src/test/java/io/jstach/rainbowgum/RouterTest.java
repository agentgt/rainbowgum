package io.jstach.rainbowgum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.jstach.rainbowgum.LogRouter.RootRouter;

public class RouterTest {

	@Test
	void testSingleRouter() throws Exception {

		LevelResolver resolver = LevelResolver.of(Map.of("stuff", Level.INFO, "", Level.DEBUG));
		var publisher = new TestSyncPublisher();
		@SuppressWarnings("resource")
		var router = new SimpleRoute(publisher, resolver);
		var route = router.route("stuff.crap", Level.DEBUG);
		assertFalse(route.isEnabled());
		assertTrue(router.route("blah", Level.DEBUG).isEnabled());

	}

	@Test
	void testCompositeRouter() throws Exception {

		LevelResolver resolver1 = LevelResolver.of(Map.of("stuff", Level.INFO, "", Level.DEBUG));
		var publisher1 = new TestSyncPublisher();
		var router1 = new SimpleRoute(publisher1, resolver1);

		LevelResolver resolver2 = LevelResolver.of(Map.of("stuff", Level.DEBUG, "", Level.WARNING));
		var publisher2 = new TestSyncPublisher();
		var router2 = new SimpleRoute(publisher2, resolver2);

		var root = RootRouter.of(List.of(router1, router2), LevelResolver.of(Level.ERROR));

		var route = root.route("stuff", Level.DEBUG);

		assertTrue(route.isEnabled());

		if (route.isEnabled()) {
			route.log(EmptyLogEvent.DEBUG);
		}

		String results1 = publisher1.events.toString();
		String results2 = publisher2.events.toString();

		assertEquals("[DEBUG]", results1);
		assertEquals("[]", results2);

	}

}
/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaffer.operation.impl.get;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gaffer.data.element.Element;
import gaffer.data.elementdefinition.view.View;
import gaffer.exception.SerialisationException;
import gaffer.jsonserialisation.JSONSerialiser;
import gaffer.operation.GetOperation;
import gaffer.operation.OperationTest;
import gaffer.operation.data.EdgeSeed;
import gaffer.operation.data.ElementSeed;
import gaffer.operation.data.EntitySeed;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class GetElementsSeedTest implements OperationTest {
    private static final JSONSerialiser serialiser = new JSONSerialiser();

    @Test
    public void shouldSetSeedMatchingTypeToEquals() {
        // Given
        final ElementSeed elementSeed1 = new EntitySeed("identifier");

        // When
        final GetElementsSeed op = new GetElementsSeed(Collections.singletonList(elementSeed1));

        // Then
        assertEquals(GetOperation.SeedMatchingType.EQUAL, op.getSeedMatching());
    }

    @Test
    @Override
    public void shouldSerialiseAndDeserialiseOperation() throws SerialisationException {
        // Given
        final ElementSeed elementSeed1 = new EntitySeed("identifier");
        final ElementSeed elementSeed2 = new EdgeSeed("source2", "destination2", true);
        final GetElementsSeed op = new GetElementsSeed(Arrays.asList(elementSeed1, elementSeed2));

        // When
        byte[] json = serialiser.serialise(op, true);
        final GetElementsSeed deserialisedOp = serialiser.deserialise(json, GetElementsSeed.class);

        // Then
        final Iterator itr = deserialisedOp.getSeeds().iterator();
        assertEquals(elementSeed1, itr.next());
        assertEquals(elementSeed2, itr.next());
        assertFalse(itr.hasNext());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {

        GetElementsSeed<EntitySeed, Element> getElementsSeed = new GetElementsSeed.Builder<EntitySeed, Element>().addSeed(new EntitySeed("A"))
                .includeEdges(GetOperation.IncludeEdgeType.ALL)
                .includeEntities(false)
                .inOutType(GetOperation.IncludeIncomingOutgoingType.BOTH)
                .option("testOption", "true")
                .populateProperties(false)
                .summarise(true)
                .view(new View.Builder().edge("testEdgeGroup").build()).build();

        assertFalse(getElementsSeed.isIncludeEntities());
        assertTrue(getElementsSeed.isSummarise());
        assertFalse(getElementsSeed.isPopulateProperties());
        assertEquals(GetOperation.IncludeIncomingOutgoingType.BOTH, getElementsSeed.getIncludeIncomingOutGoing());
        assertEquals(GetOperation.IncludeEdgeType.ALL, getElementsSeed.getIncludeEdges());
        assertEquals("true", getElementsSeed.getOption("testOption"));
        assertTrue(getElementsSeed.isSummarise());
        assertNotNull(getElementsSeed.getView());
    }
}

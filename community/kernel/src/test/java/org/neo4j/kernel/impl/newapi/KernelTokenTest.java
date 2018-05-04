/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.newapi;

import org.junit.Test;

import org.neo4j.function.ThrowingAction;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.internal.kernel.api.exceptions.schema.IllegalTokenNameException;
import org.neo4j.kernel.impl.api.KernelTransactionImplementation;
import org.neo4j.kernel.impl.core.TokenHolder;
import org.neo4j.storageengine.api.StoreReadLayer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.neo4j.test.assertion.Assert.assertException;

public class KernelTokenTest
{
    private final StoreReadLayer storeReadLayer = mock( StoreReadLayer.class );
    private final KernelTransactionImplementation ktx = mock( KernelTransactionImplementation.class );
    private KernelToken token = new KernelToken( storeReadLayer, ktx );

    @Test
    public void labelGetOrCreateForName() throws Exception
    {
        assertIllegalToken( () -> token.labelGetOrCreateForName( null ) );
        assertIllegalToken( () -> token.labelGetOrCreateForName( "" ) );
        when( storeReadLayer.labelGetForName( "label" ) ).thenReturn( TokenHolder.NO_ID );
        when( storeReadLayer.labelGetOrCreateForName( "label" ) ).thenReturn( 42 );
        assertThat( token.labelGetOrCreateForName( "label" ), is( 42 ) );
    }

    @Test
    public void labelGetOrCreateForNames() throws Exception
    {
        assertIllegalToken( () -> token.labelGetOrCreateForNames( new String[]{null}, new int[1] ) );
        assertIllegalToken( () -> token.labelGetOrCreateForNames( new String[]{""}, new int[1] ) );
        String[] names = {"a", "b"};
        int[] ids = new int[2];
        when( storeReadLayer.labelGetForName( "a" ) ).thenReturn( TokenHolder.NO_ID );
        token.labelGetOrCreateForNames( names, ids );
        verify( storeReadLayer ).labelGetOrCreateForNames( names, ids );
    }

    @Test
    public void propertyKeyGetOrCreateForName() throws IllegalTokenNameException
    {
        assertIllegalToken( () -> token.propertyKeyGetOrCreateForName( null ) );
        assertIllegalToken( () -> token.propertyKeyGetOrCreateForName( "" ) );
        when( storeReadLayer.propertyKeyGetForName( "prop" ) ).thenReturn( TokenHolder.NO_ID );
        when( storeReadLayer.propertyKeyGetOrCreateForName( "prop" ) ).thenReturn( 42 );
        assertThat( token.propertyKeyGetOrCreateForName( "prop" ), is( 42 ) );
    }

    @Test
    public void relationshipTypeGetOrCreateForName() throws IllegalTokenNameException
    {
        assertIllegalToken( () -> token.relationshipTypeGetOrCreateForName( null ) );
        assertIllegalToken( () -> token.relationshipTypeGetOrCreateForName( "" ) );
        when( storeReadLayer.relationshipTypeGetForName( "rel" ) ).thenReturn( TokenHolder.NO_ID );
        when( storeReadLayer.relationshipTypeGetOrCreateForName( "rel" ) ).thenReturn( 42 );
        assertThat( token.relationshipTypeGetOrCreateForName( "rel" ), is( 42 ) );
    }

    private void assertIllegalToken( ThrowingAction<KernelException> f )
    {
        assertException( f, IllegalTokenNameException.class );
    }
}

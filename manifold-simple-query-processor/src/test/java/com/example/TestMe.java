package com.example;

import com.example.query.api.Query;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static com.example.Gender.female;
import static com.example.Gender.male;
import static org.junit.Assert.*;


public class TestMe
{
  private ArrayList<Person> data = new ArrayList<Person>()
  {{
    add( new Person( "Skipper", 4, male, false ) );
    add( new Person( "Pokechop", 3, male, false ) );
    add( new Person( "Smokey", 40, male, true ) );
    add( new Person( "Skeeter", 39, female, true ) );
  }};

  @Test
  public void scratchTest()
  {
    Query<Person> query0 = Person.query( ( p, q ) -> q
      .orderBy( p.age ) );
    System.out.println( query0.run( data ) );
    System.out.println( query0 );

    Query<Person> query = Person.query( (p, q) -> q
      .where( p.age >= 39 && p.gender != male )
      .orderBy( p.name ) );
    Iterable<Person> results = query.run( data );
    Iterator<Person> iterator = results.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "Skeeter", iterator.next().name );
    assertFalse( iterator.hasNext() );
    System.out.println( results );
    System.out.println( query );

    Query<Person> query2 = Person.query( ( p, q ) -> q
      .where( !p.deceased )
      .orderBy( p.age ) );
    System.out.println( query2.run( data ) );
    System.out.println( query2 );
  }

  @Test
  public void functionCallTest()
  {
    Query<Person> query = Person.query( (p, q) -> q
      .where( p.age >= 0 && p.name.contains( "S" ) ) // using contains() method
      .orderBy( p.name ) );
    Iterable<Person> results = query.run( data );
    Iterator<Person> iterator = results.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "Skeeter", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Skipper", iterator.next().name );
    assertTrue( iterator.hasNext() );
    assertEquals( "Smokey", iterator.next().name );
    System.out.println( results );
    System.out.println( query );
  }
}
